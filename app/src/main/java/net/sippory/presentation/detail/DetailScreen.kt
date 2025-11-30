package net.sippory.presentation.detail

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
    onNavigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(bottleId) {
        viewModel.loadBottle(bottleId)
    }

    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.Black,
        topBar = {
            TopAppBar(
                title = { Text("Detail", color = androidx.compose.ui.graphics.Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = androidx.compose.ui.graphics.Color.White,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::toggleWishlist) {
                        Icon(
                            if (uiState.bottle?.isWishlist == true) {
                                Icons.Default.Favorite
                            } else {
                                Icons.Default.FavoriteBorder
                            },
                            contentDescription = "Wishlist",
                            tint =
                                if (uiState.bottle?.isWishlist == true) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    androidx.compose.ui.graphics.Color.White
                                },
                        )
                    }
                    IconButton(onClick = viewModel::toggleEditMode) {
                        Icon(
                            if (uiState.isEditing) Icons.Default.Close else Icons.Default.Edit,
                            contentDescription = if (uiState.isEditing) "Cancel" else "Edit",
                            tint = androidx.compose.ui.graphics.Color.White,
                        )
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = androidx.compose.ui.graphics.Color.White,
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = androidx.compose.ui.graphics.Color.Black,
                    ),
            )
        },
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(androidx.compose.ui.graphics.Color.Black)
                            .padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = androidx.compose.ui.graphics.Color.White)
                }
            }
            uiState.bottle == null -> {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(androidx.compose.ui.graphics.Color.Black)
                            .padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Bottle not found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = androidx.compose.ui.graphics.Color.White,
                    )
                }
            }
            else -> {
                BottleDetailContent(
                    bottle = uiState.bottle!!,
                    isEditing = uiState.isEditing,
                    onUpdate = viewModel::updateBottle,
                    onRecordDrink = viewModel::recordDrink,
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(padding),
                )
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog && uiState.bottle != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    "Delete Confirmation",
                    color = androidx.compose.ui.graphics.Color.White,
                )
            },
            text = {
                Text(
                    "Are you sure you want to delete '${uiState.bottle!!.name}'?",
                    color = androidx.compose.ui.graphics.Color.White,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteBottle(uiState.bottle!!)
                        showDeleteDialog = false
                        onNavigateBack()
                    },
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = androidx.compose.ui.graphics.Color.White)
                }
            },
            containerColor = androidx.compose.ui.graphics.Color(0xFF1C1C1E),
        )
    }
}

