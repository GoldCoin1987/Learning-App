package com.studyforge.app.data

import com.studyforge.app.domain.Sm2
import com.studyforge.app.model.CatalogIndex
import com.studyforge.app.model.Item
import com.studyforge.app.model.Pack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

/** A study item paired with its owning pack id, so it can be graded back to storage. */
data class StudyCard(val packId: String, val item: Item)

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

    suspend fun install(pack: Pack, todayEpochDay: Long) {
        db.packDao().upsert(
            PackEntity(
                id = pack.id,
                title = pack.title,
                version = pack.version,
                curriculumIndex = pack.curriculumIndex,
                description = pack.description,
                requiresCsv = pack.requires.joinToString(","),
                installedAt = todayEpochDay,
            )
        )
        db.itemDao().insertAll(pack.items.map { it.toEntity(pack.id, todayEpochDay) })
    }

    private fun httpGet(url: String): String {
        http.newCall(Request.Builder().url(url).build()).execute().use { resp ->
            if (!resp.isSuccessful) error("HTTP ${resp.code} for $url")
            return resp.body?.string() ?: error("Empty response body for $url")
        }
    }
}

/** Builds daily study sessions and applies grades. */
class StudyRepository(private val db: StudyDatabase) {

    fun observeDueCount(today: Long) = db.itemDao().observeDueCount(today)

    /**
     * Session = all due reviews first (SM-2), then a capped number of NEW items drawn only from
     * unlocked difficulty tiers. A tier is unlocked once every lower-tier item has been attempted.
     */
    suspend fun buildSession(today: Long, newLimit: Int = 10, reviewLimit: Int = 50): List<StudyCard> {
        val due = db.itemDao().dueItems(today, reviewLimit).map { StudyCard(it.packId, it.toItem()) }

        val fresh = mutableListOf<StudyCard>()
        for (packId in db.packDao().installedIds()) {
            if (fresh.size >= newLimit) break
            val tier = db.itemDao().lowestUnintroducedTier(packId) ?: continue
            if (db.itemDao().unmasteredBelowTier(packId, tier) > 0) continue // tier still locked
            val take = newLimit - fresh.size
            fresh += db.itemDao().newItemsInTier(packId, tier, take).map { StudyCard(it.packId, it.toItem()) }
        }
        return due + fresh
    }

    suspend fun grade(packId: String, itemId: String, grade: Int, today: Long) {
        val entity = db.itemDao().getItem(packId, itemId) ?: return
        db.itemDao().update(Sm2.review(entity, grade, today))
    }
}

private fun Item.toEntity(packId: String, todayEpochDay: Long): ItemEntity = ItemEntity(
    packId = packId,
    itemId = id,
    type = type.name,
    difficulty = difficulty,
    topic = topic,
    requiresCsv = requires.joinToString(","),
    payloadJson = json.encodeToString(Item.serializer(), this),
    ef = 2.5,
    intervalDays = 0,
    reps = 0,
    dueEpochDay = todayEpochDay,
    lastReviewedEpochDay = 0,
    introduced = false,
)

private fun ItemEntity.toItem(): Item = json.decodeFromString(Item.serializer(), payloadJson)
