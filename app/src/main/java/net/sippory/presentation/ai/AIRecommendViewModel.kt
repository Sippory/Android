package net.sippory.presentation.ai

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.sippory.BuildConfig
import net.sippory.data.entity.BottleEntity
import net.sippory.data.repository.BottleRepository
import org.json.JSONArray

data class AIRecommendUiState(
    val isLoading: Boolean = false,
    val recommendations: List<RecommendItem> = emptyList(),
    val error: String? = null,
)

data class RecommendItem(
    val name: String,
    val type: String,
    val abv: Float?,
    val country: String?,
    val reason: String,
    val isWished: Boolean = false,
)

class AIRecommendViewModel(
    private val repository: BottleRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AIRecommendUiState())
    val uiState: StateFlow<AIRecommendUiState> = _uiState.asStateFlow()

    // Gemini SDK
    private val model =
        GenerativeModel(
            modelName = "gemini-2.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY,
        )

    fun requestRecommendation() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                // 1) 전체 기록 가져오기
                val allBottles = repository.getAllBottles().first()

                // 2) "마신 기록"만 추리기
                val history =
                    allBottles
                        .filter { bottle ->
                            // 마신 횟수가 양수면
                            bottle.drinkCount > 0
                        }
                // .take(20) // 토큰 과다 방지: 최근 20개만 사용

                val historyText =
                    if (history.isEmpty()) {
                        "No drinking history yet."
                    } else {
                        history.joinToString(separator = "\n") { b ->
                            buildString {
                                append("- name: ${b.name}")
                                append(", type: ${b.type}")
                                if (b.abv != null) append(", abv: ${b.abv}")
                                if (!b.country.isNullOrBlank()) append(", country: ${b.country}")
                                append(", rating: ${b.rating}")
                                append(", drinkCount: ${b.drinkCount}")
                                if (b.note.isNotBlank()) append(", note: ${b.note}")
                            }
                        }
                    }

                Log.d("History", historyText)

                // 3) 프롬프트 구성 (Detail 기록 기반)
                val prompt =
                    """
                    You are an expert sommelier/bartender.
                    Based on the user's actual drinking history below, recommend 5 alcoholic beverages they are likely to enjoy next.
                    Consider preferred types, ABV range, countries, ratings, notes, and re-drink frequency.

                    USER DRINKING HISTORY:
                    $historyText

                    Respond ONLY as a JSON array with objects containing:
                    "name", "type", "abv", "country", "reason"

                    Example:
                    [
                      {
                        "name": "string",
                        "type": "string",
                        "abv": number,
                        "country": "string",
                        "reason": "string"
                      }
                    ]
                    """.trimIndent()

                val response = model.generateContent(prompt).text ?: "[]"

                val cleaned =
                    response
                        .replace("```json", "")
                        .replace("```", "")
                        .replace("\r", "")
                        .trim()

                // 4) JSON 내부 앞뒤 텍스트 제거
                val startIndex = cleaned.indexOf("[")
                val endIndex = cleaned.lastIndexOf("]")

                if (startIndex == -1 || endIndex == -1 || endIndex <= startIndex) {
                    throw Exception("JSON array not found in model output.\n$cleaned")
                }

                val jsonArrayStr = cleaned.substring(startIndex, endIndex + 1)
                val jsonArray = JSONArray(jsonArrayStr)

                val items =
                    (0 until jsonArray.length()).map { i ->
                        val obj = jsonArray.getJSONObject(i)
                        RecommendItem(
                            name = obj.optString("name"),
                            type = obj.optString("type"),
                            abv = obj.optDouble("abv").toFloat().takeIf { it > 0f },
                            country = obj.optString("country"),
                            reason = obj.optString("reason"),
                        )
                    }

                _uiState.update { it.copy(isLoading = false, recommendations = items, error = null) }
            } catch (e: Exception) {
                Log.e("AIRecommend", "Recommendation error", e)
                e.printStackTrace()
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
            }
        }
    }

    private fun normalizeType(aiType: String): String {
        val normalized = aiType.lowercase()

        return when {
            "wine" in normalized -> "Wine"
            "whiskey" in normalized || "whisky" in normalized -> "Whiskey"
            "vodka" in normalized -> "Vodka"
            "rum" in normalized -> "Rum"
            "gin" in normalized -> "Gin"
            "tequila" in normalized -> "Tequila"
            "beer" in normalized -> "Beer"
            "sake" in normalized -> "Sake"
            "soju" in normalized -> "Soju"
            "champagne" in normalized -> "Champagne"
            else -> "Other"
        }
    }

    fun toggleWishlist(item: RecommendItem) {
        // 이미 위시된 아이템은 무시 (취소 불가)
        if (item.isWished) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // DB에 바로 저장
                Log.d("Wishlist", "${item.name} 위시리스트 추가 중...")
                repository.insertBottle(
                    BottleEntity(
                        name = item.name,
                        type = normalizeType(item.type),
                        abv = item.abv,
                        country = item.country,
                        isWishlist = true,
                    ),
                )
                Log.d("Wishlist", "${item.name} 위시리스트 추가 성공")

                // UI 상태 업데이트 (Main 스레드에서)
                val updatedList =
                    _uiState.value.recommendations.map {
                        if (it == item) {
                            it.copy(isWished = true)
                        } else {
                            it
                        }
                    }
                _uiState.value = _uiState.value.copy(recommendations = updatedList)
            } catch (e: Exception) {
                Log.e("Wishlist", "${item.name} 위시리스트 추가 실패: ${e.message}", e)
            }
        }
    }
}
