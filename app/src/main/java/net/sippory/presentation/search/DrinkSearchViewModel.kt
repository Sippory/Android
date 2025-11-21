package net.sippory.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.sippory.data.entity.DrinkEntity
import net.sippory.data.entity.RecentlySearchedDrinkEntity
import net.sippory.data.repository.DrinkRepository
import net.sippory.data.repository.RecentlySearchedDrinkRepository

class DrinkSearchViewModel(
    private val drinkRepository: DrinkRepository,
    private val recentlySearchedDrinkRepository: RecentlySearchedDrinkRepository,
) : ViewModel() {
    private val _searchResult = MutableStateFlow<List<DrinkEntity>>(emptyList())
    val searchResults: StateFlow<List<DrinkEntity>> = _searchResult.asStateFlow()

    private val _recentlySearchedDrinks = MutableStateFlow<List<RecentlySearchedDrinkEntity>>(emptyList())
    val recentlySearchedDrinks: StateFlow<List<RecentlySearchedDrinkEntity>> = _recentlySearchedDrinks.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _selectedDrink = MutableStateFlow<DrinkEntity?>(null)
    val selectedDrink: StateFlow<DrinkEntity?> = _selectedDrink.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadRecentlySearchedDrinks()
    }

    private fun loadRecentlySearchedDrinks() {
        viewModelScope.launch {
            recentlySearchedDrinkRepository.getAllRecentlySearchedDrinks()
                .collect { drinks ->
                    _recentlySearchedDrinks.value = drinks
                }
        }
    }

    fun searchDrinks(query: String) {
        println(query)
        searchJob?.cancel()

        if (query.isBlank()) {
            _searchResult.value = emptyList()
            return
        }

        searchJob =
            viewModelScope.launch {
                _isSearching.value = true
                delay(300) // 디바운싱

                try {
                    val results = drinkRepository.getDrinksByName(query)
                    _searchResult.value = results
                } catch (e: Exception) {
                    _searchResult.value = emptyList()
                } finally {
                    _isSearching.value = false
                }
            }
    }

    fun onDrinkSelected(drink: DrinkEntity) {
        _selectedDrink.value = drink
        viewModelScope.launch {
            val selectedDrink =
                RecentlySearchedDrinkEntity(
                    name = drink.name,
                    category = drink.category,
                    image_url = drink.image_url,
                    timestamp = System.currentTimeMillis(),
                )
            recentlySearchedDrinkRepository.addOrUpdateRecentlySearchedDrink(selectedDrink)
        }
    }

    fun onClickRemoveRecentlySearchedDrink(drink: RecentlySearchedDrinkEntity) {
        viewModelScope.launch {
            recentlySearchedDrinkRepository.removeRecentlySearchedDrink(drink)
        }
    }

    fun clearRecentlySearchedDrinks() {
        _searchResult.value = emptyList()
    }
}