@Composable
fun BottleDetailContent(
    bottle: BottleEntity,
    isEditing: Boolean,
    onUpdate: (BottleEntity) -> Unit,
    onRecordDrink: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var editedBottle by remember(bottle) { mutableStateOf(bottle) }

    Column(
        modifier =
            modifier
                .background(androidx.compose.ui.graphics.Color.Black)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
    ) {
        // Image
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(androidx.compose.ui.graphics.Color(0xFF1C1C1E)),
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

        Spacer(modifier = Modifier.height(24.dp))

        // Name
        if (isEditing) {
            OutlinedTextField(
                value = editedBottle.name,
                onValueChange = { editedBottle = editedBottle.copy(name = it) },
                label = { Text("Name", color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.6f)) },
                modifier = Modifier.fillMaxWidth(),
                colors =
                    OutlinedTextFieldDefaults.colors(
                        focusedTextColor = androidx.compose.ui.graphics.Color.White,
                        unfocusedTextColor = androidx.compose.ui.graphics.Color.White,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.3f),
                        cursorColor = MaterialTheme.colorScheme.primary,
                    ),
            )
        } else {
            Text(
                text = bottle.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = androidx.compose.ui.graphics.Color.White,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Type
        DetailRow(
            label = "Type",
            value = "${BottleTypes.getEmojiForType(bottle.type)} ${bottle.type}",
        )

        // ABV
        bottle.abv?.let {
            DetailRow(
                label = "ABV",
                value = "$it%",
            )
        }

        // Country
        bottle.country?.let {
            DetailRow(
                label = "Country",
                value = it,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Rating
        Text(
            text = "⭐ Rating",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = androidx.compose.ui.graphics.Color.White,
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (isEditing) {
            // Editable star rating
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                repeat(5) { index ->
                    val starIndex = index + 1
                    val isFilled = editedBottle.rating >= starIndex

                    Text(
                        text = if (isFilled) "⭐" else "☆",
                        style = MaterialTheme.typography.displaySmall,
                        modifier =
                            Modifier
                                .clickable {
                                    editedBottle = editedBottle.copy(rating = starIndex.toFloat())
                                }
                                .padding(horizontal = 4.dp),
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${editedBottle.rating.toInt()} / 5",
                style = MaterialTheme.typography.bodyLarge,
                color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f),
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                RatingDisplay(rating = bottle.rating)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${bottle.rating.toInt()} / 5",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = androidx.compose.ui.graphics.Color.White,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider(color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.2f))

        Spacer(modifier = Modifier.height(16.dp))

        // Drink Count
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors =
                CardDefaults.cardColors(
                    containerColor = androidx.compose.ui.graphics.Color(0xFF2D0A0A),
                ),
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "Drink Count",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = androidx.compose.ui.graphics.Color.White,
                    )
                    Text(
                        text = "${bottle.drinkCount} times",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = androidx.compose.ui.graphics.Color(0xFFB22222),
                    )
                }
                if (!isEditing) {
                    Button(
                        onClick = onRecordDrink,
                        modifier = Modifier.height(48.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = androidx.compose.ui.graphics.Color(0xFF6B0000),
                            ),
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Record")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Notes
        Text(
            text = "📝 Notes",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = androidx.compose.ui.graphics.Color.White,
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (isEditing) {
            OutlinedTextField(
                value = editedBottle.note,
                onValueChange = { editedBottle = editedBottle.copy(note = it) },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                maxLines = 8,
                colors =
                    OutlinedTextFieldDefaults.colors(
                        focusedTextColor = androidx.compose.ui.graphics.Color.White,
                        unfocusedTextColor = androidx.compose.ui.graphics.Color.White,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.3f),
                        cursorColor = MaterialTheme.colorScheme.primary,
                    ),
            )
        } else {
            Text(
                text = bottle.note.ifBlank { "No notes." },
                style = MaterialTheme.typography.bodyMedium,
                color =
                    if (bottle.note.isBlank()) {
                        androidx.compose.ui.graphics.Color.White.copy(alpha = 0.6f)
                    } else {
                        androidx.compose.ui.graphics.Color.White
                    },
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        LocationSection(
            isEditing = isEditing,
            bottle = bottle,
            editedBottle = editedBottle,
            onBottleChange = { editedBottle = it },
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Created/Updated time
        val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.ENGLISH)
        Text(
            text = "Created: ${dateFormat.format(Date(bottle.createdAt))}",
            style = MaterialTheme.typography.bodySmall,
            color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.6f),
        )
        if (bottle.updatedAt != bottle.createdAt) {
            Text(
                text = "Updated: ${dateFormat.format(Date(bottle.updatedAt))}",
                style = MaterialTheme.typography.bodySmall,
                color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.6f),
            )
        }

        // Save button
        if (isEditing) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { onUpdate(editedBottle) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Save")
            }
        }
    }
}

@Composable
fun DetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = androidx.compose.ui.graphics.Color.White,
        )
    }
}

@Composable
fun RatingDisplay(
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
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
private fun LocationSection(
    isEditing: Boolean,
    bottle: BottleEntity,
    editedBottle: BottleEntity,
    onBottleChange: (BottleEntity) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "📍 Location",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = androidx.compose.ui.graphics.Color.White,
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (isEditing) {
            OutlinedTextField(
                value = editedBottle.locationName.orEmpty(),
                onValueChange = {
                    onBottleChange(editedBottle.copy(locationName = it.ifBlank { null }))
                },
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
        } else {
            val hasLocation = bottle.locationName != null
            if (hasLocation) {
                bottle.locationName?.let {
                    DetailRow(label = "Location", value = it)
                }
            } else {
                Text(
                    text = "No location recorded.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.6f),
                )
            }
        }
    }
}
