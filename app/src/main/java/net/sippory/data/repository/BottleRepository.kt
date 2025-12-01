package net.sippory.data.repository

import android.content.Context
import kotlinx.coroutines.flow.Flow
import net.sippory.data.dao.BottleDao
import net.sippory.data.entity.BottleEntity
import net.sippory.utils.ImageFileManager

class BottleRepository(
    private val bottleDao: BottleDao,
    private val context: Context,
) {
    fun getAllBottles(): Flow<List<BottleEntity>> = bottleDao.getAllBottles()

    suspend fun getBottleById(id: Int): BottleEntity? = bottleDao.getBottleById(id)

    fun getBottlesByType(type: String): Flow<List<BottleEntity>> = bottleDao.getBottlesByType(type)

    fun getBottlesByRating(minRating: Float): Flow<List<BottleEntity>> = bottleDao.getBottlesByRating(minRating)

    fun searchBottles(query: String): Flow<List<BottleEntity>> = bottleDao.searchBottles(query)

    // 위시리스트 관련 메서드
    fun getWishlistBottles(): Flow<List<BottleEntity>> = bottleDao.getWishlistBottles()

    fun getOwnedBottles(): Flow<List<BottleEntity>> = bottleDao.getOwnedBottles()

    suspend fun updateWishlistStatus(
        id: Int,
        isWishlist: Boolean,
    ) = bottleDao.updateWishlistStatus(id, isWishlist)

    suspend fun incrementDrinkCount(id: Int) = bottleDao.incrementDrinkCount(id, System.currentTimeMillis())

    suspend fun insertBottle(bottle: BottleEntity): Long = bottleDao.insertBottle(bottle)

    suspend fun updateBottle(bottle: BottleEntity) = bottleDao.updateBottle(bottle)

    suspend fun deleteBottle(bottle: BottleEntity) {
        // 이미지 파일 삭제
        bottle.photoUri?.let {
            ImageFileManager.deleteImage(context, it)
        }
        bottleDao.deleteBottle(bottle)
    }

    suspend fun deleteBottleById(id: Int) {
        // 이미지 파일 삭제를 위해 먼저 병 정보를 가져옴
        getBottleById(id)?.let { bottle ->
            bottle.photoUri?.let {
                ImageFileManager.deleteImage(context, it)
            }
        }
        bottleDao.deleteBottleById(id)
    }
}
