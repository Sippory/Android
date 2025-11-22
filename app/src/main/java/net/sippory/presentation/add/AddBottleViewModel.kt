package net.sippory.presentation.add

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.sippory.BuildConfig
import net.sippory.data.ai.GeminiService
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
    val isSaving: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false,
    val isWishlist: Boolean = false,
    val isAnalyzing: Boolean = false,
    val aiSuggestion: String? = null,
)

class AddBottleViewModel(
    private val repository: BottleRepository,
    private val context: Context,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddBottleUiState())
    val uiState: StateFlow<AddBottleUiState> = _uiState.asStateFlow()

    private val geminiService = GeminiService.getInstance(context)

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

    fun toggleWishlist() {
        _uiState.update { it.copy(isWishlist = !it.isWishlist) }
    }

    fun analyzeBottleImage(imageUri: Uri) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "========================================")
            Log.d(TAG, "🔍 이미지 분석 요청 시작")
            Log.d(TAG, "이미지 URI: $imageUri")
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isAnalyzing = true, error = null, aiSuggestion = null) }
            if (BuildConfig.DEBUG) Log.d(TAG, "UI 상태 업데이트: 분석 중...")

            geminiService.analyzeBottle(imageUri).fold(
                onSuccess = { bottleInfo ->
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "✅ AI 분석 성공!")
                        Log.d(TAG, "- 이름: ${bottleInfo.name}")
                        Log.d(TAG, "- 타입: ${bottleInfo.type}")
                        Log.d(TAG, "- 도수: ${bottleInfo.abv ?: "N/A"}")
                        Log.d(TAG, "- 국가: ${bottleInfo.country ?: "N/A"}")
                        Log.d(TAG, "- 신뢰도: ${bottleInfo.confidence}%")
                        Log.d(TAG, "- 설명: ${bottleInfo.description ?: "N/A"}")
                    }

                    _uiState.update {
                        it.copy(
                            name = bottleInfo.name,
                            type = bottleInfo.type,
                            abv = bottleInfo.abv?.toString() ?: "",
                            country = bottleInfo.country ?: "",
                            photoUri = imageUri.toString(),
                            note = bottleInfo.description ?: "",
                            isAnalyzing = false,
                            aiSuggestion =
                                "🤖 AI가 ${bottleInfo.confidence.toInt()}% 확률로 " +
                                    "'${bottleInfo.name}'으로 인식했습니다!",
                        )
                    }
                    if (BuildConfig.DEBUG) Log.d(TAG, "UI 상태 업데이트 완료")
                },
                onFailure = { error ->
                    Log.e(TAG, "❌ AI 분석 실패")
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "에러 메시지: ${error.message}")
                        Log.e(TAG, "에러 스택: ${error.stackTraceToString()}")
                    }

                    _uiState.update {
                        it.copy(
                            isAnalyzing = false,
                            error = error.message,
                            // 실패해도 이미지는 저장
                            photoUri = imageUri.toString(),
                        )
                    }
                    if (BuildConfig.DEBUG) Log.d(TAG, "UI 에러 상태 업데이트 완료")
                },
            )
            if (BuildConfig.DEBUG) Log.d(TAG, "========================================")
        }
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

    companion object {
        private const val TAG = "AddBottleViewModel"
    }
}
