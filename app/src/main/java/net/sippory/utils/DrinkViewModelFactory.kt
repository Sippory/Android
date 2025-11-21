package net.sippory.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.sippory.data.repository.DrinkRepository
import net.sippory.data.repository.RecentlySearchedDrinkRepository
import net.sippory.presentation.search.DrinkSearchViewModel

class DrinkViewModelFactory(
    private val drinkRepository: DrinkRepository,
    private val recentlySearchedDrinkRepository: RecentlySearchedDrinkRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(DrinkSearchViewModel::class.java) -> {
                DrinkSearchViewModel(drinkRepository, recentlySearchedDrinkRepository) as T
            }
            else -> {
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}
