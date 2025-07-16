package com.djordjekrutil.fsqhc.feature.view.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.djordjekrutil.fsqhc.R
import com.djordjekrutil.fsqhc.feature.model.Place
import com.djordjekrutil.fsqhc.feature.model.util.*
import com.djordjekrutil.fsqhc.feature.viewmodel.PlaceDetailsScreenState
import com.djordjekrutil.fsqhc.feature.viewmodel.PlaceDetailsViewModel
import com.djordjekrutil.fsqhc.ui.component.*
import com.djordjekrutil.fsqhc.ui.util.getOptimizedHeroUrl

@Composable
fun DetailsScreen(viewModel: PlaceDetailsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

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
            CenteredContent {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.failed_to_load_place_details),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        is PlaceDetailsScreenState.Content -> {
            val place = (state as PlaceDetailsScreenState.Content).place
            PlaceDetailsContent(place = place)
        }
    }
}

@Composable
private fun PlaceDetailsContent(place: Place) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            PlaceHeaderSection(place)
        }

        item {
            QuickInfoSection(place)
        }

        if (place.details?.description != null) {
            item {
                DescriptionSection(place.details.description)
            }
        }

        if (place.details?.photos?.isNotEmpty() == true) {
            item {
                PhotosSection(place.details.photos)
            }
        }
    }
}

@Composable
private fun PlaceHeaderSection(place: Place) {
    Column {
        val heroImageUrl = place.details?.photos?.firstOrNull() ?: place.photoUrl
        if (heroImageUrl != null) {
            PlaceImageHero(
                imageUrl = place.getOptimizedHeroUrl(),
                contentDescription = "Photo of ${place.name}"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = place.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (place.categories.isNotEmpty()) {
            Text(
                text = place.categories.joinToString(stringResource(R.string.point)),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
        }

        if (place.address != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = place.address,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun QuickInfoSection(place: Place) {
    Row(
        modifier = Modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Rating
        if (place.rating != null) {
            QuickInfoCard(
                icon = Icons.Outlined.Star,
                title = "Rating",
                value = place.getRatingDisplay(),
                subtitle = place.details?.ratingCount?.let { "($it reviews)" },
                modifier = Modifier.weight(1f)
            )
        }

        // Status
        if (place.isOpen != null) {
            QuickInfoCard(
                icon = Icons.Outlined.LocationOn,
                title = stringResource(R.string.status),
                value = place.getOpenStatusDisplay(),
                valueColor = if (place.isOpen) Color.Green else Color.Red,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickInfoCard(
    icon: ImageVector,
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    valueColor: Color = Color.Unspecified,
) {
    Card(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun DescriptionSection(description: String) {
    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.about),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun PhotosSection(photos: List<String>) {
    Column {
        Text(
            text = "Photos",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(photos) { photoUrl ->
                PlaceImageGallery(
                    imageUrl = photoUrl,
                    contentDescription = stringResource(R.string.place_photo)
                )
            }
        }
    }
}