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

    // 위시리스트 관련 쿼리
    @Query("SELECT * FROM bottles WHERE isWishlist = 1 ORDER BY createdAt DESC")
    fun getWishlistBottles(): Flow<List<BottleEntity>>

    @Query("SELECT * FROM bottles WHERE isWishlist = 0 ORDER BY createdAt DESC")
    fun getOwnedBottles(): Flow<List<BottleEntity>>

    @Query("UPDATE bottles SET isWishlist = :isWishlist WHERE id = :id")
    suspend fun updateWishlistStatus(
        id: Int,
        isWishlist: Boolean,
    )

    @Query("UPDATE bottles SET drinkCount = drinkCount + 1, updatedAt = :updatedAt WHERE id = :id")
    suspend fun incrementDrinkCount(
        id: Int,
        updatedAt: Long,
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBottle(bottle: BottleEntity): Long

    @Update
    suspend fun updateBottle(bottle: BottleEntity)

    @Delete
    suspend fun deleteBottle(bottle: BottleEntity)

    @Query("DELETE FROM bottles WHERE id = :id")
    suspend fun deleteBottleById(id: Int)
}
