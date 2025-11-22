package net.sippory.data.ai

import kotlinx.serialization.Serializable

@Serializable
data class BottleInfo(
    val name: String,
    val type: String,
    val abv: Float? = null,
    val country: String? = null,
    val description: String? = null,
    val confidence: Float = 0f,
)
