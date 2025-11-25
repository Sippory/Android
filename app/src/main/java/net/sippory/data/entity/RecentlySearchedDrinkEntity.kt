package net.sippory.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recently_searched_drinks")
data class RecentlySearchedDrinkEntity(
    @PrimaryKey val name: String = "",
    val category: String = "",
    val image_url: String = "",
    val timestamp: Long,
)
