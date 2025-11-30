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
    val backgroundColor = MaterialTheme.colorScheme.background
    val onBackgroundColor = MaterialTheme.colorScheme.onBackground
    val progress by animateFloatAsState(
        targetValue = viewModel.getProgress(),
        animationSpec = tween(durationMillis = 300),
        label = "progress",
    )

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(backgroundColor),
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
                            tint = onBackgroundColor,
                        )
                    }

                    // 사용자 아이콘 영역 (선택사항)
                    Spacer(modifier = Modifier.size(48.dp))
                }

                // 진행률 표시
                Text(
                    text = "Question ${uiState.currentQuestionIndex + 1} of ${uiState.questions.size}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = onBackgroundColor,
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
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            } else {
                // 결과 화면 상단바
                TopAppBar(
                    title = {
                        Text("Your Results", fontWeight = FontWeight.Bold)
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.restart() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Restart")
                        }
                    },
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
    val backgroundColor = MaterialTheme.colorScheme.background
    val onBackgroundColor = MaterialTheme.colorScheme.onBackground

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(backgroundColor)
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
            color = onBackgroundColor,
            modifier = Modifier.padding(bottom = 32.dp),
        )

        // 질문 텍스트
        Text(
            text = question.question,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            color = onBackgroundColor,
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
                    color = MaterialTheme.colorScheme.surfaceVariant,
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }

                // 텍스트
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
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
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
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
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(
                text = "🎉 Recommended for You",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            Text(
                text = "Based on your preferences, here are some drinks we think you'll love!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp),
            )
        }

        itemsIndexed(recommendations) { index, bottle ->
            AnimatedRecommendationCard(
                bottle = bottle,
                onAddToWishlist = { onAddToWishlist(bottle) },
                isLoading = isLoading,
                isAdded = bottle.name in addedBottleNames,
                index = index,
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    Text(
                        text = "💡 Like what you see?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Add to your wishlist to purchase later,\nor try them and record your experience!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
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
        delay(index * 100L) // 각 카드마다 100ms 지연
        visible = true
    }

    // 스케일 + 회전 애니메이션
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.8f,
        animationSpec =
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow,
            ),
        label = "scale",
    )

    val rotation by animateFloatAsState(
        targetValue = if (visible) 0f else -5f,
        animationSpec =
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow,
            ),
        label = "rotation",
    )

    // 슬라이드 + 페이드 효과
    val offsetX by animateDpAsState(
        targetValue = if (visible) 0.dp else 50.dp,
        animationSpec =
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow,
            ),
        label = "offsetX",
    )

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "alpha",
    )

    // 버튼 클릭 시 펄스 애니메이션
    var buttonPressed by remember { mutableStateOf(false) }
    val buttonScale by animateFloatAsState(
        targetValue = if (buttonPressed) 0.95f else 1f,
        animationSpec =
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessHigh,
            ),
        label = "buttonScale",
    )

    LaunchedEffect(buttonPressed) {
        if (buttonPressed) {
            delay(150)
            buttonPressed = false
        }
    }

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    rotationZ = rotation
                    translationX = offsetX.toPx()
                    this.alpha = alpha
                },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = bottle.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = bottle.subType,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                // ABV 표시
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(
                        text = "${bottle.abv}%",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 원산지
            Text(
                text = "🌍 ${bottle.country}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 설명
            Text(
                text = bottle.description,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 위시리스트 추가 버튼
            Button(
                onClick = {
                    buttonPressed = true
                    onAddToWishlist()
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .scale(buttonScale),
                enabled = !isAdded && !isLoading,
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
