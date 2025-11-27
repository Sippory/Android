// net/sippory/presentation/dashboard/DashboardScreen.kt
package net.sippory.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import net.sippory.data.AppDatabase
import net.sippory.data.repository.DashboardRepository

// 커스텀 컬러
private val WineRed = Color(0xFF8B1538)
private val DarkWine = Color(0xFF5D0E28)
private val DeepBlack = Color(0xFF0D0D0D)
private val SoftBlack = Color(0xFF1A1A1A)
private val LightGray = Color(0xFFB0B0B0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel? = null,
    onBack: (() -> Unit)? = null,
) {
    val actualVm =
        viewModel ?: run {
            val context = LocalContext.current
            val db = remember { AppDatabase.getDatabase(context) }
            val repo = remember { DashboardRepository(db.dashboardDao()) }
            viewModel(factory = DashboardViewModelFactory(repo))
        }

    val typeRanking by actualVm.typeRanking.collectAsState()
    val abvRanking by actualVm.abvRanking.collectAsState()
    val averageRatingPerType by actualVm.averageRatingPerType.collectAsState()
    val mostConsumed by actualVm.mostConsumedBottleRanking.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "대시보드",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepBlack
                )
            )
        },
        containerColor = DeepBlack
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(DeepBlack)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(vertical = 20.dp)
        ) {
            // 헤더 텍스트
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "나의 음주 통계",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        "당신의 취향을 한눈에 확인하세요",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LightGray
                    )
                }
            }

            item {
                ImageRankingSection(
                    title = "주로 마시는 주종",
                    subtitle = "TYPE RANKING",
                    items = typeRanking.map { bottle ->
                        ImageRankingItem(
                            label = bottle.type,
                            value = "${bottle.count}회",
                            imageEmoji = getTypeEmoji(bottle.type)
                        )
                    },
                    icon = "🍷"
                )
            }

            item {
                RankingSection(
                    title = "자주 마신 도수",
                    subtitle = "ABV RANKING",
                    items = abvRanking.map { bottle -> RankingItem("${bottle.abv}%", "${bottle.count}회") },
                    icon = "🔥"
                )
            }

            item {
                ImageRankingSection(
                    title = "주종별 평균 평점",
                    subtitle = "RATING BY TYPE",
                    items = averageRatingPerType.map { bottle ->
                        ImageRankingItem(
                            label = bottle.type,
                            value = String.format("%.1f점", bottle.averageRating),
                            imageEmoji = getTypeEmoji(bottle.type)
                        )
                    },
                    icon = "⭐"
                )
            }

            item {
                RankingSection(
                    title = "가장 많이 마신 술",
                    subtitle = "MOST CONSUMED",
                    items = mostConsumed.map { bottle -> RankingItem("${bottle.name}", "${bottle.count}회") },
                    icon = "🏆"
                )
            }

            if (onBack != null) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WineRed.copy(alpha = 0.2f),
                            contentColor = WineRed
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "뒤로가기",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

data class RankingItem(val label: String, val value: String)
data class ImageRankingItem(val label: String, val value: String, val imageEmoji: String)

// 주종별 이모지 매핑
private fun getTypeEmoji(type: String): String {
    return when (type.lowercase()) {
        "wine", "red wine", "white wine", "rosé", "rose" -> "🍷"
        "whisky", "whiskey", "bourbon", "scotch" -> "🥃"
        "vodka" -> "🍸"
        "gin" -> "🍸"
        "rum" -> "🥃"
        "tequila" -> "🍹"
        "beer", "lager", "ale" -> "🍺"
        "champagne", "sparkling" -> "🍾"
        "sake", "soju" -> "🍶"
        "cocktail" -> "🍹"
        else -> "🥂"
    }
}

