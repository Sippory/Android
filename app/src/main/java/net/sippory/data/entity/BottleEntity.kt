package net.sippory.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bottles")
data class BottleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val type: String, // 예: "Wine", "Whiskey", "Vodka", "Beer" 등
    val abv: Float? = null, // 알코올 도수 (선택)
    val country: String? = null,
    val photoUri: String? = null, // 이미지 URI
    val rating: Float = 0f, // 0.5 ~ 5.0
    val note: String = "",
    val isWishlist: Boolean = false, // 위시리스트 여부
    val drinkCount: Int = 0, // 마신 횟수
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)
