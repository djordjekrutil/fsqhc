package com.djordjekrutil.fsqhc.feature.view.screen

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import com.djordjekrutil.fsqhc.R
import com.djordjekrutil.fsqhc.feature.viewmodel.PlacesScreenState
import com.djordjekrutil.fsqhc.ui.component.LoadingPlaceItem
import com.djordjekrutil.fsqhc.ui.component.NoSearchResultsState
import com.djordjekrutil.fsqhc.ui.component.PlaceItem
import com.djordjekrutil.fsqhc.ui.component.SearchHintState

@Composable
fun SearchScreen(
    state: PlacesScreenState,
    onItemClick: (String) -> Unit,
    onSearch: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Styled search bar in Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            border = BorderStroke(0.5.dp, Color.LightGray),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF8F8F8)
            )
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    onSearch(it)
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                placeholder = {
                    Text(stringResource(R.string.search_venues))
                },
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedLeadingIconColor = Color.Gray,
                    unfocusedLeadingIconColor = Color.Gray,
                    focusedPlaceholderColor = Color.Gray,
                    unfocusedPlaceholderColor = Color.Gray
                )
            )
        }

        Crossfade(targetState = state, label = "search-screen-crossfade") { screenState ->
            when (screenState) {
                is PlacesScreenState.StartSearch -> {
                    SearchHintState()
                }

                is PlacesScreenState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(stringResource(R.string.error, screenState.message))
                    }
                }

                is PlacesScreenState.Loading -> {
                    LazyColumn {
                        items(5) {
                            LoadingPlaceItem()
                        }
                    }
                }

                is PlacesScreenState.Content -> {
                    val places by screenState.places.collectAsState(initial = emptyList())
                    if (places.isEmpty()) {
                        LazyColumn {
                            item { NoSearchResultsState() }
                        }
                    } else {
                        LazyColumn {
                            items(places, key = { it.fsqId }) { place ->
                                PlaceItem(
                                    place = place,
                                    onClick = { onItemClick(place.fsqId) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
