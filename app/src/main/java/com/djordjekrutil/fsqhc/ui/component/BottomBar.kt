package com.djordjekrutil.fsqhc.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.djordjekrutil.fsqhc.feature.navigation.Screens

@Composable
fun BottomBar(
    navController: NavController,
    screens: List<Screens>,
    currentDestination: NavDestination?
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 0.5.dp, color = Color.LightGray),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        NavigationBar(
            containerColor = Color.White,
            tonalElevation = 0.dp
        ) {
            screens.forEach { screen ->
                NavigationBarItem(
                    selected = currentDestination?.route == screen.route,
                    onClick = {
                        if (currentDestination?.route != screen.route) {
                            navController.navigate(screen.route)
                        }
                    },
                    icon = { Icon(screen.icon, contentDescription = null) },
                    label = { Text(screen.label) }
                )
            }
        }
    }
}
