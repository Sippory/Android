package net.sippory.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.sippory.data.entity.BottleEntity
import net.sippory.data.repository.BottleRepository

data class HomeUiState(
    val bottles: List<BottleEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedFilter: BottleFilter = BottleFilter.All,
)

sealed class BottleFilter {
    object All : BottleFilter()

    data class ByType(val type: String) : BottleFilter()

    data class ByRating(val minRating: Float) : BottleFilter()

    object Wishlist : BottleFilter()

    object Owned : BottleFilter()
}

class HomeViewModel(private val repository: BottleRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadBottles()
    }

    private fun loadBottles() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.getAllBottles().collect { bottles ->
                    _uiState.update {
                        it.copy(
                            bottles = filterBottles(bottles, it.selectedFilter, it.searchQuery),
                            isLoading = false,
                            error = null,
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message,
                    )
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update {
            val currentBottles = it.bottles
            it.copy(
                searchQuery = query,
                bottles = filterBottles(currentBottles, it.selectedFilter, query),
            )
        }
    }

    fun onFilterChange(filter: BottleFilter) {
        viewModelScope.launch {
            _uiState.update { it.copy(selectedFilter = filter, isLoading = true) }
            try {
                val flow =
                    when (filter) {
                        is BottleFilter.All -> repository.getAllBottles()
                        is BottleFilter.ByType -> repository.getBottlesByType(filter.type)
                        is BottleFilter.ByRating -> repository.getBottlesByRating(filter.minRating)
                        is BottleFilter.Wishlist -> repository.getWishlistBottles()
                        is BottleFilter.Owned -> repository.getOwnedBottles()
                    }
                flow.collect { bottles ->
                    _uiState.update {
                        it.copy(
                            bottles = filterBottles(bottles, filter, it.searchQuery),
                            isLoading = false,
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message,
                    )
                }
            }
        }
    }

    fun deleteBottle(bottle: BottleEntity) {
        viewModelScope.launch {
            try {
                repository.deleteBottle(bottle)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun toggleWishlist(
        bottleId: Int,
        isWishlist: Boolean,
    ) {
        viewModelScope.launch {
            try {
                repository.updateWishlistStatus(bottleId, isWishlist)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun recordDrink(bottleId: Int) {
        viewModelScope.launch {
            try {
                repository.incrementDrinkCount(bottleId)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    private fun filterBottles(
        bottles: List<BottleEntity>,
        filter: BottleFilter,
        searchQuery: String,
    ): List<BottleEntity> {
        var filtered = bottles

        // Apply filter
        filtered =
            when (filter) {
                is BottleFilter.All -> filtered
                is BottleFilter.ByType -> filtered.filter { it.type == filter.type }
                is BottleFilter.ByRating -> filtered.filter { it.rating >= filter.minRating }
                is BottleFilter.Wishlist -> filtered.filter { it.isWishlist }
                is BottleFilter.Owned -> filtered.filter { !it.isWishlist }
            }

        // Apply search
        if (searchQuery.isNotBlank()) {
            filtered =
                filtered.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                        it.type.contains(searchQuery, ignoreCase = true)
                }
        }

        return filtered
    }
}
