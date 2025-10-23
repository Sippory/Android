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

    suspend fun insertBottle(bottle: BottleEntity): Long = bottleDao.insertBottle(bottle)

    suspend fun updateBottle(bottle: BottleEntity) = bottleDao.updateBottle(bottle)

    suspend fun deleteBottle(bottle: BottleEntity) = bottleDao.deleteBottle(bottle)

    suspend fun deleteBottleById(id: Int) = bottleDao.deleteBottleById(id)
}
