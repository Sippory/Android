package net.sippory.utils

object BottleTypes {
    val ALL_TYPES =
        listOf(
            "Wine" to "🍷",
            "Whiskey" to "🥃",
            "Vodka" to "🍸",
            "Rum" to "🍹",
            "Gin" to "🍸",
            "Tequila" to "🥃",
            "Beer" to "🍺",
            "Sake" to "🍶",
            "Soju" to "🍶",
            "Champagne" to "🍾",
            "Other" to "🍷",
        )

    fun getEmojiForType(type: String): String {
        return ALL_TYPES.find { it.first == type }?.second ?: "🍷"
    }
}