@Composable
private fun ImageRankingSection(
    title: String,
    subtitle: String,
    items: List<ImageRankingItem>,
    icon: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 섹션 헤더
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 아이콘
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(8.dp),
                color = WineRed.copy(alpha = 0.2f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        icon,
                        fontSize = 24.sp
                    )
                }
            }

            Column {
                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = WineRed,
                    letterSpacing = 1.2.sp
                )
            }
        }

        // 이미지 랭킹 카드
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = SoftBlack
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            if (items.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "📊",
                            fontSize = 40.sp,
                            color = LightGray.copy(alpha = 0.3f)
                        )
                        Text(
                            "아직 데이터가 없습니다",
                            color = LightGray.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    items.take(5).forEachIndexed { idx, item ->
                        ImageRankingCard(
                            rank = idx + 1,
                            item = item,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ImageRankingCard(
    rank: Int,
    item: ImageRankingItem,
    modifier: Modifier = Modifier
) {
    // 1위가 가장 크고, 순위가 내려갈수록 작아짐
    val containerHeight = when (rank) {
        1 -> 180.dp
        2 -> 160.dp
        3 -> 145.dp
        4 -> 135.dp
        else -> 125.dp
    }

    val circleSize = when (rank) {
        1 -> 100.dp
        2 -> 85.dp
        3 -> 75.dp
        4 -> 65.dp
        else -> 60.dp
    }

    val fontSize = when (rank) {
        1 -> 48.sp
        2 -> 40.sp
        3 -> 35.sp
        4 -> 30.sp
        else -> 28.sp
    }

    val isTop3 = rank <= 3

    Column(
        modifier = modifier
            .height(containerHeight)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 순위 뱃지 (각진 디자인)
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(
                    if (isTop3) {
                        Brush.linearGradient(
                            colors = listOf(WineRed, DarkWine)
                        )
                    } else {
                        Brush.linearGradient(
                            colors = listOf(
                                SoftBlack.copy(alpha = 0.5f),
                                SoftBlack
                            )
                        )
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "$rank",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (isTop3) Color.White else LightGray,
                fontSize = 10.sp
            )
        }

        // 이미지 사각형 배경 (각진 디자인)
        Box(
            modifier = Modifier
                .size(circleSize)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (isTop3) {
                        Brush.linearGradient(
                            colors = listOf(
                                WineRed.copy(alpha = 0.25f),
                                DarkWine.copy(alpha = 0.4f)
                            ),
                            start = androidx.compose.ui.geometry.Offset(0f, 0f),
                            end = androidx.compose.ui.geometry.Offset(300f, 300f)
                        )
                    } else {
                        Brush.linearGradient(
                            colors = listOf(
                                LightGray.copy(alpha = 0.08f),
                                LightGray.copy(alpha = 0.15f)
                            )
                        )
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                item.imageEmoji,
                fontSize = fontSize
            )
        }

        // 하단 텍스트 영역
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // 라벨
            Text(
                item.label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isTop3) FontWeight.Bold else FontWeight.Medium,
                color = if (isTop3) Color.White else LightGray,
                maxLines = 1,
                fontSize = if (isTop3) 12.sp else 11.sp
            )

            // 값 (각진 디자인)
            Surface(
                color = if (isTop3) WineRed.copy(alpha = 0.2f) else Color.Transparent,
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    item.value,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isTop3) WineRed else LightGray.copy(alpha = 0.7f),
                    fontSize = if (isTop3) 11.sp else 10.sp
                )
            }
        }
    }
}

