package com.djordjekrutil.fsqhc.feature.view.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.djordjekrutil.fsqhc.R
import com.djordjekrutil.fsqhc.feature.viewmodel.FavoritesScreenState
import com.djordjekrutil.fsqhc.feature.viewmodel.FavoritesViewModel
import com.djordjekrutil.fsqhc.ui.component.CenteredContent
import com.djordjekrutil.fsqhc.ui.component.NoFavoritesState
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
                NoFavoritesState()
            }

            FavoritesScreenState.Error -> {
                CenteredContent {
                    Text(
                        text = stringResource(R.string.failed_to_load_favorites),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.titleLarge
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
                        item {
                            Text(
                                text = stringResource(R.string.your_favorites),
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
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
                NoFavoritesState()
            }

            FavoritesScreenState.Initial -> {
                CenteredContent { CircularProgressIndicator() }
            }

        }

    }
}