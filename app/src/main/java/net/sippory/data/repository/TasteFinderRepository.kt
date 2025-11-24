package net.sippory.data.repository

import net.sippory.data.model.RecommendedBottle
import net.sippory.data.model.TasteOption
import net.sippory.data.model.TasteQuestion

/**
 * 취향 찾기 질문과 추천 술 데이터 관리
 */
class TasteFinderRepository {
    /**
     * 취향 찾기 질문 목록
     */
    fun getQuestions(): List<TasteQuestion> {
        return listOf(
            // 1. 첫 번째 질문: 도수 선호도
            TasteQuestion(
                id = 1,
                question = "어떤 강도의 술이 좋으신가요?",
                optionA =
                    TasteOption(
                        text = "부드럽고 가벼운 느낌",
                        description = "목넘김이 편하고 도수가 낮은 술",
                        tags = listOf("low_abv", "smooth", "light"),
                    ),
                optionB =
                    TasteOption(
                        text = "강렬하고 깊은 느낌",
                        description = "진한 맛과 높은 도수의 술",
                        tags = listOf("high_abv", "strong", "bold"),
                    ),
            ),
            // 2. 두 번째 질문: 맛의 방향
            TasteQuestion(
                id = 2,
                question = "어떤 맛을 선호하시나요?",
                optionA =
                    TasteOption(
                        text = "달콤하고 과일향이 나는",
                        description = "은은한 단맛과 과일의 향",
                        tags = listOf("sweet", "fruity", "aromatic"),
                    ),
                optionB =
                    TasteOption(
                        text = "드라이하고 씁쓸한",
                        description = "깔끔하고 쌉싸름한 뒷맛",
                        tags = listOf("dry", "bitter", "clean"),
                    ),
            ),
            // 3. 세 번째 질문: 향의 복잡도
            TasteQuestion(
                id = 3,
                question = "향에 대한 선호도는?",
                optionA =
                    TasteOption(
                        text = "은은하고 단순한 향",
                        description = "깔끔하고 부담 없는 향",
                        tags = listOf("simple", "clean_aroma", "subtle"),
                    ),
                optionB =
                    TasteOption(
                        text = "복잡하고 깊은 향",
                        description = "다층적이고 풍부한 향",
                        tags = listOf("complex", "rich_aroma", "layered"),
                    ),
            ),
            // 4. 네 번째 질문: 온도/숙성 선호
            TasteQuestion(
                id = 4,
                question = "어떤 특성의 술이 좋으신가요?",
                optionA =
                    TasteOption(
                        text = "신선하고 상큼한",
                        description = "산뜻하고 청량한 느낌",
                        tags = listOf("fresh", "crisp", "young"),
                    ),
                optionB =
                    TasteOption(
                        text = "묵직하고 성숙한",
                        description = "오크통 숙성의 깊은 맛",
                        tags = listOf("aged", "oaky", "mature"),
                    ),
            ),
            // 5. 다섯 번째 질문: 바디감
            TasteQuestion(
                id = 5,
                question = "입안에서 느껴지는 질감은?",
                optionA =
                    TasteOption(
                        text = "가볍고 청량한",
                        description = "가벼운 바디감과 톡 쏘는 느낌",
                        tags = listOf("light_body", "refreshing", "bubbly"),
                    ),
                optionB =
                    TasteOption(
                        text = "묵직하고 부드러운",
                        description = "풀바디의 부드러운 질감",
                        tags = listOf("full_body", "silky", "smooth_texture"),
                    ),
            ),
            // 6. 여섯 번째 질문: 풍미 선호
            TasteQuestion(
                id = 6,
                question = "어떤 풍미가 더 매력적인가요?",
                optionA =
                    TasteOption(
                        text = "꽃향기와 허브",
                        description = "플로럴하고 허브향이 나는",
                        tags = listOf("floral", "herbal", "botanical"),
                    ),
                optionB =
                    TasteOption(
                        text = "스모키하고 스파이시",
                        description = "훈연향과 향신료의 맛",
                        tags = listOf("smoky", "spicy", "peaty"),
                    ),
            ),
        )
    }

