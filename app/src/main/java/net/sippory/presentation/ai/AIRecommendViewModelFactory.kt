package net.sippory.presentation.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.sippory.data.repository.BottleRepository

class AIRecommendViewModelFactory(
    private val repository: BottleRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AIRecommendViewModel(repository) as T
    }
}
