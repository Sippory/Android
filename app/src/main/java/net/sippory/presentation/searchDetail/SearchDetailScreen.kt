package net.sippory.presentation.searchDetail

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import net.sippory.presentation.search.DrinkSearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchDetailScreen(
    navController: NavController,
    viewModel: DrinkSearchViewModel,
) {
    val selectedDrink by viewModel.selectedDrink.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(
                    text = selectedDrink?.name ?: "",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }, navigationIcon = {
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
            })
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
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
                    Text("Drink Detail")
                    Spacer(modifier = Modifier.padding(8.dp))
                    HorizontalDivider()
                    Column(
                        modifier = Modifier.padding(top = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                            Text(text = "Drink Name", fontSize = 12.sp)
                            Text(text = selectedDrink?.name ?: "", fontSize = 12.sp, color = Color.Gray)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                            Text("Drink Category", fontSize = 12.sp)
                            Text(selectedDrink?.category ?: "", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
            Button(
                onClick = {},
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = Color.Blue,
                    ),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(text = "Add Tasting Note", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
