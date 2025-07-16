package com.djordjekrutil.fsqhc.feature.view.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.djordjekrutil.fsqhc.R
import com.djordjekrutil.fsqhc.feature.viewmodel.PlaceDetailsScreenState
import com.djordjekrutil.fsqhc.feature.viewmodel.PlaceDetailsViewModel
import com.djordjekrutil.fsqhc.ui.component.CenteredContent

@Composable
fun DetailsScreen(viewModel: PlaceDetailsViewModel) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        when (state) {
            PlaceDetailsScreenState.Loading -> {
                CenteredContent {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = stringResource(R.string.loading_place_details))
                    }
                }
            }

            PlaceDetailsScreenState.FailedToLoadDetails -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.failed_to_load_place_details),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            is PlaceDetailsScreenState.Content -> {
                val place = (state as PlaceDetailsScreenState.Content).place
                CenteredContent {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = place.name, style = MaterialTheme.typography.headlineMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = place.address.orEmpty())
                        // Add more details about the place as needed
                    }
                }
            }
        }
    }

}