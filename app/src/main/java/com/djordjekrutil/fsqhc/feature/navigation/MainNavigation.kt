package com.djordjekrutil.fsqhc.feature.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.djordjekrutil.fsqhc.feature.view.screen.DetailsScreen
import com.djordjekrutil.fsqhc.feature.view.screen.FavoritesScreen
import com.djordjekrutil.fsqhc.feature.view.screen.SearchScreen
import com.djordjekrutil.fsqhc.feature.viewmodel.PlacesViewModel

@Composable
fun MainNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Screens.Search.route,
        modifier = modifier,
    ) {
        composable(Screens.Search.route) {
            val viewModel: PlacesViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()

            SearchScreen(
                state = state,
                onItemClick = { id -> navController.navigate("details/$id") },
                onSearch = { query -> viewModel.searchPlaces(query) }
            )
        }
        composable(Screens.Favorites.route) {
            FavoritesScreen { id -> navController.navigate("details/$id") }
        }
        composable("details/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            DetailsScreen(itemId = id)
        }
    }
}