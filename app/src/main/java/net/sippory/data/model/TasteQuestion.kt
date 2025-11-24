package net.sippory.data.model

/**
 * 취향 찾기 질문 데이터 모델
 */
data class TasteQuestion(
    val id: Int,
    val question: String,
    val optionA: TasteOption,
    val optionB: TasteOption,
)

/**
 * 선택지 옵션
 */
data class TasteOption(
    val text: String,
    val description: String,
    val tags: List<String>, // 이 선택이 나타내는 술 특성 태그
)

/**
 * 추천 술 정보
 */
data class RecommendedBottle(
    val name: String,
    val type: String,
    val subType: String,
    val description: String,
    val tags: List<String>, // 이 술이 가진 특성 태그
    val abv: Float,
    val country: String,
    val imageUrl: String? = null,
)
