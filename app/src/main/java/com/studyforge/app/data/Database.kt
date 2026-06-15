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
    val curriculumIndex: String,
    val description: String,
    val requiresCsv: String,
    val installedAt: Long,
)

/**
 * One study item plus its embedded SM-2 scheduling state.
 * Keyed on (packId, itemId) so re-importing a pack can IGNORE existing rows and preserve progress.
 */
@Entity(tableName = "items", primaryKeys = ["packId", "itemId"])
data class ItemEntity(
    val packId: String,
    val itemId: String,
    val type: String,
    val difficulty: Int,
    val topic: String,
    val requiresCsv: String,
    val payloadJson: String,
    // SRS state
    val ef: Double,
    val intervalDays: Int,
    val reps: Int,
    val dueEpochDay: Long,
    val lastReviewedEpochDay: Long,
    val introduced: Boolean,
)

@Dao
interface PackDao {
    @Query("SELECT * FROM packs ORDER BY curriculumIndex, title")
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
    /** IGNORE preserves existing SRS state when a newer pack version is re-imported. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<ItemEntity>)

    @Update
    suspend fun update(item: ItemEntity)

    @Query("DELETE FROM items WHERE packId = :packId")
    suspend fun deleteByPack(packId: String)

    @Query("SELECT * FROM items WHERE packId = :packId AND itemId = :itemId")
    suspend fun getItem(packId: String, itemId: String): ItemEntity?

    @Query("SELECT COUNT(*) FROM items WHERE introduced = 1 AND dueEpochDay <= :today")
    fun observeDueCount(today: Long): Flow<Int>

    @Query(
        "SELECT * FROM items WHERE introduced = 1 AND dueEpochDay <= :today " +
            "ORDER BY difficulty, dueEpochDay LIMIT :limit"
    )
    suspend fun dueItems(today: Long, limit: Int): List<ItemEntity>

    /** Lowest difficulty tier in this pack that still has un-introduced items. */
    @Query("SELECT MIN(difficulty) FROM items WHERE packId = :packId AND introduced = 0")
    suspend fun lowestUnintroducedTier(packId: String): Int?

    /** Count of not-yet-attempted items below a tier — if > 0, the tier stays locked. */
    @Query("SELECT COUNT(*) FROM items WHERE packId = :packId AND difficulty < :tier AND reps = 0")
    suspend fun unmasteredBelowTier(packId: String, tier: Int): Int

    @Query(
        "SELECT * FROM items WHERE introduced = 0 AND packId = :packId AND difficulty = :tier " +
            "ORDER BY itemId LIMIT :limit"
    )
    suspend fun newItemsInTier(packId: String, tier: Int, limit: Int): List<ItemEntity>
}

@Database(entities = [PackEntity::class, ItemEntity::class], version = 1, exportSchema = false)
abstract class StudyDatabase : RoomDatabase() {
    abstract fun packDao(): PackDao
    abstract fun itemDao(): ItemDao
}
