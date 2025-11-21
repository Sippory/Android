package net.sippory.presentation.home

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import net.sippory.data.entity.BottleEntity
import net.sippory.navigation.Screen
import net.sippory.presentation.add.AddBottleSheet
import net.sippory.utils.BottleTypes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onBottleClick: (Int) -> Unit,
    repository: net.sippory.data.repository.BottleRepository,
    onDashboardClick: () -> Unit,
    navController: NavHostController,
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddBottleSheet by remember { mutableStateOf(false) }
    var showSearchBar by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sippory", fontWeight = FontWeight.Bold) },
                actions = {
                    // ✅ 대시보드로 이동 버튼 추가
                    IconButton(onClick = onDashboardClick) {
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = "대시보드로 이동",
                        )
                    }

                    IconButton(onClick = { showSearchBar = !showSearchBar }) {
                        Icon(Icons.Default.Search, contentDescription = "검색")
                    }

                    IconButton(onClick = {
                        navController.navigate(Screen.AIRecommend.route)
                    }) {
                        Icon(Icons.Default.Email, contentDescription = "AI 추천")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddBottleSheet = true },
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                Icon(Icons.Default.Add, contentDescription = "술 추가")
            }
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding),
        ) {
            // 검색 바
            AnimatedVisibility(
                visible = showSearchBar,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                SearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = viewModel::onSearchQueryChange,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }

            // 필터 칩
            FilterChips(
                selectedFilter = uiState.selectedFilter,
                onFilterChange = viewModel::onFilterChange,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )

            // 병 그리드
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.bottles.isEmpty()) {
                EmptyState(modifier = Modifier.fillMaxSize())
            } else {
                BottleGrid(
                    bottles = uiState.bottles,
                    onBottleClick = onBottleClick,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }

    // AddBottleSheet
    if (showAddBottleSheet) {
        AddBottleSheet(
            onDismiss = { showAddBottleSheet = false },
            repository = repository,
        )
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("술 이름이나 종류로 검색") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        singleLine = true,
        shape = RoundedCornerShape(24.dp),
    )
}

@Composable
fun FilterChips(
    selectedFilter: BottleFilter,
    onFilterChange: (BottleFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterChip(
            selected = selectedFilter is BottleFilter.All,
            onClick = { onFilterChange(BottleFilter.All) },
            label = { Text("전체") },
        )

        FilterChip(
            selected = selectedFilter is BottleFilter.Wishlist,
            onClick = { onFilterChange(BottleFilter.Wishlist) },
            label = { Text("💝 위시리스트") },
        )

        FilterChip(
            selected = selectedFilter is BottleFilter.Owned,
            onClick = { onFilterChange(BottleFilter.Owned) },
            label = { Text("🍾 소유") },
        )

        BottleTypes.ALL_TYPES.take(5).forEach { (type, emoji) ->
            FilterChip(
                selected = selectedFilter is BottleFilter.ByType && selectedFilter.type == type,
                onClick = { onFilterChange(BottleFilter.ByType(type)) },
                label = { Text("$emoji $type") },
            )
        }

        FilterChip(
            selected = selectedFilter is BottleFilter.ByRating,
            onClick = { onFilterChange(BottleFilter.ByRating(4f)) },
            label = { Text("⭐ 4점 이상") },
        )
    }
}

@Composable
fun BottleGrid(
    bottles: List<BottleEntity>,
    onBottleClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(bottles, key = { it.id }) { bottle ->
            BottleCard(
                bottle = bottle,
                onClick = { onBottleClick(bottle.id) },
                modifier = Modifier.animateItem(),
            )
        }
    }
}

@Composable
fun BottleCard(
    bottle: BottleEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .aspectRatio(0.7f)
                .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            // 이미지
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center,
            ) {
                if (bottle.photoUri != null) {
                    AsyncImage(
                        model = bottle.photoUri,
                        contentDescription = bottle.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Text(
                        text = BottleTypes.getEmojiForType(bottle.type),
                        style = MaterialTheme.typography.displayLarge,
                    )
                }
            }

            // 정보
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
            ) {
                Text(
                    text = bottle.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = bottle.type,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(8.dp))

                RatingStars(
                    rating = bottle.rating,
                    modifier = Modifier.height(16.dp),
                )

                if (bottle.note.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = bottle.note,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
fun RatingStars(
    rating: Float,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        repeat(5) { index ->
            val starRating = (rating - index).coerceIn(0f, 1f)
            Text(
                text =
                    when {
                        starRating >= 1f -> "⭐"
                        starRating >= 0.5f -> "⭐"
                        else -> "☆"
                    },
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "🍷",
                style = MaterialTheme.typography.displayLarge,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "아직 추가된 술이 없습니다",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "+ 버튼을 눌러 첫 번째 술을 추가해보세요",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun rememberScrollState() = androidx.compose.foundation.rememberScrollState()
