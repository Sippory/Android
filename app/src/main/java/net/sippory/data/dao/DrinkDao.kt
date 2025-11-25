package net.sippory.data.dao

import net.sippory.data.entity.DrinkEntity

interface DrinkDao {
    suspend fun getAllDrinks(): List<DrinkEntity>

    suspend fun getDrinksByName(searchTerm: String): List<DrinkEntity>
}
