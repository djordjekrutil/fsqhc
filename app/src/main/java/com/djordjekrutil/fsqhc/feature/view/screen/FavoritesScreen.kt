package com.djordjekrutil.fsqhc.feature.view.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.djordjekrutil.fsqhc.R
import com.djordjekrutil.fsqhc.feature.viewmodel.FavoritesScreenState
import com.djordjekrutil.fsqhc.feature.viewmodel.FavoritesViewModel
import com.djordjekrutil.fsqhc.ui.component.CenteredContent
import com.djordjekrutil.fsqhc.ui.component.NoSearchResultsState
import com.djordjekrutil.fsqhc.ui.component.PlaceItem

@Composable
fun FavoritesScreen(
    onItemClick: (String) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when (state) {
            FavoritesScreenState.Loading,
            FavoritesScreenState.Initial -> {
                CenteredContent { CircularProgressIndicator() }
            }

            FavoritesScreenState.Error -> {
                CenteredContent {
                    Text(
                        text = stringResource(R.string.failed_to_load_favorites),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            is FavoritesScreenState.Success -> {
                val places = (state as FavoritesScreenState.Success).places
                if (places.isEmpty()) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item { NoSearchResultsState() }
                    }
                } else {
                    LazyColumn {
                        items(places, key = { place -> place.fsqId }) { place ->
                            PlaceItem(
                                place = place,
                                onClick = { onItemClick(place.fsqId) },
                                onFavoritesClick = viewModel::toggleFavorite
                            )
                        }
                    }
                }
            }

            FavoritesScreenState.Empty -> {
                CenteredContent {
                    Text(
                        text = stringResource(R.string.add_places_to_your_favorites_to_see_them_here),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            FavoritesScreenState.Initial -> {
                CenteredContent { CircularProgressIndicator() }
            }

        }

    }
}