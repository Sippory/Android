package net.sippory.presentation.searchDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import net.sippory.data.repository.BottleRepository
import net.sippory.presentation.add.AddBottleSheet
import net.sippory.presentation.search.DrinkSearchViewModel

private val DeepBlack = Color(0xFF0D0D0D)
private val WineRed = Color(0xFF8B1538)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchDetailScreen(
    navController: NavController,
    viewModel: DrinkSearchViewModel,
    bottleRepository: BottleRepository,
    imageManager: net.sippory.utils.ImageManager,
) {
    val selectedDrink by viewModel.selectedDrink.collectAsState()

    var showAddBottleSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = selectedDrink?.name ?: "",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Back",
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = DeepBlack,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                    ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(DeepBlack),
        ) {
            Spacer(modifier = Modifier.padding(48.dp))
            Column(
                modifier =
                    Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                selectedDrink?.let { drink ->
                    AsyncImage(
                        model = drink.image_url,
                        contentDescription = drink.name,
                        modifier =
                            Modifier
                                .size(300.dp)
                                .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop,
                    )
                }
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                ) {
                    Text("Drink Detail", color = WineRed, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.padding(8.dp))
                    HorizontalDivider()
                    Column(
                        modifier = Modifier.padding(top = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                            Text(text = "Drink Name", fontSize = 14.sp, color = Color.White)
                            Text(text = selectedDrink?.name ?: "", fontSize = 14.sp, color = Color.White)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                            Text("Drink Category", fontSize = 14.sp, color = Color.White)
                            Text(selectedDrink?.category ?: "", fontSize = 14.sp, color = Color.White)
                        }
                    }
                }
            }
            Button(
                onClick = {
                    showAddBottleSheet = true
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = WineRed,
                    ),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(text = "Add Tasting Note", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            if (showAddBottleSheet) {
                AddBottleSheet(
                    onDismiss = { showAddBottleSheet = false },
                    imageManager = imageManager,
                    repository = bottleRepository,
                    drinkName = selectedDrink?.name ?: "",
                    drinkType = selectedDrink?.category ?: "",
                    drinkPhotoUri = selectedDrink?.image_url,
                )
            }
        }
    }
}
