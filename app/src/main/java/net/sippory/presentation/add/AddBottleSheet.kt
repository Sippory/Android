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
    drinkName: String = "",
    drinkType: String = "Wine",
    drinkPhotoUri: String? = null,
    onSaveBottle: () -> Unit = {},
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
    LaunchedEffect(Unit) {
        viewModel.updateName(drinkName)
        viewModel.updateType(drinkType)
        viewModel.updatePhotoUri(drinkPhotoUri)
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
        containerColor = androidx.compose.ui.graphics.Color.Black,
        contentColor = androidx.compose.ui.graphics.Color.White,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(androidx.compose.ui.graphics.Color.Black)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp)
                    .verticalScroll(rememberScrollState()),
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Add New Drink",
                    style = MaterialTheme.typography.headlineSmall,
                    color = androidx.compose.ui.graphics.Color.White,
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close",
                        tint = androidx.compose.ui.graphics.Color.White,
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Photo Section
            Text(
                text = "📷 Photo",
                style = MaterialTheme.typography.titleMedium,
                color = androidx.compose.ui.graphics.Color.White,
                modifier = Modifier.padding(bottom = 12.dp),
            )

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(androidx.compose.ui.graphics.Color(0xFF1C1C1E))
                        .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center,
            ) {
                if (uiState.photoUri != null) {
                    AsyncImage(
                        model = uiState.photoUri,
                        contentDescription = "Selected photo",
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
                            text = "Add Photo",
                            style = MaterialTheme.typography.bodyMedium,
                            color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.6f),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Basic Information Section
            Text(
                text = "📝 Basic Information",
                style = MaterialTheme.typography.titleMedium,
                color = androidx.compose.ui.graphics.Color.White,
                modifier = Modifier.padding(bottom = 12.dp),
            )

            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::updateName,
                label = { Text("Name *", color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.6f)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors =
                    OutlinedTextFieldDefaults.colors(
                        focusedTextColor = androidx.compose.ui.graphics.Color.White,
                        unfocusedTextColor = androidx.compose.ui.graphics.Color.White,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.3f),
                        cursorColor = MaterialTheme.colorScheme.primary,
                    ),
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = showTypeDropdown,
                onExpandedChange = { showTypeDropdown = it },
            ) {
                OutlinedTextField(
                    value = "${BottleTypes.getEmojiForType(uiState.type)} ${uiState.type}",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Type", color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.6f)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showTypeDropdown) },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    colors =
                        OutlinedTextFieldDefaults.colors(
                            focusedTextColor = androidx.compose.ui.graphics.Color.White,
                            unfocusedTextColor = androidx.compose.ui.graphics.Color.White,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.3f),
                        ),
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                OutlinedTextField(
                    value = uiState.abv,
                    onValueChange = viewModel::updateAbv,
                    label = { Text("ABV (%)", color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.6f)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors =
                        OutlinedTextFieldDefaults.colors(
                            focusedTextColor = androidx.compose.ui.graphics.Color.White,
                            unfocusedTextColor = androidx.compose.ui.graphics.Color.White,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.3f),
                            cursorColor = MaterialTheme.colorScheme.primary,
                        ),
                )

                OutlinedTextField(
                    value = uiState.country,
                    onValueChange = viewModel::updateCountry,
                    label = { Text("Country", color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.6f)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors =
                        OutlinedTextFieldDefaults.colors(
                            focusedTextColor = androidx.compose.ui.graphics.Color.White,
                            unfocusedTextColor = androidx.compose.ui.graphics.Color.White,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.3f),
                            cursorColor = MaterialTheme.colorScheme.primary,
                        ),
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Rating Section
            Text(
                text = "⭐ Rating",
                style = MaterialTheme.typography.titleMedium,
                color = androidx.compose.ui.graphics.Color.White,
                modifier = Modifier.padding(bottom = 16.dp),
            )

            // Star Rating (Click to rate - whole stars only)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    repeat(5) { index ->
                        val starIndex = index + 1
                        val isFilled = uiState.rating >= starIndex

                        Text(
                            text = if (isFilled) "⭐" else "☆",
                            style = MaterialTheme.typography.displayMedium,
                            modifier =
                                Modifier
                                    .clickable {
                                        viewModel.updateRating(starIndex.toFloat())
                                    }
                                    .padding(horizontal = 6.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "${uiState.rating.toInt()} / 5",
                    style = MaterialTheme.typography.bodyLarge,
                    color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f),
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Notes Section
            Text(
                text = "📝 Notes",
                style = MaterialTheme.typography.titleMedium,
                color = androidx.compose.ui.graphics.Color.White,
                modifier = Modifier.padding(bottom = 12.dp),
            )

            OutlinedTextField(
                value = uiState.note,
                onValueChange = viewModel::updateNote,
                label = { Text("Your notes", color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.6f)) },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                maxLines = 5,
                colors =
                    OutlinedTextFieldDefaults.colors(
                        focusedTextColor = androidx.compose.ui.graphics.Color.White,
                        unfocusedTextColor = androidx.compose.ui.graphics.Color.White,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.3f),
                        cursorColor = MaterialTheme.colorScheme.primary,
                    ),
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Location Section
            Text(
                text = "📍 Location",
                style = MaterialTheme.typography.titleMedium,
                color = androidx.compose.ui.graphics.Color.White,
                modifier = Modifier.padding(bottom = 12.dp),
            )

            OutlinedTextField(
                value = uiState.locationName,
                onValueChange = viewModel::updateLocationName,
                label = { Text("Location name", color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.6f)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors =
                    OutlinedTextFieldDefaults.colors(
                        focusedTextColor = androidx.compose.ui.graphics.Color.White,
                        unfocusedTextColor = androidx.compose.ui.graphics.Color.White,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.3f),
                        cursorColor = MaterialTheme.colorScheme.primary,
                    ),
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Wishlist Section
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.toggleWishlist() }
                        .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = uiState.isWishlist,
                    onCheckedChange = null,
                    colors =
                        CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary,
                            uncheckedColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.6f),
                            checkmarkColor = androidx.compose.ui.graphics.Color.White,
                        ),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Add to wishlist (Not purchased yet)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = androidx.compose.ui.graphics.Color.White,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Error message
            uiState.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            }

            // Save button
            Button(
                onClick = {
                    viewModel.saveBottle()
                    onSaveBottle()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSaving,
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Text("Save")
                }
            }
        }
    }
}
