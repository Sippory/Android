package net.sippory.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.sippory.data.repository.BottleRepository
import net.sippory.presentation.add.AddBottleViewModel
import net.sippory.presentation.detail.DetailViewModel
import net.sippory.presentation.home.HomeViewModel

class BottleViewModelFactory(
    private val repository: BottleRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(repository) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(repository) as T
            }
            modelClass.isAssignableFrom(AddBottleViewModel::class.java) -> {
                AddBottleViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
