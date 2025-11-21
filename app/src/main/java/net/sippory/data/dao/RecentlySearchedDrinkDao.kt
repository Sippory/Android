package net.sippory.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import net.sippory.data.entity.RecentlySearchedDrinkEntity

@Dao
interface RecentlySearchedDrinkDao {
    @Query("SELECT * FROM recently_searched_drinks ORDER BY timestamp DESC")
    fun getAllRecentlySearchedDrinks(): Flow<List<RecentlySearchedDrinkEntity>>

    @Upsert
    suspend fun upsertRecentlySearchedDrink(drink: RecentlySearchedDrinkEntity)

    @Delete
    suspend fun deleteRecentlySearchedDrink(drink: RecentlySearchedDrinkEntity)
}
