package com.djordjekrutil.fsqhc.feature.model

import kotlinx.coroutines.flow.Flow

data class PagedPlacesResult(
    val places: Flow<List<Place>>,
    val nextCursor: String?
)