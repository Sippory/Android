package net.sippory.presentation.tastefinder

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import net.sippory.data.model.RecommendedBottle
import net.sippory.data.model.TasteQuestion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasteFinderScreen(
    viewModel: TasteFinderViewModel,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val progress by animateFloatAsState(
        targetValue = viewModel.getProgress(),
        animationSpec = tween(durationMillis = 300),
        label = "progress",
    )

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(androidx.compose.ui.graphics.Color.Black),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            // 커스텀 상단 바
            if (!uiState.isCompleted) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = {
                        if (uiState.currentQuestionIndex > 0) {
                            viewModel.goBack()
                        } else {
                            onBack()
                        }
                    }) {
                        Icon(
                            Icons.Default.ChevronLeft,
                            contentDescription = "Back",
                            tint = androidx.compose.ui.graphics.Color.White,
                        )
                    }

                    // 사용자 아이콘 영역 (선택사항)
                    Spacer(modifier = Modifier.size(48.dp))
                }

                // 진행률 표시
                Text(
                    text = "Question ${uiState.currentQuestionIndex + 1} of ${uiState.questions.size}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = androidx.compose.ui.graphics.Color.White,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 프로그레스 바
                LinearProgressIndicator(
                    progress = { progress },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .height(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = androidx.compose.ui.graphics.Color.DarkGray,
                )
            } else {
                // 결과 화면 상단바
                TopAppBar(
                    title = {
                        Text("Your Results", fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.White)
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = "Back", tint = androidx.compose.ui.graphics.Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.restart() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Restart", tint = androidx.compose.ui.graphics.Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = androidx.compose.ui.graphics.Color.Black,
                    ),
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 컨텐츠
            AnimatedContent(
                targetState = uiState.isCompleted,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith
                        fadeOut(animationSpec = tween(300))
                },
                label = "content",
            ) { isCompleted ->
                if (isCompleted) {
                    // 추천 결과 화면
                    RecommendationResultScreen(
                        recommendations = uiState.recommendations,
                        onAddToWishlist = { bottle ->
                            viewModel.addToWishlist(bottle)
                        },
                        isLoading = uiState.isLoading,
                        addedBottleNames = uiState.addedBottleNames,
                    )
                } else {
                    // 질문 화면
                    val question = uiState.questions.getOrNull(uiState.currentQuestionIndex)
                    question?.let {
                        QuestionScreen(
                            question = it,
                            onSelectOption = viewModel::selectOption,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestionScreen(
    question: TasteQuestion,
    onSelectOption: (Boolean) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(androidx.compose.ui.graphics.Color.Black)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // 제목
        Text(
            text = "Taste Finder",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = androidx.compose.ui.graphics.Color.White,
            modifier = Modifier.padding(bottom = 32.dp),
        )

        // 질문 텍스트
        Text(
            text = question.question,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            color = androidx.compose.ui.graphics.Color.White,
            modifier = Modifier.padding(bottom = 48.dp),
        )

        // A/B 선택지
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Option A
            ModernOptionCard(
                label = "A",
                text = question.optionA.text,
                onClick = { onSelectOption(true) },
            )

            // Option B
            ModernOptionCard(
                label = "B",
                text = question.optionB.text,
                onClick = { onSelectOption(false) },
            )
        }
    }
}

@Composable
private fun OptionCard(
    text: String,
    description: String,
    gradientColors: List<androidx.compose.ui.graphics.Color>,
    onClick: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp)
                .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(gradientColors),
                    )
                    .padding(20.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                )
            }
        }
    }
}

