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
    val categories: String?, // JSON string
    val distance: Int?,
    val searchQuery: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val rating: Double? = null,
    val price: Int? = null,
    val isOpen: Boolean? = null,
    val photoUrl: String? = null,
    val detailsJson: String? = null, // Serialized PlaceDetails
    val hasFullDetails: Boolean = false,
    val detailsLastUpdated: Long? = null
)