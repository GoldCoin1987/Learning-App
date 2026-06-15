package com.studyforge.app.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "packs")
data class PackEntity(
    @PrimaryKey val id: String,
    val title: String,
    val version: String,
    val description: String,
    val requiresCsv: String,
    val installedAt: Long,
)

/**
 * One study item, denormalized with its sub-topic/lesson context and embedded SM-2 state.
 * Keyed on (packId, itemId) so re-importing a pack IGNOREs existing rows and preserves progress.
 * [seq] = subtopicOrder*10000 + lessonOrder*100 + difficulty — the easy→hard progression order.
 */
@Entity(tableName = "items", primaryKeys = ["packId", "itemId"])
data class ItemEntity(
    val packId: String,
    val itemId: String,
    val subtopicId: String,
    val subtopicTitle: String,
    val subtopicOrder: Int,
    val lessonId: String,
    val lessonTitle: String,
    val lessonOrder: Int,
    val difficulty: Int,
    val seq: Int,
    val type: String,
    val payloadJson: String,
    // SRS state
    val ef: Double,
    val intervalDays: Int,
    val reps: Int,
    val dueEpochDay: Long,
    val lastReviewedEpochDay: Long,
    val introduced: Boolean,
)

/** Aggregated counts for a sub-topic, for the browse screen. */
data class SubtopicSummary(
    val subtopicId: String,
    val subtopicTitle: String,
    val subtopicOrder: Int,
    val total: Int,
    val due: Int,
    val newCount: Int,
)

@Dao
interface PackDao {
    @Query("SELECT * FROM packs ORDER BY title")
    fun observePacks(): Flow<List<PackEntity>>

    @Query("SELECT id FROM packs")
    suspend fun installedIds(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(pack: PackEntity)

    @Query("DELETE FROM packs WHERE id = :id")
    suspend fun delete(id: String)
}

@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<ItemEntity>)

    /** Insert-or-replace; the repository carries forward SRS state for existing rows. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: ItemEntity)

    @Update
    suspend fun update(item: ItemEntity)

    @Query("DELETE FROM items WHERE packId = :packId")
    suspend fun deleteByPack(packId: String)

    @Query("SELECT * FROM items WHERE packId = :packId AND itemId = :itemId")
    suspend fun getItem(packId: String, itemId: String): ItemEntity?

    @Query("SELECT COUNT(*) FROM items WHERE introduced = 1 AND dueEpochDay <= :today")
    fun observeDueCount(today: Long): Flow<Int>

    /** Due reviews in scope (null packId/subtopicId = unrestricted). */
    @Query(
        "SELECT * FROM items " +
            "WHERE introduced = 1 AND dueEpochDay <= :today " +
            "AND (:packId IS NULL OR packId = :packId) " +
            "AND (:subtopicId IS NULL OR subtopicId = :subtopicId) " +
            "ORDER BY seq, dueEpochDay LIMIT :limit"
    )
    suspend fun dueItemsScoped(today: Long, packId: String?, subtopicId: String?, limit: Int): List<ItemEntity>

    /** New (never-introduced) items in scope, in easy→hard progression order. */
    @Query(
        "SELECT * FROM items " +
            "WHERE introduced = 0 " +
            "AND (:packId IS NULL OR packId = :packId) " +
            "AND (:subtopicId IS NULL OR subtopicId = :subtopicId) " +
            "ORDER BY seq, itemId LIMIT :limit"
    )
    suspend fun newItemsScoped(packId: String?, subtopicId: String?, limit: Int): List<ItemEntity>

    @Query(
        "SELECT subtopicId, subtopicTitle, subtopicOrder, " +
            "COUNT(*) AS total, " +
            "SUM(CASE WHEN introduced = 1 AND dueEpochDay <= :today THEN 1 ELSE 0 END) AS due, " +
            "SUM(CASE WHEN introduced = 0 THEN 1 ELSE 0 END) AS newCount " +
            "FROM items WHERE packId = :packId " +
            "GROUP BY subtopicId, subtopicTitle, subtopicOrder ORDER BY subtopicOrder"
    )
    suspend fun subtopicSummaries(packId: String, today: Long): List<SubtopicSummary>
}

@Database(entities = [PackEntity::class, ItemEntity::class], version = 2, exportSchema = false)
abstract class StudyDatabase : RoomDatabase() {
    abstract fun packDao(): PackDao
    abstract fun itemDao(): ItemDao
}