@Composable
private fun RankingSection(
    title: String,
    subtitle: String,
    items: List<RankingItem>,
    icon: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 섹션 헤더
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 아이콘
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(8.dp),
                color = WineRed.copy(alpha = 0.2f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        icon,
                        fontSize = 24.sp
                    )
                }
            }

            Column {
                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = WineRed,
                    letterSpacing = 1.2.sp
                )
            }
        }

        // 랭킹 카드
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = SoftBlack
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                if (items.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "📊",
                                fontSize = 40.sp,
                                color = LightGray.copy(alpha = 0.3f)
                            )
                            Text(
                                "아직 데이터가 없습니다",
                                color = LightGray.copy(alpha = 0.6f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                } else {
                    items.forEachIndexed { idx, item ->
                        RankingRow(
                            rank = idx + 1,
                            label = item.label,
                            value = item.value,
                            isTop = idx < 3
                        )
                        if (idx < items.size - 1) {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RankingRow(
    rank: Int,
    label: String,
    value: String,
    isTop: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isTop) WineRed.copy(alpha = 0.1f) else Color.Transparent
            )
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // 순위 뱃지 (각진 디자인)
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        if (isTop) {
                            Brush.linearGradient(
                                colors = listOf(WineRed, DarkWine)
                            )
                        } else {
                            Brush.linearGradient(
                                colors = listOf(
                                    SoftBlack.copy(alpha = 0.5f),
                                    SoftBlack
                                )
                            )
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "$rank",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isTop) Color.White else LightGray
                )
            }

            // 라벨
            Text(
                label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isTop) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isTop) Color.White else LightGray,
                modifier = Modifier.weight(1f)
            )
        }

        // 값 (각진 디자인)
        Surface(
            color = if (isTop) WineRed.copy(alpha = 0.2f) else Color.Transparent,
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(
                value,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (isTop) WineRed else LightGray
            )
        }
    }
}

// Preview
@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFF0D0D0D)
@Composable
fun DashboardScreenPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepBlack)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(vertical = 20.dp)
            ) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "나의 음주 통계",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "당신의 취향을 한눈에 확인하세요",
                            style = MaterialTheme.typography.bodyMedium,
                            color = LightGray
                        )
                    }
                }

                item {
                    ImageRankingSection(
                        title = "주로 마시는 주종",
                        subtitle = "TYPE RANKING",
                        items = listOf(
                            ImageRankingItem("Red Wine", "15회", "🍷"),
                            ImageRankingItem("Whisky", "12회", "🥃"),
                            ImageRankingItem("Gin", "8회", "🍸"),
                            ImageRankingItem("Beer", "5회", "🍺"),
                            ImageRankingItem("Sake", "3회", "🍶")
                        ),
                        icon = "🍷"
                    )
                }

                item {
                    RankingSection(
                        title = "자주 마신 도수",
                        subtitle = "ABV RANKING",
                        items = listOf(
                            RankingItem("13.5%", "10회"),
                            RankingItem("40.0%", "8회"),
                            RankingItem("43.0%", "7회")
                        ),
                        icon = "🔥"
                    )
                }

                item {
                    ImageRankingSection(
                        title = "주종별 평균 평점",
                        subtitle = "RATING BY TYPE",
                        items = listOf(
                            ImageRankingItem("Red Wine", "4.5점", "🍷"),
                            ImageRankingItem("Whisky", "4.2점", "🥃"),
                            ImageRankingItem("Gin", "3.8점", "🍸"),
                            ImageRankingItem("Beer", "3.5점", "🍺")
                        ),
                        icon = "⭐"
                    )
                }

                item {
                    RankingSection(
                        title = "가장 많이 마신 술",
                        subtitle = "MOST CONSUMED",
                        items = listOf(
                            RankingItem("Château Margaux 2015", "8회"),
                            RankingItem("Macallan 18", "6회"),
                            RankingItem("Hendrick's Gin", "5회")
                        ),
                        icon = "🏆"
                    )
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFF0D0D0D)
@Composable
fun EmptyRankingSectionPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepBlack)
                .padding(20.dp)
        ) {
            RankingSection(
                title = "주로 마시는 주종",
                subtitle = "TYPE RANKING",
                items = emptyList(),
                icon = "🍷"
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFF0D0D0D)
@Composable
fun ImageRankingSectionPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepBlack)
                .padding(20.dp)
        ) {
            ImageRankingSection(
                title = "주로 마시는 주종",
                subtitle = "TYPE RANKING",
                items = listOf(
                    ImageRankingItem("Red Wine", "15회", "🍷"),
                    ImageRankingItem("Whisky", "12회", "🥃"),
                    ImageRankingItem("Gin", "8회", "🍸"),
                    ImageRankingItem("Beer", "5회", "🍺"),
                    ImageRankingItem("Sake", "3회", "🍶")
                ),
                icon = "🍷"
            )
        }
    }
}
