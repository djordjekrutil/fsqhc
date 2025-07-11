package com.djordjekrutil.fsqhc.feature.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screens(val route: String, val label: String, val icon: ImageVector) {
    data object Search : Screens("search", "Search", Icons.Default.Search)
    data object Favorites : Screens("favorites", "Favorites", Icons.Default.Favorite)
}