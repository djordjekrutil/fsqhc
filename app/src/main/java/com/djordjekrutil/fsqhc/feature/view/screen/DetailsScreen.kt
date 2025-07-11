package com.djordjekrutil.fsqhc.feature.view.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DetailsScreen(itemId: String) {
//    val place = samplePlaces.find { it.id == itemId }
//
//    place?.let {
//        Column(modifier = Modifier.padding(16.dp)) {
//            Text("Details for: ${place.name}", style = MaterialTheme.typography.headlineMedium)
//            Text("Type: ${place.type}")
//            Text("Distance: ${place.distance}m")
//        }
//    } ?:
    Text("Item not found.")
}