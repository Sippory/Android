package net.sippory.data.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DashboardDao {

    // 1. 가장 많이 기록된 BottleTypes 랭킹 (타입과 "마신 횟수" 합)
    @Query("""
        SELECT type, SUM(drinkCount) AS count
        FROM bottles
        WHERE drinkCount > 0
        GROUP BY type
        ORDER BY count DESC
    """)
    fun getTypeRanking(): Flow<List<TypeRanking>>

    // 2. 가장 많이 기록된 abv 랭킹 (10도 단위 구간 시작값과 마신 횟수 합)
    // abv 컬럼은 "구간 시작값" (예: 0.0, 10.0, 20.0 ...)
    @Query("""
        SELECT (CAST(abv / 10 AS INT) * 10) AS abv,
               SUM(drinkCount) AS count
        FROM bottles
        WHERE abv IS NOT NULL AND drinkCount > 0
        GROUP BY CAST(abv / 10 AS INT)
        ORDER BY count DESC
    """)
    fun getAbvRanking(): Flow<List<AbvRanking>>

    // 3. BottleTypes별 평균 rating 랭킹 (기록된 병만)
    @Query("""
        SELECT type, AVG(rating) AS averageRating
        FROM bottles
        WHERE drinkCount > 0
        GROUP BY type
        ORDER BY averageRating DESC
    """)
    fun getAverageRatingPerType(): Flow<List<TypeRating>>

    // 4. 가장 많이 마신 술(name 기준) 랭킹 -> drinkCount 합 높은 순
    @Query("""
        SELECT name, SUM(drinkCount) AS count
        FROM bottles
        WHERE drinkCount > 0
        GROUP BY name
        ORDER BY count DESC
    """)
    fun getMostConsumedBottleRanking(): Flow<List<BottleRanking>>

    data class TypeRanking(val type: String, val count: Int)
    data class AbvRanking(val abv: Float, val count: Int)
    data class TypeRating(val type: String, val averageRating: Float)
    data class BottleRanking(val name: String, val count: Int)
}

