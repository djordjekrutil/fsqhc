package com.djordjekrutil.fsqhc.feature.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.djordjekrutil.fsqhc.feature.view.screen.DetailsScreen
import com.djordjekrutil.fsqhc.feature.view.screen.FavoritesScreen
import com.djordjekrutil.fsqhc.feature.view.screen.SearchScreen

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
            SearchScreen(
                onItemClick = { id ->
                    navController.navigate("details/$id") {
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(Screens.Favorites.route) {
            FavoritesScreen(
                onItemClick = { id ->
                    navController.navigate("details/$id") {
                        launchSingleTop = true
                    }
                },
            )
        }
        composable("details/{id}") { backStackEntry ->
            DetailsScreen()
        }
    }
}