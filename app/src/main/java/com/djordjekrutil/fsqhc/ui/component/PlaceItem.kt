package com.djordjekrutil.fsqhc.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.djordjekrutil.fsqhc.R
import com.djordjekrutil.fsqhc.feature.model.Place
import com.djordjekrutil.fsqhc.feature.model.util.*
import com.djordjekrutil.fsqhc.ui.util.getOptimizedThumbnailUrl

@Composable
fun PlaceItem(
    place: Place,
    onClick: () -> Unit,
    onFavoritesClick: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(0.5.dp, Color.LightGray),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            PlaceImageThumbnail(
                imageUrl = place.getOptimizedThumbnailUrl(),
                contentDescription = "Photo of ${place.name}",
                size = 80
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = place.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = place.categories.firstOrNull().orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                if (place.rating != null || place.price != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (place.rating != null) {
                            Icon(
                                imageVector = Icons.Outlined.Star,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color(0xFFFFB300)
                            )
                            Text(
                                text = " ${place.getRatingDisplay()}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        if (place.price != null) {
                            if (place.rating != null) {
                                Text(
                                    text = stringResource(R.string.point),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Text(
                                text = place.getPriceDisplay(),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (place.distance != null) {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.Gray
                        )
                        Text(
                            text = "${place.distance}m",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }

                    if (place.isOpen != null) {
                        if (place.distance != null) {
                            Text(
                                text = stringResource(R.string.point),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                        Text(
                            text = place.getOpenStatusDisplay(),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (place.isOpen == true) Color(0xFF4CAF50) else Color(0xFFF44336),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = if (place.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = stringResource(R.string.favorite),
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onFavoritesClick(place.fsqId, !place.isFavorite) },
                    tint = if (place.isFavorite) Color(0xFFE91E63) else Color.Gray
                )

                Spacer(modifier = Modifier.height(48.dp))

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
        }
    }
}