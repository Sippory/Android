package net.sippory.presentation.ai

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.sippory.data.entity.BottleEntity
import net.sippory.data.repository.BottleRepository
import org.json.JSONArray
import net.sippory.BuildConfig

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
    val reason: String
)


class AIRecommendViewModel(
    private val repository: BottleRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AIRecommendUiState())
    val uiState: StateFlow<AIRecommendUiState> = _uiState.asStateFlow()

    // Gemini SDK
    private val model = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    fun requestRecommendation(dashboardSummary: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                val prompt = """
    Analyze the user's alcohol consumption statistics and recommend 5 alcoholic beverages.

    Respond ONLY as a JSON array with:
    "name", "type", "abv", "country", "reason"

    Example:
    [
      {
        "name": "string",
        "type": "string",
        "abv": 0.0,
        "country": "string",
        "reason": "why this was recommended"
      }
    ]

    --- User Statistics ---
    $dashboardSummary
""".trimIndent()


                val response = model.generateContent(prompt)
                val rawText = response.text ?: throw Exception("No output")

                Log.e("AI_DEBUG", "Gemini output RAW: $rawText")

                // 🔥 1) 코드블록 제거
                var cleaned = rawText
                    .replace("```json", "")
                    .replace("```", "")
                    .replace("\r", "")
                    .trim()

                // 🔥 2) JSON 내부 앞뒤의 텍스트 제거
                // 예: "Here is your JSON: [...]"
                val startIndex = cleaned.indexOf("[")
                val endIndex = cleaned.lastIndexOf("]")

                if (startIndex == -1 || endIndex == -1 || endIndex <= startIndex) {
                    throw Exception("JSON array not found in model output.\n$cleaned")
                }

                cleaned = cleaned.substring(startIndex, endIndex + 1).trim()

                Log.e("AI_DEBUG", "Gemini output CLEAN: $cleaned")

                // 🔥 3) JSONArray 변환
                val array = JSONArray(cleaned)

                val list = mutableListOf<RecommendItem>()

                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)

                    list += RecommendItem(
                        name = obj.getString("name"),
                        type = normalizeType(obj.getString("type")),
                        abv = obj.optDouble("abv", 0.0).toFloat(),
                        country = obj.optString("country", "Unknown"),
                        reason = obj.optString("reason", "AI 추천 이유 없음")
                    )
                }

                _uiState.update { it.copy(isLoading = false, recommendations = list) }


            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
            }
        }
    }

    fun saveToWishlist(item: RecommendItem) {
        viewModelScope.launch {
            repository.insertBottle(
                BottleEntity(
                    name = item.name,
                    type = item.type,
                    abv = item.abv,
                    country = item.country,
                    isWishlist = true
                )
            )
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

}
