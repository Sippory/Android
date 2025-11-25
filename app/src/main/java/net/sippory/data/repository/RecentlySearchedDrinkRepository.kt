package net.sippory.data.repository

import net.sippory.data.dao.RecentlySearchedDrinkDao
import net.sippory.data.entity.RecentlySearchedDrinkEntity

class RecentlySearchedDrinkRepository(private val recentlySearchedDrinkDao: RecentlySearchedDrinkDao) {
    fun getAllRecentlySearchedDrinks() = recentlySearchedDrinkDao.getAllRecentlySearchedDrinks()

    suspend fun addOrUpdateRecentlySearchedDrink(drink: RecentlySearchedDrinkEntity) {
        val newRecentlySearchedDrink = drink.copy(timestamp = System.currentTimeMillis())
        recentlySearchedDrinkDao.upsertRecentlySearchedDrink(newRecentlySearchedDrink)
    }

    suspend fun removeRecentlySearchedDrink(drink: RecentlySearchedDrinkEntity) {
        recentlySearchedDrinkDao.deleteRecentlySearchedDrink(drink)
    }
}