    /**
     * 추천 가능한 술 목록
     */
    fun getRecommendableBottles(): List<RecommendedBottle> {
        return listOf(
            // === 와인 ===
            // 레드 와인
            RecommendedBottle(
                name = "까베르네 소비뇽",
                type = "Wine",
                subType = "Red Wine - Cabernet Sauvignon",
                description = "풀바디의 대표 레드와인. 블랙커런트와 오크향이 특징적이며 타닌이 풍부합니다.",
                tags = listOf("high_abv", "dry", "complex", "aged", "full_body", "bold"),
                abv = 13.5f,
                country = "France",
            ),
            RecommendedBottle(
                name = "피노 누아",
                type = "Wine",
                subType = "Red Wine - Pinot Noir",
                description = "라이트~미디엄 바디의 우아한 레드와인. 체리와 베리류의 과일향이 특징입니다.",
                tags = listOf("low_abv", "fruity", "complex", "aged", "light_body", "smooth"),
                abv = 12.5f,
                country = "France",
            ),
            RecommendedBottle(
                name = "쉬라즈/시라",
                type = "Wine",
                subType = "Red Wine - Shiraz/Syrah",
                description = "강렬한 풀바디 레드와인. 후추향과 스파이시한 맛이 특징입니다.",
                tags = listOf("high_abv", "spicy", "bold", "aged", "full_body", "strong"),
                abv = 14.0f,
                country = "Australia",
            ),
            // 화이트 와인
            RecommendedBottle(
                name = "샤르도네",
                type = "Wine",
                subType = "White Wine - Chardonnay",
                description = "풀바디 화이트와인. 버터향과 바닐라향이 나며 오크 숙성됩니다.",
                tags = listOf("low_abv", "smooth", "complex", "aged", "full_body", "oaky"),
                abv = 13.0f,
                country = "USA",
            ),
            RecommendedBottle(
                name = "소비뇽 블랑",
                type = "Wine",
                subType = "White Wine - Sauvignon Blanc",
                description = "상큼한 화이트와인. 풀과 감귤류의 프레시한 향이 특징입니다.",
                tags = listOf("low_abv", "dry", "fresh", "crisp", "light_body", "clean"),
                abv = 12.5f,
                country = "New Zealand",
            ),
            RecommendedBottle(
                name = "리슬링",
                type = "Wine",
                subType = "White Wine - Riesling",
                description = "달콤하고 향긋한 화이트와인. 꽃향기와 복숭아향이 납니다.",
                tags = listOf("low_abv", "sweet", "fruity", "floral", "light_body", "aromatic"),
                abv = 11.0f,
                country = "Germany",
            ),
            // 로제/스파클링
            RecommendedBottle(
                name = "로제 와인",
                type = "Wine",
                subType = "Rosé Wine",
                description = "가볍고 상큼한 핑크빛 와인. 딸기와 수박향이 특징입니다.",
                tags = listOf("low_abv", "fruity", "fresh", "crisp", "light_body", "sweet"),
                abv = 12.0f,
                country = "France",
            ),
            RecommendedBottle(
                name = "샴페인/스파클링 와인",
                type = "Wine",
                subType = "Sparkling Wine",
                description = "탄산이 있는 고급 와인. 청량하고 우아한 맛이 특징입니다.",
                tags = listOf("low_abv", "dry", "fresh", "crisp", "light_body", "bubbly", "refreshing"),
                abv = 12.0f,
                country = "France",
            ),
            // === 위스키 ===
            // 스카치
            RecommendedBottle(
                name = "스카치 싱글몰트 (아일라)",
                type = "Whiskey",
                subType = "Scotch - Islay Single Malt",
                description = "강렬한 피트향과 스모키한 맛이 특징인 아일라 위스키.",
                tags = listOf("high_abv", "smoky", "peaty", "complex", "strong", "bold", "dry"),
                abv = 46.0f,
                country = "Scotland",
            ),
            RecommendedBottle(
                name = "스카치 싱글몰트 (스페이사이드)",
                type = "Whiskey",
                subType = "Scotch - Speyside Single Malt",
                description = "부드럽고 달콤한 과일향이 특징인 스페이사이드 위스키.",
                tags = listOf("high_abv", "sweet", "fruity", "smooth", "complex", "aged", "oaky"),
                abv = 43.0f,
                country = "Scotland",
            ),
            // 버번/아이리시
            RecommendedBottle(
                name = "버번 위스키",
                type = "Whiskey",
                subType = "Bourbon Whiskey",
                description = "달콤한 캐러멜과 바닐라향이 특징인 미국 위스키.",
                tags = listOf("high_abv", "sweet", "smooth", "oaky", "aged", "full_body", "strong"),
                abv = 45.0f,
                country = "USA",
            ),
            RecommendedBottle(
                name = "아이리시 위스키",
                type = "Whiskey",
                subType = "Irish Whiskey",
                description = "부드럽고 가벼운 위스키. 3회 증류로 깔끔한 맛이 특징입니다.",
                tags = listOf("high_abv", "smooth", "light", "clean", "simple", "subtle"),
                abv = 40.0f,
                country = "Ireland",
            ),
            // === 맥주 ===
            RecommendedBottle(
                name = "필스너",
                type = "Beer",
                subType = "Lager - Pilsner",
                description = "가볍고 상쾌한 라거. 깔끔한 맛과 홉의 은은한 쓴맛이 특징입니다.",
                tags = listOf("low_abv", "dry", "bitter", "fresh", "crisp", "light_body", "refreshing", "bubbly"),
                abv = 5.0f,
                country = "Czech Republic",
            ),
            RecommendedBottle(
                name = "IPA (인디아 페일 에일)",
                type = "Beer",
                subType = "Ale - IPA",
                description = "홉의 강한 쓴맛과 시트러스향이 특징인 에일.",
                tags = listOf("low_abv", "bitter", "aromatic", "fruity", "bold", "full_body"),
                abv = 6.5f,
                country = "USA",
            ),
            RecommendedBottle(
                name = "스타우트",
                type = "Beer",
                subType = "Ale - Stout",
                description = "진한 흑맥주. 로스티한 커피와 초콜릿 풍미가 특징입니다.",
                tags = listOf("low_abv", "bitter", "complex", "rich_aroma", "full_body", "smooth_texture", "aged"),
                abv = 5.5f,
                country = "Ireland",
            ),
            RecommendedBottle(
                name = "밀맥주 (바이젠)",
                type = "Beer",
                subType = "Wheat Beer - Hefeweizen",
                description = "부드럽고 상큼한 밀맥주. 바나나와 클로브향이 특징입니다.",
                tags = listOf("low_abv", "fruity", "sweet", "smooth", "light_body", "refreshing", "aromatic"),
                abv = 5.0f,
                country = "Germany",
            ),
            // === 전통주 ===
            RecommendedBottle(
                name = "막걸리",
                type = "Traditional",
                subType = "Korean Rice Wine",
                description = "한국 전통 탁주. 은은한 단맛과 톡 쏘는 탄산이 특징입니다.",
                tags = listOf("low_abv", "sweet", "smooth", "fresh", "light_body", "bubbly", "simple"),
                abv = 6.0f,
                country = "Korea",
            ),
            RecommendedBottle(
                name = "청주",
                type = "Traditional",
                subType = "Korean Clear Rice Wine",
                description = "맑고 깨끗한 한국 전통주. 부드러운 단맛과 깔끔한 뒷맛이 특징입니다.",
                tags = listOf("low_abv", "sweet", "smooth", "clean", "light_body", "simple", "subtle"),
                abv = 13.0f,
                country = "Korea",
            ),
            RecommendedBottle(
                name = "사케",
                type = "Traditional",
                subType = "Japanese Rice Wine",
                description = "일본 전통주. 섬세한 풍미와 다양한 온도에서 즐길 수 있습니다.",
                tags = listOf("low_abv", "clean", "subtle", "smooth", "light_body", "aromatic"),
                abv = 15.0f,
                country = "Japan",
            ),
            RecommendedBottle(
                name = "증류식 소주",
                type = "Traditional",
                subType = "Korean Distilled Soju",
                description = "전통 방식으로 증류한 한국 소주. 깊은 풍미와 높은 도수가 특징입니다.",
                tags = listOf("high_abv", "strong", "complex", "bold", "clean", "aged"),
                abv = 25.0f,
                country = "Korea",
            ),
            // === 기타 ===
            RecommendedBottle(
                name = "진 (Gin)",
                type = "Spirit",
                subType = "London Dry Gin",
                description = "주니퍼 베리향이 특징인 증류주. 허브와 식물성 향이 풍부합니다.",
                tags = listOf("high_abv", "dry", "botanical", "herbal", "aromatic", "clean", "strong"),
                abv = 40.0f,
                country = "UK",
            ),
            RecommendedBottle(
                name = "럼 (Rum)",
                type = "Spirit",
                subType = "Dark Rum",
                description = "사탕수수로 만든 증류주. 달콤하고 스파이시한 맛이 특징입니다.",
                tags = listOf("high_abv", "sweet", "spicy", "aged", "full_body", "oaky", "strong"),
                abv = 40.0f,
                country = "Caribbean",
            ),
            RecommendedBottle(
                name = "보드카",
                type = "Spirit",
                subType = "Vodka",
                description = "깨끗하고 순수한 증류주. 무색무취에 가까운 깔끔한 맛이 특징입니다.",
                tags = listOf("high_abv", "clean", "smooth", "simple", "subtle", "strong"),
                abv = 40.0f,
                country = "Russia",
            ),
        )
    }

    /**
     * 사용자가 선택한 태그를 기반으로 술을 추천
     * @param selectedTags 사용자가 선택한 태그들
     * @return 추천 점수 순으로 정렬된 술 목록 (상위 3개)
     */
    fun getRecommendations(selectedTags: List<String>): List<RecommendedBottle> {
        val bottles = getRecommendableBottles()

        // 각 술에 대해 매칭 점수 계산
        val scored =
            bottles.map { bottle ->
                val matchCount = bottle.tags.count { it in selectedTags }
                bottle to matchCount
            }

        // 점수 순으로 정렬하고 상위 3개 반환
        return scored
            .sortedByDescending { it.second }
            .take(3)
            .map { it.first }
    }
}
