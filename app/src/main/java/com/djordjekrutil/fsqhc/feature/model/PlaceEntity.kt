package com.djordjekrutil.fsqhc.feature.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "places")
data class PlaceEntity(
    @PrimaryKey val fsqId: String,
    val name: String,
    val address: String?,
    val latitude: Double?,
    val longitude: Double?,
    val categories: String?,
    val distance: Int?,
    val searchQuery: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)