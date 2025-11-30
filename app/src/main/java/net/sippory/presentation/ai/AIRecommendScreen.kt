package net.sippory.presentation.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// 커스텀 컬러
private val WineRed = Color(0xFF8B1538)
private val DarkWine = Color(0xFF5D0E28)
private val DeepBlack = Color(0xFF0D0D0D)
private val SoftBlack = Color(0xFF1A1A1A)
private val LightGray = Color(0xFFB0B0B0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIRecommendScreen(
    viewModel: AIRecommendViewModel,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.requestRecommendation()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "AI Recommendations",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = DeepBlack,
                    ),
            )
        },
        containerColor = DeepBlack,
    ) { padding ->
        Box(
            modifier =
                Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(DeepBlack),
            contentAlignment = Alignment.Center,
        ) {
            when {
                uiState.isLoading -> CircularProgressIndicator(color = WineRed)

                uiState.error != null ->
                    Text(
                        "Error: ${uiState.error}",
                        color = LightGray,
                        style = MaterialTheme.typography.bodyLarge,
                    )

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        contentPadding = PaddingValues(20.dp),
                    ) {
                        items(uiState.recommendations.size) { index ->
                            RecommendCard(
                                item = uiState.recommendations[index],
                                onHeartClick = { viewModel.toggleWishlist(it) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecommendCard(
    item: RecommendItem,
    onHeartClick: (RecommendItem) -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
        colors =
            CardDefaults.cardColors(
                containerColor = SoftBlack,
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 8.dp,
            ),
    ) {
        Box {
            // 그라데이션 배경 효과
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(WineRed, DarkWine, WineRed),
                            ),
                        ),
            )

            Column(
                Modifier
                    .padding(24.dp)
                    .padding(top = 4.dp),
            ) {
                // 이름
                Text(
                    item.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 정보 섹션
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    InfoChip(label = "Type", value = item.type)
                    InfoChip(label = "ABV", value = "${item.abv}%")
                }

                Spacer(modifier = Modifier.height(12.dp))

                InfoChip(label = "Origin", value = item.country ?: "Unknown")

                Spacer(modifier = Modifier.height(20.dp))

                // 구분선
                Divider(
                    color = WineRed.copy(alpha = 0.3f),
                    thickness = 1.dp,
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 추천 이유
                Column {
                    Text(
                        "Why We Recommend",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = WineRed,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        item.reason,
                        style = MaterialTheme.typography.bodyMedium,
                        color = LightGray,
                        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight.times(1.4f),
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 하트 버튼
                IconButton(
                    onClick = { onHeartClick(item) },
                    modifier = Modifier.align(Alignment.End),
                ) {
                    Icon(
                        imageVector = if (item.isWished) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Add to Wishlist",
                        tint = if (item.isWished) WineRed else LightGray,
                        modifier = Modifier.size(28.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun InfoChip(
    label: String,
    value: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Surface(
            color = WineRed.copy(alpha = 0.2f),
            shape = RoundedCornerShape(6.dp),
        ) {
            Text(
                label,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = WineRed,
            )
        }
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            fontWeight = FontWeight.Medium,
        )
    }
}

// Preview
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun RecommendCardPreview() {
    MaterialTheme {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(DeepBlack)
                    .padding(20.dp),
        ) {
            RecommendCard(
                item =
                    RecommendItem(
                        name = "Château Margaux 2015",
                        type = "Red Wine",
                        abv = 13.5f,
                        country = "France",
                        reason =
                            "An elegant full-bodied Bordeaux wine with smooth tannins and harmonious notes " +
                                "of blackberry and cassis. A premium wine that perfectly matches your taste.",
                        isWished = false,
                    ),
                onHeartClick = {},
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFF0D0D0D)
@Composable
fun RecommendCardListPreview() {
    MaterialTheme {
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(DeepBlack),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(20.dp),
        ) {
            items(3) { index ->
                RecommendCard(
                    item =
                        when (index) {
                            0 ->
                                RecommendItem(
                                    name = "Château Margaux 2015",
                                    type = "Red Wine",
                                    abv = 13.5f,
                                    country = "France",
                                    reason =
                                        "An elegant full-bodied Bordeaux wine with smooth tannins " +
                                            "and harmonious notes of blackberry and cassis.",
                                    isWished = false,
                                )
                            1 ->
                                RecommendItem(
                                    name = "Macallan 18",
                                    type = "Whisky",
                                    abv = 43.0f,
                                    country = "Scotland",
                                    reason =
                                        "A premium single malt aged in sherry oak casks, " +
                                            "featuring sweet vanilla and spicy oak notes.",
                                    isWished = true,
                                )
                            else ->
                                RecommendItem(
                                    name = "Hendrick's Gin",
                                    type = "Gin",
                                    abv = 41.4f,
                                    country = "Scotland",
                                    reason =
                                        "A premium gin with unique cucumber and rose notes, " +
                                            "offering a refreshingly elegant taste.",
                                    isWished = false,
                                )
                        },
                    onHeartClick = {},
                )
            }
        }
    }
}
