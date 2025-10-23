package net.sippory.presentation.detail

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import net.sippory.data.entity.BottleEntity
import net.sippory.utils.BottleTypes
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    bottleId: Int,
    viewModel: DetailViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(bottleId) {
        viewModel.loadBottle(bottleId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("상세 정보") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::toggleEditMode) {
                        Icon(
                            if (uiState.isEditing) Icons.Default.Close else Icons.Default.Edit,
                            contentDescription = if (uiState.isEditing) "취소" else "수정"
                        )
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "삭제")
                    }
                }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.bottle == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "술 정보를 찾을 수 없습니다",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            else -> {
                BottleDetailContent(
                    bottle = uiState.bottle!!,
                    isEditing = uiState.isEditing,
                    onUpdate = viewModel::updateBottle,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                )
            }
        }
    }

    // 삭제 확인 다이얼로그
    if (showDeleteDialog && uiState.bottle != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("삭제 확인") },
            text = { Text("'${uiState.bottle!!.name}'을(를) 삭제하시겠습니까?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteBottle(uiState.bottle!!)
                        showDeleteDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text("삭제", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("취소")
                }
            }
        )
    }
}

@Composable
fun BottleDetailContent(
    bottle: BottleEntity,
    isEditing: Boolean,
    onUpdate: (BottleEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var editedBottle by remember(bottle) { mutableStateOf(bottle) }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // 이미지
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (bottle.photoUri != null) {
                AsyncImage(
                    model = bottle.photoUri,
                    contentDescription = bottle.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = BottleTypes.getEmojiForType(bottle.type),
                    style = MaterialTheme.typography.displayLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 이름
        if (isEditing) {
            OutlinedTextField(
                value = editedBottle.name,
                onValueChange = { editedBottle = editedBottle.copy(name = it) },
                label = { Text("이름") },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Text(
                text = bottle.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 종류
        DetailRow(
            label = "종류",
            value = "${BottleTypes.getEmojiForType(bottle.type)} ${bottle.type}"
        )

        // 도수
        bottle.abv?.let {
            DetailRow(
                label = "도수",
                value = "${it}%"
            )
        }

        // 원산지
        bottle.country?.let {
            DetailRow(
                label = "원산지",
                value = it
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 평점
        if (isEditing) {
            Text(
                text = "평점: ${String.format("%.1f", editedBottle.rating)}",
                style = MaterialTheme.typography.bodyLarge
            )
            Slider(
                value = editedBottle.rating,
                onValueChange = { editedBottle = editedBottle.copy(rating = it) },
                valueRange = 0.5f..5f,
                steps = 8
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "평점: ",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                RatingDisplay(rating = bottle.rating)
                Text(
                    text = " ${String.format("%.1f", bottle.rating)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider()

        Spacer(modifier = Modifier.height(16.dp))

        // 메모
        Text(
            text = "메모",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (isEditing) {
            OutlinedTextField(
                value = editedBottle.note,
                onValueChange = { editedBottle = editedBottle.copy(note = it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                maxLines = 8
            )
        } else {
            Text(
                text = bottle.note.ifBlank { "메모가 없습니다." },
                style = MaterialTheme.typography.bodyMedium,
                color = if (bottle.note.isBlank())
                    MaterialTheme.colorScheme.onSurfaceVariant
                else
                    MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 생성/수정 일시
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일 HH:mm", Locale.KOREAN)
        Text(
            text = "생성: ${dateFormat.format(Date(bottle.createdAt))}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (bottle.updatedAt != bottle.createdAt) {
            Text(
                text = "수정: ${dateFormat.format(Date(bottle.updatedAt))}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // 수정 버튼
        if (isEditing) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { onUpdate(editedBottle) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("저장")
            }
        }
    }
}

@Composable
fun DetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun RatingDisplay(
    rating: Float,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        repeat(5) { index ->
            val starRating = (rating - index).coerceIn(0f, 1f)
            Text(
                text = when {
                    starRating >= 1f -> "⭐"
                    starRating >= 0.5f -> "⭐"
                    else -> "☆"
                },
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
