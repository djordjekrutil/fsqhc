package com.djordjekrutil.fsqhc.feature.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.djordjekrutil.fsqhc.ui.component.MainScaffold
import com.djordjekrutil.fsqhc.ui.theme.FSQHCTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FSQHCTheme {
                val navController = rememberNavController()
                MainScaffold(navController = navController)
            }
        }
    }
}