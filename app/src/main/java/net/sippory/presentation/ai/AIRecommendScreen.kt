package net.sippory.presentation.ai

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
                title = { Text("AI 추천") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = "뒤로")
                    }
                },
            )
        },
    ) { padding ->
        Box(
            modifier =
                Modifier
                    .padding(padding)
                    .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            when {
                uiState.isLoading -> CircularProgressIndicator()

                uiState.error != null -> Text("오류 발생: ${uiState.error}")

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(16.dp),
                    ) {
                        items(uiState.recommendations.size) { index ->
                            RecommendCard(
                                item = uiState.recommendations[index],
                                onHeartClick = { viewModel.saveToWishlist(it) },
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
    var isWish by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(item.name, style = MaterialTheme.typography.titleLarge)
            Text("Type: ${item.type}")
            Text("ABV: ${item.abv}%")
            Text("Country: ${item.country}")

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "추천 이유: ${item.reason}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )

            IconButton(
                onClick = {
                    if (!isWish) {
                        onHeartClick(item)
                        isWish = true
                    }
                },
                modifier = Modifier.align(Alignment.End),
            ) {
                Icon(
                    imageVector = if (isWish) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "위시리스트에 추가",
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}
