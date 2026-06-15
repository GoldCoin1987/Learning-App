package com.studyforge.app.data

import com.studyforge.app.domain.Sm2
import com.studyforge.app.model.CatalogIndex
import com.studyforge.app.model.Item
import com.studyforge.app.model.Lesson
import com.studyforge.app.model.Pack
import com.studyforge.app.model.Subtopic
import androidx.room.withTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

/** A study item with display context, ready to grade back to storage. */
data class StudyCard(
    val packId: String,
    val item: Item,
    val subtopicTitle: String,
    val lessonTitle: String,
)

private val json = Json { ignoreUnknownKeys = true }

/** Downloads the catalog + packs and imports them into the local database. */
class PackRepository(
    private val db: StudyDatabase,
    private val catalogUrl: () -> String,
    private val http: OkHttpClient = OkHttpClient(),
) {
    fun observePacks() = db.packDao().observePacks()

    suspend fun fetchCatalog(): CatalogIndex = withContext(Dispatchers.IO) {
        json.decodeFromString(CatalogIndex.serializer(), httpGet(catalogUrl()))
    }

    suspend fun installFromUrl(url: String, todayEpochDay: Long) = withContext(Dispatchers.IO) {
        val pack = json.decodeFromString(Pack.serializer(), httpGet(url))
        install(pack, todayEpochDay)
    }

    suspend fun install(pack: Pack, todayEpochDay: Long) = db.withTransaction {
        db.packDao().upsert(
            PackEntity(
                id = pack.id,
                title = pack.title,
                version = pack.version,
                description = pack.description,
                requiresCsv = pack.requires.joinToString(","),
                installedAt = todayEpochDay,
            )
        )
        for (subtopic in pack.subtopics) {
            for (lesson in subtopic.lessons) {
                for (item in lesson.items) {
                    val fresh = item.toEntity(pack.id, subtopic, lesson, todayEpochDay)
                    val existing = db.itemDao().getItem(pack.id, item.id)
                    // Update content on re-import, but carry forward SRS progress for known items.
                    val merged = if (existing != null) fresh.copy(
                        ef = existing.ef,
                        intervalDays = existing.intervalDays,
                        reps = existing.reps,
                        dueEpochDay = existing.dueEpochDay,
                        lastReviewedEpochDay = existing.lastReviewedEpochDay,
                        introduced = existing.introduced,
                    ) else fresh
                    db.itemDao().upsert(merged)
                }
            }
        }
    }

    private fun httpGet(url: String): String {
        http.newCall(Request.Builder().url(url).build()).execute().use { resp ->
            if (!resp.isSuccessful) error("HTTP ${resp.code} for $url")
            return resp.body?.string() ?: error("Empty response body for $url")
        }
    }
}

/** Builds (optionally scoped) study sessions and applies grades. */
class StudyRepository(private val db: StudyDatabase) {

    fun observeDueCount(today: Long) = db.itemDao().observeDueCount(today)

    suspend fun subtopics(packId: String, today: Long): List<SubtopicSummary> =
        db.itemDao().subtopicSummaries(packId, today)

    /**
     * Session = due reviews first (SM-2), then new items introduced in easy→hard order.
     * [packId]/[subtopicId] null = study everything; set them to scope to a topic or sub-topic.
     */
    suspend fun buildSession(
        today: Long,
        packId: String? = null,
        subtopicId: String? = null,
        newLimit: Int = 20,
        reviewLimit: Int = 80,
    ): List<StudyCard> {
        val due = db.itemDao().dueItemsScoped(today, packId, subtopicId, reviewLimit)
        val fresh = db.itemDao().newItemsScoped(packId, subtopicId, newLimit)
        return (due + fresh).map { it.toStudyCard() }
    }

    suspend fun grade(packId: String, itemId: String, grade: Int, today: Long) {
        val entity = db.itemDao().getItem(packId, itemId) ?: return
        db.itemDao().update(Sm2.review(entity, grade, today))
    }
}

private fun Item.toEntity(packId: String, subtopic: Subtopic, lesson: Lesson, todayEpochDay: Long): ItemEntity =
    ItemEntity(
        packId = packId,
        itemId = id,
        subtopicId = subtopic.id,
        subtopicTitle = subtopic.title,
        subtopicOrder = subtopic.order,
        lessonId = lesson.id,
        lessonTitle = lesson.title,
        lessonOrder = lesson.order,
        difficulty = lesson.difficulty,
        seq = subtopic.order * 10000 + lesson.order * 100 + lesson.difficulty,
        type = type.name,
        payloadJson = json.encodeToString(Item.serializer(), this),
        ef = 2.5,
        intervalDays = 0,
        reps = 0,
        dueEpochDay = todayEpochDay,
        lastReviewedEpochDay = 0,
        introduced = false,
    )

private fun ItemEntity.toStudyCard(): StudyCard = StudyCard(
    packId = packId,
    item = json.decodeFromString(Item.serializer(), payloadJson),
    subtopicTitle = subtopicTitle,
    lessonTitle = lessonTitle,
)
