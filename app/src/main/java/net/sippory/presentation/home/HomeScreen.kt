package net.sippory.presentation.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import net.sippory.data.entity.BottleEntity
import net.sippory.navigation.Screen
import net.sippory.presentation.add.AddBottleSheet
import net.sippory.ui.theme.SipporyTheme
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
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF0B0B10),
                        titleContentColor = Color(0xFFEDEDF5),
                        actionIconContentColor = Color(0xFFEDEDF5),
                    ),
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
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF08080C), Color(0xFF0F1017)),
                            startY = 0f,
                            endY = 1600f,
                        ),
                    )
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
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF7C7CFF),
                unfocusedBorderColor = Color(0xFF2E2E3A),
                focusedContainerColor = Color(0xFF12121C),
                unfocusedContainerColor = Color(0xFF101018),
                cursorColor = Color(0xFFB3B3FF),
                focusedTextColor = Color(0xFFEDEDF5),
                unfocusedTextColor = Color(0xFFEDEDF5),
                unfocusedLeadingIconColor = Color(0xFFB8B8C6),
                focusedLeadingIconColor = Color(0xFFEDEDF5),
                focusedPlaceholderColor = Color(0xFF7D7D92),
                unfocusedPlaceholderColor = Color(0xFF6F7085),
            ),
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
            colors = chipColors(selectedFilter is BottleFilter.All),
        )

        FilterChip(
            selected = selectedFilter is BottleFilter.Wishlist,
            onClick = { onFilterChange(BottleFilter.Wishlist) },
            label = { Text("💝 위시리스트") },
            colors = chipColors(selectedFilter is BottleFilter.Wishlist),
        )

        FilterChip(
            selected = selectedFilter is BottleFilter.Owned,
            onClick = { onFilterChange(BottleFilter.Owned) },
            label = { Text("🍾 소유") },
            colors = chipColors(selectedFilter is BottleFilter.Owned),
        )

        BottleTypes.ALL_TYPES.take(5).forEach { (type, emoji) ->
            FilterChip(
                selected = selectedFilter is BottleFilter.ByType && selectedFilter.type == type,
                onClick = { onFilterChange(BottleFilter.ByType(type)) },
                label = { Text("$emoji $type") },
                colors = chipColors(selectedFilter is BottleFilter.ByType && selectedFilter.type == type),
            )
        }

        FilterChip(
            selected = selectedFilter is BottleFilter.ByRating,
            onClick = { onFilterChange(BottleFilter.ByRating(4f)) },
            label = { Text("⭐ 4점 이상") },
            colors = chipColors(selectedFilter is BottleFilter.ByRating),
        )
    }
}

@Composable
private fun chipColors(isSelected: Boolean) =
    FilterChipDefaults.filterChipColors(
        containerColor = Color(0xFF161620),
        selectedContainerColor = Color(0xFF1E1E2A),
        labelColor = if (isSelected) Color(0xFFEDEDF5) else Color(0xFFB8B8C6),
        selectedLabelColor = Color(0xFFEDEDF5),
        iconColor = Color(0xFFB8B8C6),
        selectedLeadingIconColor = Color(0xFFEDEDF5),
    )

@Composable
fun BottleGrid(
    bottles: List<BottleEntity>,
    onBottleClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val rows = bottles.chunked(2)
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        items(rows, key = { row -> row.joinToString { it.id.toString() } }) { rowBottles ->
            ShelfBottleRow(
                bottles = rowBottles,
                onBottleClick = onBottleClick,
            )
        }
    }
}

