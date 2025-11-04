package net.sippory.data.repository

import kotlinx.coroutines.flow.Flow
import net.sippory.data.dao.BottleDao
import net.sippory.data.entity.BottleEntity

class BottleRepository(private val bottleDao: BottleDao) {

    fun getAllBottles(): Flow<List<BottleEntity>> = bottleDao.getAllBottles()

    suspend fun getBottleById(id: Int): BottleEntity? = bottleDao.getBottleById(id)

    fun getBottlesByType(type: String): Flow<List<BottleEntity>> = bottleDao.getBottlesByType(type)

    fun getBottlesByRating(minRating: Float): Flow<List<BottleEntity>> = bottleDao.getBottlesByRating(minRating)

    fun searchBottles(query: String): Flow<List<BottleEntity>> = bottleDao.searchBottles(query)

    // 위시리스트 관련 메서드
    fun getWishlistBottles(): Flow<List<BottleEntity>> = bottleDao.getWishlistBottles()

    fun getOwnedBottles(): Flow<List<BottleEntity>> = bottleDao.getOwnedBottles()

    suspend fun updateWishlistStatus(id: Int, isWishlist: Boolean) = bottleDao.updateWishlistStatus(id, isWishlist)

    suspend fun incrementDrinkCount(id: Int) = bottleDao.incrementDrinkCount(id)

    suspend fun insertBottle(bottle: BottleEntity): Long = bottleDao.insertBottle(bottle)

    suspend fun updateBottle(bottle: BottleEntity) = bottleDao.updateBottle(bottle)

    suspend fun deleteBottle(bottle: BottleEntity) = bottleDao.deleteBottle(bottle)

    suspend fun deleteBottleById(id: Int) = bottleDao.deleteBottleById(id)
}
