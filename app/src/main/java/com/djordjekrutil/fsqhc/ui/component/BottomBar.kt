package com.djordjekrutil.fsqhc.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
            .fillMaxWidth(),
        shadowElevation = 12.dp,
    ) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.background,
            tonalElevation = 0.dp
        ) {
            screens.forEach { screen ->
                NavigationBarItem(
                    selected = currentDestination?.route == screen.route,
                    onClick = {
                        if (currentDestination?.route != screen.route) {
                            navController.navigate(screen.route) {
                                popUpTo(Screens.Search.route) { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    },
                    icon = { Icon(screen.icon, contentDescription = null) },
                    label = { Text(screen.label) }
                )
            }
        }
    }
}
