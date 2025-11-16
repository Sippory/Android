package net.sippory.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bottles")
data class BottleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    // 예: "Wine", "Whiskey", "Vodka", "Beer" 등
    val type: String,
    // 알코올 도수 (선택)
    val abv: Float? = null,
    val country: String? = null,
    // 이미지 URI
    val photoUri: String? = null,
    // 0.5 ~ 5.0
    val rating: Float = 0f,
    val note: String = "",
    // 위시리스트 여부
    val isWishlist: Boolean = false,
    // 마신 횟수
    val drinkCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)
