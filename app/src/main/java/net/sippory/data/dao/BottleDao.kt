package net.sippory.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import net.sippory.data.entity.BottleEntity

@Dao
interface BottleDao {
    @Query("SELECT * FROM bottles ORDER BY createdAt DESC")
    fun getAllBottles(): Flow<List<BottleEntity>>

    @Query("SELECT * FROM bottles WHERE id = :id")
    suspend fun getBottleById(id: Int): BottleEntity?

    @Query("SELECT * FROM bottles WHERE type = :type ORDER BY createdAt DESC")
    fun getBottlesByType(type: String): Flow<List<BottleEntity>>

    @Query("SELECT * FROM bottles WHERE rating >= :minRating ORDER BY rating DESC")
    fun getBottlesByRating(minRating: Float): Flow<List<BottleEntity>>

    @Query("SELECT * FROM bottles WHERE name LIKE '%' || :query || '%' OR type LIKE '%' || :query || '%'")
    fun searchBottles(query: String): Flow<List<BottleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBottle(bottle: BottleEntity): Long

    @Update
    suspend fun updateBottle(bottle: BottleEntity)

    @Delete
    suspend fun deleteBottle(bottle: BottleEntity)

    @Query("DELETE FROM bottles WHERE id = :id")
    suspend fun deleteBottleById(id: Int)
}
