package net.sippory.presentation.tastefinder

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (uiState.isCompleted) "추천 결과" else "나에게 맞는 술 찾기",
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (uiState.currentQuestionIndex > 0 && !uiState.isCompleted) {
                            viewModel.goBack()
                        } else {
                            onBack()
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                actions = {
                    if (uiState.isCompleted) {
                        IconButton(onClick = { viewModel.restart() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "다시 시작")
                        }
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding),
        ) {
            // 진행률 표시
            if (!uiState.isCompleted) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "질문 ${uiState.currentQuestionIndex + 1} / ${uiState.questions.size}",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        // 질문 텍스트
        Text(
            text = question.question,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp),
        )

        // A/B 선택지
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Option A
            OptionCard(
                text = question.optionA.text,
                description = question.optionA.description,
                gradientColors =
                    listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.secondaryContainer,
                    ),
                onClick = { onSelectOption(true) },
            )

            // Option B
            OptionCard(
                text = question.optionB.text,
                description = question.optionB.description,
                gradientColors =
                    listOf(
                        MaterialTheme.colorScheme.tertiaryContainer,
                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                    ),
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
                text = "🎉 당신에게 추천하는 술",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            Text(
                text = "선택하신 취향을 바탕으로 이런 술들을 추천드려요!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp),
            )
        }

        items(recommendations) { bottle ->
            RecommendationCard(
                bottle = bottle,
                onAddToWishlist = { onAddToWishlist(bottle) },
                isLoading = isLoading,
                isAdded = bottle.name in addedBottleNames,
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
                        text = "💡 추천 받은 술이 마음에 드시나요?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "위시리스트에 추가하여 나중에 구매하거나,\n직접 마셔보고 기록을 남겨보세요!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun RecommendationCard(
    bottle: RecommendedBottle,
    onAddToWishlist: () -> Unit,
    isLoading: Boolean,
    isAdded: Boolean = false,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                onClick = onAddToWishlist,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isAdded && !isLoading,
            ) {
                Icon(
                    imageVector = if (isAdded) Icons.Default.Check else Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = if (isAdded) "위시리스트에 추가됨" else "위시리스트에 추가",
                )
            }
        }
    }
}
