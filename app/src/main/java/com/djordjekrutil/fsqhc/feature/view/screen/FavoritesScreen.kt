package com.djordjekrutil.fsqhc.feature.view.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.djordjekrutil.fsqhc.ui.component.PlaceItem

@Composable
fun FavoritesScreen(onItemClick: (String) -> Unit) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
//        items(samplePlaces.filter { it.isFavorite }) { place ->
//            PlaceItem(place = place, onClick = { onItemClick(place.id) })
//        }
    }
}