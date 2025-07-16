package com.djordjekrutil.fsqhc.feature.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.djordjekrutil.fsqhc.feature.view.screen.DetailsScreen
import com.djordjekrutil.fsqhc.feature.view.screen.FavoritesScreen
import com.djordjekrutil.fsqhc.feature.view.screen.SearchScreen
import com.djordjekrutil.fsqhc.feature.viewmodel.FavoritesViewModel
import com.djordjekrutil.fsqhc.feature.viewmodel.PlaceDetailsViewModel
import com.djordjekrutil.fsqhc.feature.viewmodel.PlacesViewModel

@Composable
fun MainNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    val animationDuration = 300
    NavHost(
        navController = navController,
        startDestination = Screens.Search.route,
        modifier = modifier,
        enterTransition = {
            fadeIn(animationSpec = tween(animationDuration))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(animationDuration))
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(animationDuration))
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(animationDuration))
        }
    ) {
        composable(Screens.Search.route) {
            val viewModel: PlacesViewModel = hiltViewModel()

            SearchScreen(
                viewModel = viewModel,
                onItemClick = { id ->
                    navController.navigate("details/$id") {
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(Screens.Favorites.route) {
            val viewModel: FavoritesViewModel = hiltViewModel()

            FavoritesScreen(
                viewModel = viewModel,
                onItemClick = { id ->
                    navController.navigate("details/$id") {
                        launchSingleTop = true
                    }
                },
            )
        }
        composable("details/{id}") { backStackEntry ->
            val viewModel: PlaceDetailsViewModel = hiltViewModel()

            DetailsScreen(viewModel)
        }
    }
}