@Composable
fun ShelfBottleRow(
    bottles: List<BottleEntity>,
    onBottleClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Box(
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(Color(0x33FFFFFF), Color(0x11FFFFFF)),
                        ),
                    )
                    .border(
                        width = 1.dp,
                        color = Color(0x26FFFFFF),
                        shape = RoundedCornerShape(26.dp),
                    ),
        )

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.Bottom,
        ) {
            bottles.forEach { bottle ->
                ShelfBottleTile(
                    bottle = bottle,
                    onClick = { onBottleClick(bottle.id) },
                    modifier = Modifier.weight(1f),
                )
            }

            if (bottles.size == 1) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun ShelfBottleTile(
    bottle: BottleEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .padding(horizontal = 4.dp)
                    .clickable(onClick = onClick),
        ) {
            // Back glow behind the glass case
            Box(
                modifier =
                    Modifier
                        .align(Alignment.Center)
                        .size(170.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0x332E9AFE), Color.Transparent),
                            radius = 220f,
                        ),
                        shape = RoundedCornerShape(40.dp),
                    ),
            )

            // Info plaque
            Column(
                modifier =
                    Modifier
                        .align(Alignment.TopCenter)
                        .padding(horizontal = 6.dp, vertical = 6.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF1B1B27), Color(0xFF12121A)),
                            ),
                        )
                        .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(22.dp))
                        .padding(horizontal = 16.dp, vertical = 14.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = bottle.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color(0xFFEDEDF5),
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.3.sp,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    RatingPill(rating = bottle.rating)
                }

                Spacer(modifier = Modifier.height(8.dp))

                TypePill(type = bottle.type)
            }

            // Glass case for the icon/photo
            Box(
                modifier =
                    Modifier
                        .align(Alignment.Center)
                        .offset(y = 10.dp)
                        .size(width = 148.dp, height = 150.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF181824), Color(0xFF0F0F18)),
                            ),
                        )
                        .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(28.dp))
                        .shadow(
                            elevation = 18.dp,
                            spotColor = Color(0x33000000),
                            ambientColor = Color(0x22000000),
                            shape = RoundedCornerShape(28.dp),
                        )
                        .padding(14.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier =
                        Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0x332E9AFE), Color.Transparent),
                                radius = 180f,
                            ),
                            shape = RoundedCornerShape(22.dp),
                        )
                            .padding(10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    if (bottle.photoUri != null) {
                        AsyncImage(
                            model = bottle.photoUri,
                            contentDescription = bottle.name,
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(18.dp))
                                    .border(
                                        1.dp,
                                        Color(0x22FFFFFF),
                                        RoundedCornerShape(18.dp),
                                    ),
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        Text(
                            text = BottleTypes.getEmojiForType(bottle.type),
                            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 42.sp),
                        )
                    }
                }
            }

            // Pedestal the icon stands on
            Box(
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 12.dp)
                        .width(128.dp)
                        .height(44.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF262633), Color(0xFF15151E)),
                            ),
                        )
                        .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(18.dp))
                        .shadow(
                            elevation = 20.dp,
                            spotColor = Color(0x44000000),
                            ambientColor = Color(0x22000000),
                            shape = RoundedCornerShape(18.dp),
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = bottle.type.ifBlank { "Base" }.uppercase(),
                    style =
                        MaterialTheme.typography.labelLarge.copy(
                            color = Color(0xFFEDEDF5),
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun TypePill(type: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(18.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0x332E9AFE))
                    .border(1.dp, Color(0x662E9AFE), RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = BottleTypes.getEmojiForType(type),
                style = MaterialTheme.typography.labelLarge,
            )
        }

        Text(
            text = type,
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color(0xFFB5B5C5),
                fontWeight = FontWeight.Medium,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun RatingPill(rating: Float) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF1F1F2C),
        border = BorderStroke(1.dp, Color(0x33FFFFFF)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFFFFD166),
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = String.format("%.1f", rating),
                style = MaterialTheme.typography.labelLarge.copy(
                    color = Color(0xFFEDEDF5),
                    fontWeight = FontWeight.SemiBold,
                ),
            )
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

private data class ShelfAction(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val accent: Color,
)

@Preview(showBackground = true, backgroundColor = 0xFF0B0B0F)
@Composable
private fun HomeShelfConceptPreview() {
    val actions =
        listOf(
            ShelfAction("위스키 노트", "오늘의 위스키 기록하기", Icons.Default.LocalBar, Color(0xFFFFC857)),
            ShelfAction("페어링", "안주 추천 받기", Icons.Default.Favorite, Color(0xFF80CBC4)),
            ShelfAction("검색/탐색", "새 술 찾기", Icons.Default.Search, Color(0xFF90CAF9)),
            ShelfAction("내 취향", "추천·랭킹 보기", Icons.Default.ThumbUp, Color(0xFFD7BDE2)),
        )

    SipporyTheme(darkTheme = true) {
        HomeShelfConcept(actions = actions)
    }
}

@Composable
private fun HomeShelfConcept(
    actions: List<ShelfAction>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF0B0B0F), Color(0xFF12121A)),
                    ),
                )
                .padding(horizontal = 20.dp, vertical = 28.dp),
    ) {
        Text(
            text = "Sippory Home",
            color = Color(0xFFEDEDF5),
            style = MaterialTheme.typography.titleMedium,
            letterSpacing = 0.5.sp,
        )
        Text(
            text = "밤의 홈바 무드에 어울리는 블랙 톤 + 아이콘 선반 레이아웃",
            color = Color(0xFFB8B8C6),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp),
        )

        actions.chunked(2).forEach { rowItems ->
            ShelfRow(items = rowItems)
        }
    }
}

@Composable
private fun ShelfRow(items: List<ShelfAction>) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Box(
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(Color(0x33FFFFFF), Color(0x11FFFFFF)),
                        ),
                        shape = RoundedCornerShape(24.dp),
                    ),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(18.dp, Alignment.CenterHorizontally),
        ) {
            items.forEach { action ->
                ShelfTile(action = action, modifier = Modifier.weight(1f))
            }

            if (items.size == 1) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ShelfTile(
    action: ShelfAction,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier =
                Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(action.accent.copy(alpha = 0.18f))
                    .border(
                        width = 1.dp,
                        color = action.accent.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(24.dp),
                    )
                    .padding(18.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(72.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(action.accent.copy(alpha = 0.6f), Color.Transparent),
                            ),
                            shape = RoundedCornerShape(18.dp),
                        )
                        .padding(12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = action.icon,
                    contentDescription = action.title,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = action.title,
            color = Color(0xFFEDEDF5),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        )
        Text(
            text = action.subtitle,
            color = Color(0xFFB8B8C6),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}
