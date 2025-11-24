package net.sippory.presentation.tastefinder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.sippory.data.entity.BottleEntity
import net.sippory.data.model.RecommendedBottle
import net.sippory.data.model.TasteQuestion
import net.sippory.data.repository.BottleRepository
import net.sippory.data.repository.TasteFinderRepository

/**
 * 취향 찾기 UI 상태
 */
data class TasteFinderUiState(
    val questions: List<TasteQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedTags: List<String> = emptyList(),
    val selectedAnswers: Map<Int, Boolean> = emptyMap(),
    val recommendations: List<RecommendedBottle> = emptyList(),
    val addedBottleNames: Set<String> = emptySet(),
    val isCompleted: Boolean = false,
    val isLoading: Boolean = false,
)

/**
 * 취향 찾기 ViewModel
 */
class TasteFinderViewModel(
    private val tasteFinderRepository: TasteFinderRepository = TasteFinderRepository(),
    private val bottleRepository: BottleRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(TasteFinderUiState())
    val uiState: StateFlow<TasteFinderUiState> = _uiState.asStateFlow()

    init {
        loadQuestions()
    }

    /**
     * 질문 목록 로드
     */
    private fun loadQuestions() {
        val questions = tasteFinderRepository.getQuestions()
        _uiState.update { it.copy(questions = questions) }
    }

    /**
     * 현재 질문 가져오기
     */
    fun getCurrentQuestion(): TasteQuestion? {
        val state = _uiState.value
        return if (state.currentQuestionIndex < state.questions.size) {
            state.questions[state.currentQuestionIndex]
        } else {
            null
        }
    }

    /**
     * 옵션 선택 처리
     * @param isOptionA true면 A 선택, false면 B 선택
     */
    fun selectOption(isOptionA: Boolean) {
        val currentQuestion = getCurrentQuestion() ?: return
        val selectedOption = if (isOptionA) currentQuestion.optionA else currentQuestion.optionB

        // 선택한 옵션의 태그들을 추가
        _uiState.update { state ->
            val newTags = state.selectedTags + selectedOption.tags
            val newAnswers = state.selectedAnswers + (currentQuestion.id to isOptionA)
            val nextIndex = state.currentQuestionIndex + 1

            // 마지막 질문이었다면 추천 계산
            if (nextIndex >= state.questions.size) {
                val recommendations = tasteFinderRepository.getRecommendations(newTags)
                state.copy(
                    selectedTags = newTags,
                    selectedAnswers = newAnswers,
                    currentQuestionIndex = nextIndex,
                    recommendations = recommendations,
                    isCompleted = true,
                )
            } else {
                state.copy(
                    selectedTags = newTags,
                    selectedAnswers = newAnswers,
                    currentQuestionIndex = nextIndex,
                )
            }
        }
    }

    /**
     * 이전 질문으로 돌아가기
     */
    fun goBack() {
        _uiState.update { state ->
            if (state.currentQuestionIndex > 0) {
                val prevQuestionIndex = state.currentQuestionIndex - 1
                val prevQuestion = state.questions[prevQuestionIndex]

                // 이전 질문에서 실제로 선택했던 답변을 찾아서 해당 태그만 제거
                val wasOptionASelected = state.selectedAnswers[prevQuestion.id]
                val tagsToRemove =
                    if (wasOptionASelected == true) {
                        prevQuestion.optionA.tags
                    } else if (wasOptionASelected == false) {
                        prevQuestion.optionB.tags
                    } else {
                        emptyList()
                    }

                val newTags = state.selectedTags.filterNot { it in tagsToRemove }
                val newAnswers = state.selectedAnswers - prevQuestion.id

                state.copy(
                    currentQuestionIndex = prevQuestionIndex,
                    selectedTags = newTags,
                    selectedAnswers = newAnswers,
                    isCompleted = false,
                )
            } else {
                state
            }
        }
    }

    /**
     * 추천된 술을 위시리스트에 추가
     */
    fun addToWishlist(bottle: RecommendedBottle) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val entity =
                    BottleEntity(
                        name = bottle.name,
                        type = bottle.type,
                        abv = bottle.abv,
                        country = bottle.country,
                        rating = 0f,
                        note = "${bottle.subType}\n\n${bottle.description}",
                        isWishlist = true,
                    )

                bottleRepository.insertBottle(entity)

                // 위시리스트에 추가된 항목을 상태에 추가
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        addedBottleNames = it.addedBottleNames + bottle.name,
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    /**
     * 처음부터 다시 시작
     */
    fun restart() {
        _uiState.update {
            TasteFinderUiState(
                questions = it.questions,
                currentQuestionIndex = 0,
                selectedTags = emptyList(),
                selectedAnswers = emptyMap(),
                recommendations = emptyList(),
                addedBottleNames = emptySet(),
                isCompleted = false,
            )
        }
    }

    /**
     * 진행률 계산
     */
    fun getProgress(): Float {
        val state = _uiState.value
        return if (state.questions.isEmpty()) {
            0f
        } else {
            state.currentQuestionIndex.toFloat() / state.questions.size.toFloat()
        }
    }
}

/**
 * ViewModel Factory
 */
class TasteFinderViewModelFactory(
    private val bottleRepository: BottleRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TasteFinderViewModel::class.java)) {
            return TasteFinderViewModel(bottleRepository = bottleRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
