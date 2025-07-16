package com.djordjekrutil.fsqhc.ui.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.djordjekrutil.fsqhc.R

@Composable
fun NoFavoritesState() {
    CenteredContent {
        Text(
            text = stringResource(R.string.add_places_to_your_favorites_to_see_them_here),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.titleLarge,
        )
    }
}