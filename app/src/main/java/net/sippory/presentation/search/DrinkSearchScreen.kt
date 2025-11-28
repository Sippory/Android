package net.sippory.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HighlightOff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import net.sippory.data.entity.DrinkEntity

private val DeepBlack = Color(0xFF0D0D0D)
private val WineRed = Color(0xFF8B1538)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrinkSearchScreen(
    navController: NavController,
    viewModel: DrinkSearchViewModel,
) {
    var searchQuery by remember { mutableStateOf("") }
    val searchResults by viewModel.searchResults.collectAsState()
    val recentlySearchedDrinks by viewModel.recentlySearchedDrinks.collectAsState()

    LaunchedEffect(searchQuery) {
        viewModel.searchDrinks(searchQuery)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Search Drinks") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepBlack,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                )
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(color = DeepBlack)
            ,
        ) {
            Spacer(modifier = Modifier.padding(top = 24.dp))
            Column(
                modifier = Modifier.padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search Keyword", color = Color.Gray) },
                    textStyle = TextStyle(color = Color.White),
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                        OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WineRed,
                            unfocusedBorderColor = Color.Gray,
                            cursorColor = Color.White
                        ),
                    shape = RoundedCornerShape(size = 12.dp),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon",
                            tint = Color.Gray
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.HighlightOff,
                            contentDescription = "Clear Search",
                            tint = Color.Gray,
                            modifier = Modifier.clickable { searchQuery = "" }
                        )
                    },
                    keyboardOptions =
                        KeyboardOptions(
                            imeAction = ImeAction.Done,
                        ),
                )
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    when (searchQuery.isNotBlank()) {
                        true -> {
                            TextAndCountContainer(
                                text = "Search Results",
                                count = searchResults.size,
                            )
                        }

                        else -> {
                            TextAndCountContainer(
                                text = "Recently Searched",
                                count = recentlySearchedDrinks.size,
                            )
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (searchQuery.isNotBlank() && searchResults.isNotEmpty()) {
                        items(searchResults) { drink ->
                            Row(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.onDrinkSelected(drink)
                                            navController.navigate("search/${drink.name}")
                                        },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text(
                                        drink.name,
                                        fontSize = 14.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = Color.White
                                    )
                                    Text(drink.category, fontSize = 12.sp, color = Color.White)
                                }
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Go to Details",
                                    tint = Color.White,
                                )
                            }
                        }
                    } else if (searchQuery.isNotBlank() && searchResults.isEmpty()) {
                        item {
                            Text(
                                text = "No results found.",
                                fontSize = 14.sp,
                                color = Color.Gray,
                            )
                        }
                    } else {
                        items(recentlySearchedDrinks) { drink ->
                            Row(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.onDrinkSelected(
                                                DrinkEntity(
                                                    name = drink.name,
                                                    category = drink.category,
                                                    image_url = drink.image_url,
                                                ),
                                            )
                                            navController.navigate("search/${drink.name}")
                                        },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text(
                                        drink.name,
                                        fontSize = 14.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = Color.White
                                    )
                                    Text(drink.category, fontSize = 12.sp, color = Color.White)
                                }
                                IconButton(
                                    onClick = {
                                        viewModel.onClickRemoveRecentlySearchedDrink(drink)
                                    },
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove from Recently Searched",
                                        tint = Color.White,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TextAndCountContainer(
    text: String,
    count: Int,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text,
            fontSize = 14.sp,
            color = Color.White
        )
        Text(
            count.toString(),
            fontSize = 14.sp,
            color = WineRed,
        )
    }
}
