package com.djordjekrutil.fsqhc.feature.view.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
        TextField(
            value = query,
            onValueChange = {
                query = it
                onSearch(it)
            },
            placeholder = { Text("Search...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        when (state) {
            is PlacesScreenState.StartSearch -> {
                SearchHintState()
            }

            is PlacesScreenState.Loading -> {
                LazyColumn {
                    items(5) { LoadingPlaceItem() }
                }
            }

            is PlacesScreenState.Content -> {
                val places by state.places.collectAsState(initial = emptyList())

                if (places.isEmpty()) {
                    NoSearchResultsState()
                } else {
                    LazyColumn {
                        items(places.size) { index ->
                            PlaceItem(
                                place = places[index],
                                onClick = { onItemClick(places[index].fsqId) }
                            )
                        }
                    }
                }
            }

            is PlacesScreenState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error: ${state.message}")
                }
            }
        }
    }
}