@Composable
private fun ModernOptionCard(
    label: String,
    text: String,
    onClick: () -> Unit,
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec =
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessHigh,
            ),
        label = "scale",
    )

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .clickable {
                    isPressed = true
                    onClick()
                },
        shape = RoundedCornerShape(12.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = androidx.compose.ui.graphics.Color(0xFF2C2C2E),
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f),
            ) {
                // 라벨 원
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = androidx.compose.foundation.shape.CircleShape,
                    color = androidx.compose.ui.graphics.Color(0xFF1C1C1E),
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = androidx.compose.ui.graphics.Color.White,
                        )
                    }
                }

                // 텍스트
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = androidx.compose.ui.graphics.Color.White,
                )
            }

            // 라디오 버튼
            Surface(
                modifier = Modifier.size(28.dp),
                shape = androidx.compose.foundation.shape.CircleShape,
                color = androidx.compose.ui.graphics.Color.Transparent,
                border =
                    androidx.compose.foundation.BorderStroke(
                        2.dp,
                        androidx.compose.ui.graphics.Color.White.copy(alpha = 0.3f),
                    ),
            ) {
                Box(modifier = Modifier.fillMaxSize())
            }
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(100)
            isPressed = false
        }
    }
}

// 커스텀 컬러 (AIRecommendScreen과 동일)
private val WineRed = androidx.compose.ui.graphics.Color(0xFF8B1538)
private val DarkWine = androidx.compose.ui.graphics.Color(0xFF5D0E28)
private val DeepBlack = androidx.compose.ui.graphics.Color(0xFF0D0D0D)
private val SoftBlack = androidx.compose.ui.graphics.Color(0xFF1A1A1A)
private val LightGray = androidx.compose.ui.graphics.Color(0xFFB0B0B0)

@Composable
private fun RecommendationResultScreen(
    recommendations: List<RecommendedBottle>,
    onAddToWishlist: (RecommendedBottle) -> Unit,
    isLoading: Boolean,
    addedBottleNames: Set<String> = emptySet(),
) {
    LazyColumn(
        modifier =
            Modifier
                .fillMaxSize()
                .background(DeepBlack)
                .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        itemsIndexed(recommendations) { index, bottle ->
            AnimatedRecommendationCard(
                bottle = bottle,
                onAddToWishlist = { onAddToWishlist(bottle) },
                isLoading = isLoading,
                isAdded = bottle.name in addedBottleNames,
                index = index,
            )
        }
    }
}

@Composable
private fun AnimatedRecommendationCard(
    bottle: RecommendedBottle,
    onAddToWishlist: () -> Unit,
    isLoading: Boolean,
    isAdded: Boolean = false,
    index: Int,
) {
    // 각 카드가 순차적으로 나타나는 웨이브 효과
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(index * 100L)
        visible = true
    }

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.8f,
        animationSpec =
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow,
            ),
        label = "scale",
    )

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "alpha",
    )

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    this.alpha = alpha
                },
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
            // 그라데이션 배경 효과 (상단)
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
                    bottle.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = androidx.compose.ui.graphics.Color.White,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 정보 섹션
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    InfoChip(label = "Type", value = bottle.subType)
                    InfoChip(label = "ABV", value = "${bottle.abv}%")
                }

                Spacer(modifier = Modifier.height(12.dp))

                InfoChip(label = "Origin", value = bottle.country)

                Spacer(modifier = Modifier.height(20.dp))

                // 구분선
                HorizontalDivider(
                    color = WineRed.copy(alpha = 0.3f),
                    thickness = 1.dp,
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 설명
                Column {
                    Text(
                        "Why We Recommend",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = WineRed,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        bottle.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = LightGray,
                        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight.times(1.4f),
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 위시리스트 추가 버튼
                Button(
                    onClick = onAddToWishlist,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isAdded && !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WineRed,
                        contentColor = androidx.compose.ui.graphics.Color.White,
                    ),
                ) {
                    Icon(
                        imageVector = if (isAdded) Icons.Default.Check else Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = if (isAdded) "Added to Wishlist" else "Add to Wishlist",
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoChip(
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
            color = androidx.compose.ui.graphics.Color.White,
            fontWeight = FontWeight.Medium,
        )
    }
}
