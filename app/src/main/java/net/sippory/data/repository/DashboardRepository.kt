package net.sippory.data.repository

import kotlinx.coroutines.flow.Flow
import net.sippory.data.dao.DashboardDao

class DashboardRepository(
    private val dashboardDao: DashboardDao,
) {
    fun getTypeRanking(): Flow<List<DashboardDao.TypeRanking>> = dashboardDao.getTypeRanking()

    fun getAbvRanking(): Flow<List<DashboardDao.AbvRanking>> = dashboardDao.getAbvRanking()

    fun getAverageRatingPerType(): Flow<List<DashboardDao.TypeRating>> = dashboardDao.getAverageRatingPerType()

    fun getMostConsumedBottleRanking(): Flow<List<DashboardDao.BottleRanking>> =
        dashboardDao.getMostConsumedBottleRanking()
}
