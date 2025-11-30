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
            // 1. Question: Alcohol strength preference
            TasteQuestion(
                id = 1,
                question = "What intensity do you prefer?",
                optionA =
                    TasteOption(
                        text = "Smooth and light",
                        description = "Easy to drink with lower alcohol content",
                        tags = listOf("low_abv", "smooth", "light"),
                    ),
                optionB =
                    TasteOption(
                        text = "Strong and bold",
                        description = "Intense flavor with higher alcohol content",
                        tags = listOf("high_abv", "strong", "bold"),
                    ),
            ),
            // 2. Question: Taste profile
            TasteQuestion(
                id = 2,
                question = "Which flavor profile appeals to you?",
                optionA =
                    TasteOption(
                        text = "Sweet and fruity",
                        description = "Gentle sweetness with fruit notes",
                        tags = listOf("sweet", "fruity", "aromatic"),
                    ),
                optionB =
                    TasteOption(
                        text = "Dry and bitter",
                        description = "Clean with a slightly bitter finish",
                        tags = listOf("dry", "bitter", "clean"),
                    ),
            ),
            // 3. Question: Aroma complexity
            TasteQuestion(
                id = 3,
                question = "How do you like the aroma?",
                optionA =
                    TasteOption(
                        text = "Subtle and simple",
                        description = "Clean and straightforward scent",
                        tags = listOf("simple", "clean_aroma", "subtle"),
                    ),
                optionB =
                    TasteOption(
                        text = "Complex and layered",
                        description = "Rich and multifaceted aroma",
                        tags = listOf("complex", "rich_aroma", "layered"),
                    ),
            ),
            // 4. Question: Age/Maturity preference
            TasteQuestion(
                id = 4,
                question = "What characteristics do you prefer?",
                optionA =
                    TasteOption(
                        text = "Fresh and crisp",
                        description = "Bright and refreshing feel",
                        tags = listOf("fresh", "crisp", "young"),
                    ),
                optionB =
                    TasteOption(
                        text = "Rich and mature",
                        description = "Deep flavors from oak aging",
                        tags = listOf("aged", "oaky", "mature"),
                    ),
            ),
            // 5. Question: Body texture
            TasteQuestion(
                id = 5,
                question = "What mouthfeel do you enjoy?",
                optionA =
                    TasteOption(
                        text = "Light and refreshing",
                        description = "Light body with a crisp sensation",
                        tags = listOf("light_body", "refreshing", "bubbly"),
                    ),
                optionB =
                    TasteOption(
                        text = "Rich and smooth",
                        description = "Full-bodied with silky texture",
                        tags = listOf("full_body", "silky", "smooth_texture"),
                    ),
            ),
            // 6. Question: Flavor preference
            TasteQuestion(
                id = 6,
                question = "Which flavors attract you more?",
                optionA =
                    TasteOption(
                        text = "Floral and herbal",
                        description = "Botanical with floral notes",
                        tags = listOf("floral", "herbal", "botanical"),
                    ),
                optionB =
                    TasteOption(
                        text = "Smoky and spicy",
                        description = "Smoky with spice notes",
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
            // === Wine ===
            // Red Wine
            RecommendedBottle(
                name = "Cabernet Sauvignon",
                type = "Wine",
                subType = "Red Wine - Cabernet Sauvignon",
                description = "A classic full-bodied red wine. Features black currant and oak notes with rich tannins.",
                tags = listOf("high_abv", "dry", "complex", "aged", "full_body", "bold"),
                abv = 13.5f,
                country = "France",
            ),
            RecommendedBottle(
                name = "Pinot Noir",
                type = "Wine",
                subType = "Red Wine - Pinot Noir",
                description = "An elegant light to medium-bodied red wine. Known for cherry and berry fruit flavors.",
                tags = listOf("low_abv", "fruity", "complex", "aged", "light_body", "smooth"),
                abv = 12.5f,
                country = "France",
            ),
            RecommendedBottle(
                name = "Shiraz/Syrah",
                type = "Wine",
                subType = "Red Wine - Shiraz/Syrah",
                description = "A bold full-bodied red wine. Characterized by peppery and spicy notes.",
                tags = listOf("high_abv", "spicy", "bold", "aged", "full_body", "strong"),
                abv = 14.0f,
                country = "Australia",
            ),
            // White Wine
            RecommendedBottle(
                name = "Chardonnay",
                type = "Wine",
                subType = "White Wine - Chardonnay",
                description = "A full-bodied white wine. Features buttery and vanilla notes from oak aging.",
                tags = listOf("low_abv", "smooth", "complex", "aged", "full_body", "oaky"),
                abv = 13.0f,
                country = "USA",
            ),
            RecommendedBottle(
                name = "Sauvignon Blanc",
                type = "Wine",
                subType = "White Wine - Sauvignon Blanc",
                description = "A crisp white wine. Known for grassy and citrus fresh aromas.",
                tags = listOf("low_abv", "dry", "fresh", "crisp", "light_body", "clean"),
                abv = 12.5f,
                country = "New Zealand",
            ),
            RecommendedBottle(
                name = "Riesling",
                type = "Wine",
                subType = "White Wine - Riesling",
                description = "A sweet and aromatic white wine. Features floral and peach notes.",
                tags = listOf("low_abv", "sweet", "fruity", "floral", "light_body", "aromatic"),
                abv = 11.0f,
                country = "Germany",
            ),
            // Rosé/Sparkling
            RecommendedBottle(
                name = "Rosé Wine",
                type = "Wine",
                subType = "Rosé Wine",
                description = "A light and refreshing pink wine. Features strawberry and watermelon notes.",
                tags = listOf("low_abv", "fruity", "fresh", "crisp", "light_body", "sweet"),
                abv = 12.0f,
                country = "France",
            ),
            RecommendedBottle(
                name = "Champagne/Sparkling Wine",
                type = "Wine",
                subType = "Sparkling Wine",
                description = "A premium wine with bubbles. Known for its refreshing and elegant taste.",
                tags = listOf("low_abv", "dry", "fresh", "crisp", "light_body", "bubbly", "refreshing"),
                abv = 12.0f,
                country = "France",
            ),
            // === Whiskey ===
            // Scotch
            RecommendedBottle(
                name = "Scotch Single Malt (Islay)",
                type = "Whiskey",
                subType = "Scotch - Islay Single Malt",
                description = "An Islay whisky with intense peat and smoky flavors.",
                tags = listOf("high_abv", "smoky", "peaty", "complex", "strong", "bold", "dry"),
                abv = 46.0f,
                country = "Scotland",
            ),
            RecommendedBottle(
                name = "Scotch Single Malt (Speyside)",
                type = "Whiskey",
                subType = "Scotch - Speyside Single Malt",
                description = "A Speyside whisky with smooth and sweet fruit notes.",
                tags = listOf("high_abv", "sweet", "fruity", "smooth", "complex", "aged", "oaky"),
                abv = 43.0f,
                country = "Scotland",
            ),
            // Bourbon/Irish
            RecommendedBottle(
                name = "Bourbon Whiskey",
                type = "Whiskey",
                subType = "Bourbon Whiskey",
                description = "An American whiskey with sweet caramel and vanilla notes.",
                tags = listOf("high_abv", "sweet", "smooth", "oaky", "aged", "full_body", "strong"),
                abv = 45.0f,
                country = "USA",
            ),
            RecommendedBottle(
                name = "Irish Whiskey",
                type = "Whiskey",
                subType = "Irish Whiskey",
                description = "A smooth and light whiskey. Triple-distilled for a clean taste.",
                tags = listOf("high_abv", "smooth", "light", "clean", "simple", "subtle"),
                abv = 40.0f,
                country = "Ireland",
            ),
            // === Beer ===
            RecommendedBottle(
                name = "Pilsner",
                type = "Beer",
                subType = "Lager - Pilsner",
                description = "A light and refreshing lager. Known for its clean taste and subtle hop bitterness.",
                tags = listOf("low_abv", "dry", "bitter", "fresh", "crisp", "light_body", "refreshing", "bubbly"),
                abv = 5.0f,
                country = "Czech Republic",
            ),
            RecommendedBottle(
                name = "IPA (India Pale Ale)",
                type = "Beer",
                subType = "Ale - IPA",
                description = "An ale with strong hop bitterness and citrus notes.",
                tags = listOf("low_abv", "bitter", "aromatic", "fruity", "bold", "full_body"),
                abv = 6.5f,
                country = "USA",
            ),
            RecommendedBottle(
                name = "Stout",
                type = "Beer",
                subType = "Ale - Stout",
                description = "A rich dark beer. Features roasted coffee and chocolate flavors.",
                tags = listOf("low_abv", "bitter", "complex", "rich_aroma", "full_body", "smooth_texture", "aged"),
                abv = 5.5f,
                country = "Ireland",
            ),
            RecommendedBottle(
                name = "Wheat Beer (Hefeweizen)",
                type = "Beer",
                subType = "Wheat Beer - Hefeweizen",
                description = "A smooth and refreshing wheat beer. Features banana and clove notes.",
                tags = listOf("low_abv", "fruity", "sweet", "smooth", "light_body", "refreshing", "aromatic"),
                abv = 5.0f,
                country = "Germany",
            ),
            // === Traditional ===
            RecommendedBottle(
                name = "Makgeolli",
                type = "Traditional",
                subType = "Korean Rice Wine",
                description = "A traditional Korean rice wine. Features gentle sweetness and fizzy carbonation.",
                tags = listOf("low_abv", "sweet", "smooth", "fresh", "light_body", "bubbly", "simple"),
                abv = 6.0f,
                country = "Korea",
            ),
            RecommendedBottle(
                name = "Cheongju",
                type = "Traditional",
                subType = "Korean Clear Rice Wine",
                description = "A clear Korean traditional wine. Known for smooth sweetness and clean finish.",
                tags = listOf("low_abv", "sweet", "smooth", "clean", "light_body", "simple", "subtle"),
                abv = 13.0f,
                country = "Korea",
            ),
            RecommendedBottle(
                name = "Sake",
                type = "Traditional",
                subType = "Japanese Rice Wine",
                description =
                    """A Japanese traditional wine. Features delicate flavors and can be enjoyed at various temperatures.""",
                tags = listOf("low_abv", "clean", "subtle", "smooth", "light_body", "aromatic"),
                abv = 15.0f,
                country = "Japan",
            ),
            RecommendedBottle(
                name = "Korean Distilled Soju",
                type = "Traditional",
                subType = "Korean Distilled Soju",
                description =
                    """A traditionally distilled Korean spirit. Known for deep flavors and higher alcohol content.""",
                tags = listOf("high_abv", "strong", "complex", "bold", "clean", "aged"),
                abv = 25.0f,
                country = "Korea",
            ),
            // === Spirits ===
            RecommendedBottle(
                name = "Gin",
                type = "Spirit",
                subType = "London Dry Gin",
                description = "A spirit featuring juniper berry notes. Rich in herbal and botanical aromas.",
                tags = listOf("high_abv", "dry", "botanical", "herbal", "aromatic", "clean", "strong"),
                abv = 40.0f,
                country = "UK",
            ),
            RecommendedBottle(
                name = "Rum",
                type = "Spirit",
                subType = "Dark Rum",
                description = "A spirit made from sugarcane. Features sweet and spicy flavors.",
                tags = listOf("high_abv", "sweet", "spicy", "aged", "full_body", "oaky", "strong"),
                abv = 40.0f,
                country = "Caribbean",
            ),
            RecommendedBottle(
                name = "Vodka",
                type = "Spirit",
                subType = "Vodka",
                description = "A clean and pure spirit. Known for its neutral taste with minimal flavor and aroma.",
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
