package net.sippory.presentation.add

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import net.sippory.data.repository.BottleRepository
import net.sippory.utils.BottleTypes
import net.sippory.utils.BottleViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBottleSheet(
    onDismiss: () -> Unit,
    repository: BottleRepository,
) {
    val viewModelFactory = BottleViewModelFactory(repository)
    val viewModel: AddBottleViewModel = viewModel(factory = viewModelFactory)
    val uiState by viewModel.uiState.collectAsState()

    var showTypeDropdown by remember { mutableStateOf(false) }

    val imagePickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
        ) { uri: Uri? ->
            uri?.let { viewModel.updatePhotoUri(it.toString()) }
        }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            onDismiss()
            viewModel.resetState()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp)
                    .verticalScroll(rememberScrollState()),
        ) {
            // 헤더
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "새 술 추가",
                    style = MaterialTheme.typography.headlineSmall,
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "닫기")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 사진 선택
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center,
            ) {
                if (uiState.photoUri != null) {
                    AsyncImage(
                        model = uiState.photoUri,
                        contentDescription = "선택된 사진",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "📷",
                            style = MaterialTheme.typography.displayMedium,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "사진 추가",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 이름
            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::updateName,
                label = { Text("이름 *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 종류
            ExposedDropdownMenuBox(
                expanded = showTypeDropdown,
                onExpandedChange = { showTypeDropdown = it },
            ) {
                OutlinedTextField(
                    value = "${BottleTypes.getEmojiForType(uiState.type)} ${uiState.type}",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("종류") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showTypeDropdown) },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                )
                ExposedDropdownMenu(
                    expanded = showTypeDropdown,
                    onDismissRequest = { showTypeDropdown = false },
                ) {
                    BottleTypes.ALL_TYPES.forEach { (type, emoji) ->
                        DropdownMenuItem(
                            text = { Text("$emoji $type") },
                            onClick = {
                                viewModel.updateType(type)
                                showTypeDropdown = false
                            },
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ABV와 Country
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                OutlinedTextField(
                    value = uiState.abv,
                    onValueChange = viewModel::updateAbv,
                    label = { Text("도수 (%)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                )

                OutlinedTextField(
                    value = uiState.country,
                    onValueChange = viewModel::updateCountry,
                    label = { Text("원산지") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 평점
            Text(
                text = "평점: ${String.format("%.1f", uiState.rating)}",
                style = MaterialTheme.typography.bodyMedium,
            )
            Slider(
                value = uiState.rating,
                onValueChange = viewModel::updateRating,
                valueRange = 0.5f..5f,
                steps = 8,
                modifier = Modifier.fillMaxWidth(),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("⭐ 0.5", style = MaterialTheme.typography.bodySmall)
                Text("⭐⭐⭐⭐⭐ 5.0", style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 노트
            OutlinedTextField(
                value = uiState.note,
                onValueChange = viewModel::updateNote,
                label = { Text("메모") },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                maxLines = 5,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 기록한 장소
            Text(
                text = "기록한 장소",
                style = MaterialTheme.typography.titleMedium,
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.locationName,
                onValueChange = viewModel::updateLocationName,
                label = { Text("장소 이름") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    value = uiState.latitudeInput,
                    onValueChange = viewModel::updateLatitude,
                    label = { Text("위도") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                )

                OutlinedTextField(
                    value = uiState.longitudeInput,
                    onValueChange = viewModel::updateLongitude,
                    label = { Text("경도") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "지도 앱에서 좌표를 복사해 붙여넣을 수 있어요",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 위시리스트 체크박스
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.toggleWishlist() },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = uiState.isWishlist,
                    onCheckedChange = null,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "위시리스트에 추가 (아직 구매하지 않은 술)",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 에러 메시지
            uiState.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            }

            // 저장 버튼
            Button(
                onClick = { viewModel.saveBottle() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSaving,
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Text("저장")
                }
            }
        }
    }
}
