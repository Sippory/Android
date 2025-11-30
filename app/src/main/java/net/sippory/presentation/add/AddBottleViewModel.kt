package net.sippory.presentation.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.sippory.data.entity.BottleEntity
import net.sippory.data.repository.BottleRepository

data class AddBottleUiState(
    val name: String = "",
    val type: String = "Wine",
    val abv: String = "",
    val country: String = "",
    val photoUri: String? = null,
    val rating: Float = 3f,
    val note: String = "",
    val locationName: String = "",
    val isSaving: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false,
    val isWishlist: Boolean = false,
)

class AddBottleViewModel(private val repository: BottleRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(AddBottleUiState())
    val uiState: StateFlow<AddBottleUiState> = _uiState.asStateFlow()

    fun updateName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun updateType(type: String) {
        _uiState.update { it.copy(type = type) }
    }

    fun updateAbv(abv: String) {
        _uiState.update { it.copy(abv = abv) }
    }

    fun updateCountry(country: String) {
        _uiState.update { it.copy(country = country) }
    }

    fun updatePhotoUri(uri: String?) {
        _uiState.update { it.copy(photoUri = uri) }
    }

    fun updateRating(rating: Float) {
        _uiState.update { it.copy(rating = rating) }
    }

    fun updateNote(note: String) {
        _uiState.update { it.copy(note = note) }
    }

    fun updateLocationName(location: String) {
        _uiState.update { it.copy(locationName = location) }
    }

    fun toggleWishlist() {
        _uiState.update { it.copy(isWishlist = !it.isWishlist) }
    }

    fun saveBottle() {
        viewModelScope.launch {
            val state = _uiState.value

            if (state.name.isBlank()) {
                _uiState.update { it.copy(error = "이름을 입력해주세요") }
                return@launch
            }

            _uiState.update { it.copy(isSaving = true, error = null) }

            try {
                val bottle =
                    BottleEntity(
                        name = state.name,
                        type = state.type,
                        abv = state.abv.toFloatOrNull(),
                        country = state.country.ifBlank { null },
                        photoUri = state.photoUri,
                        rating = state.rating,
                        note = state.note,
                        isWishlist = state.isWishlist,
                        locationName = state.locationName.trim().ifBlank { null },
                        latitude = null,
                        longitude = null,
                    )

                repository.insertBottle(bottle)
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        saveSuccess = true,
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        error = e.message ?: "저장 실패",
                    )
                }
            }
        }
    }

    fun resetState() {
        _uiState.value = AddBottleUiState()
    }
}
