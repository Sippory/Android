package net.sippory.data.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DashboardDao {

    // 1. 가장 많이 기록된 주류 타입 랭킹 (이름과 횟수)
    @Query("SELECT type, COUNT(type) as count FROM bottles GROUP BY type ORDER BY count DESC")
    fun getTypeRanking(): Flow<List<TypeRanking>>

    // 2. 가장 많이 기록된 주류 도수 랭킹 (도수와 횟수)
    @Query("SELECT abv, COUNT(abv) as count FROM bottles WHERE abv IS NOT NULL GROUP BY abv ORDER BY count DESC")
    fun getAbvRanking(): Flow<List<AbvRanking>>

    // 3. 타입별 평균 평점 랭킹 (타입과 평균 평점)
    @Query("SELECT type, AVG(rating) as averageRating FROM bottles GROUP BY type ORDER BY averageRating DESC")
    fun getAverageRatingPerType(): Flow<List<TypeRating>>

    // 4. 가장 많이 마신 술(이름 기준) 랭킹
    @Query("SELECT name, COUNT(name) as count FROM bottles GROUP BY name ORDER BY count DESC")
    fun getMostConsumedBottleRanking(): Flow<List<BottleRanking>>

    // 데이터 클래스는 쿼리 결과에 맞춰 별도 파일이나 DAO 파일 내에 정의합니다.
    data class TypeRanking(val type: String, val count: Int)
    data class AbvRanking(val abv: Float, val count: Int)
    data class TypeRating(val type: String, val averageRating: Float)
    data class BottleRanking(val name: String, val count: Int)
}
