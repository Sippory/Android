package net.sippory.presentation.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
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
    onSearchClick: () -> Unit,
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
                    IconButton(onClick = { showSearchBar = !showSearchBar }) {
                        Icon(Icons.Default.Search, contentDescription = "검색")
                    }

                    IconButton(onClick = {
                        navController.navigate(Screen.Search.route)
                    }) {
                        Icon(imageVector = Icons.Default.Book, contentDescription = "술 검색")
                    }
                },
            )
        },
        floatingActionButton = {
            ExpandableFAB(
                onAddBottleClick = { showAddBottleSheet = true },
                onTasteFinderClick = { navController.navigate(Screen.TasteFinder.route) },
                onDashboardClick = onDashboardClick,
                onAIRecommendClick = { navController.navigate(Screen.AIRecommend.route) },
            )
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

@OptIn(ExperimentalMaterial3Api::class)
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
fun ExpandableFAB(
    onAddBottleClick: () -> Unit,
    onTasteFinderClick: () -> Unit,
    onDashboardClick: () -> Unit,
    onAIRecommendClick: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 45f else 0f,
        label = "FAB rotation",
    )

    // 메뉴 아이템 리스트
    val menuItems =
        remember {
            listOf(
                Triple(Icons.Default.Add, "술 추가", onAddBottleClick),
                Triple(Icons.Default.Favorite, "취향 찾기", onTasteFinderClick),
                Triple(Icons.Default.ThumbUp, "대시보드", onDashboardClick),
                Triple(Icons.Default.Email, "AI 추천", onAIRecommendClick),
            )
        }

    Box(
        modifier = Modifier.size(250.dp),
        contentAlignment = Alignment.BottomEnd,
    ) {
        // 서브 메뉴 아이템들 - 아치형 배치 (오른쪽 아래에서 왼쪽 위로 펼치기)
        menuItems.forEachIndexed { index, (icon, label, onClick) ->
            val targetAngle = 90f - (index * 30f)
            val radius = 100.dp

            AnimatedFABMenuItem(
                icon = icon,
                label = label,
                onClick = {
                    onClick()
                    expanded = false
                },
                expanded = expanded,
                angle = targetAngle,
                radius = radius,
                index = index,
            )
        }

        // 메인 FAB
        FloatingActionButton(
            onClick = { expanded = !expanded },
            containerColor = MaterialTheme.colorScheme.primary,
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "메뉴",
                modifier = Modifier.rotate(rotation),
            )
        }
    }
}

@Composable
fun AnimatedFABMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    expanded: Boolean,
    angle: Float,
    radius: Dp,
    index: Int,
) {
    // 각도를 라디안으로 변환
    val angleInRadians = (angle * Math.PI / 180f).toFloat()

    // 극좌표를 직교좌표로 변환 (시계 모양 배치)
    val targetX = if (expanded) -(radius.value * kotlin.math.cos(angleInRadians)) else 0f
    val targetY = if (expanded) -(radius.value * kotlin.math.sin(angleInRadians)) else 0f

    // 애니메이션
    val offsetX by animateDpAsState(
        targetValue = targetX.dp,
        animationSpec =
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow,
            ),
        label = "offsetX",
    )

    val offsetY by animateDpAsState(
        targetValue = targetY.dp,
        animationSpec =
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow,
            ),
        label = "offsetY",
    )

    val alpha by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec =
            tween(
                durationMillis = 200,
                delayMillis = if (expanded) index * 50 else 0,
            ),
        label = "alpha",
    )

    val scale by animateFloatAsState(
        targetValue = if (expanded) 1f else 0.3f,
        animationSpec =
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium,
            ),
        label = "scale",
    )

    Box(
        modifier =
            Modifier
                .offset(x = offsetX, y = offsetY)
                .graphicsLayer {
                    this.alpha = alpha
                    scaleX = scale
                    scaleY = scale
                },
    ) {
        if (expanded) {
            // 작은 FAB만 표시 (라벨 제거)
            SmallFloatingActionButton(
                onClick = onClick,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                )
            }
        }
    }
}

@Composable
fun FABMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // 라벨
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp,
        ) {
            Text(
                text = label,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
        }

        // 작은 FAB
        SmallFloatingActionButton(
            onClick = onClick,
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
            )
        }
    }
}

@Composable
private fun rememberScrollState() = androidx.compose.foundation.rememberScrollState()
