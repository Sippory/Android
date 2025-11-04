package net.sippory.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.sippory.data.entity.BottleEntity
import net.sippory.data.repository.BottleRepository

data class DetailUiState(
    val bottle: BottleEntity? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isEditing: Boolean = false
)

class DetailViewModel(private val repository: BottleRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadBottle(bottleId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val bottle = repository.getBottleById(bottleId)
                _uiState.update {
                    it.copy(
                        bottle = bottle,
                        isLoading = false,
                        error = if (bottle == null) "Bottle not found" else null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun updateBottle(bottle: BottleEntity) {
        viewModelScope.launch {
            try {
                repository.updateBottle(bottle.copy(updatedAt = System.currentTimeMillis()))
                _uiState.update { it.copy(bottle = bottle, isEditing = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
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

    fun toggleWishlist() {
        viewModelScope.launch {
            val currentBottle = _uiState.value.bottle ?: return@launch
            try {
                val newWishlistStatus = !currentBottle.isWishlist
                repository.updateWishlistStatus(currentBottle.id, newWishlistStatus)
                _uiState.update {
                    it.copy(bottle = currentBottle.copy(isWishlist = newWishlistStatus))
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun recordDrink() {
        viewModelScope.launch {
            val currentBottle = _uiState.value.bottle ?: return@launch
            try {
                repository.incrementDrinkCount(currentBottle.id)
                _uiState.update {
                    it.copy(bottle = currentBottle.copy(drinkCount = currentBottle.drinkCount + 1))
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun toggleEditMode() {
        _uiState.update { it.copy(isEditing = !it.isEditing) }
    }
}
