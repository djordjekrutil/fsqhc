package com.djordjekrutil.fsqhc.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.djordjekrutil.fsqhc.feature.navigation.MainNavigation
import com.djordjekrutil.fsqhc.feature.navigation.Screens

@Composable
fun MainScaffold(navController: NavHostController) {
    val bottomScreens = listOf(Screens.Search, Screens.Favorites)
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    Scaffold(
        bottomBar = {
            BottomBar(
                navController = navController,
                screens = bottomScreens,
                currentDestination = currentDestination
            )
        }
    ) { innerPadding ->
        MainNavigation(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}