package com.djordjekrutil.fsqhc.feature.model.mappers

import com.djordjekrutil.fsqhc.feature.model.Place
import com.djordjekrutil.fsqhc.feature.model.PlaceDto
import com.djordjekrutil.fsqhc.feature.model.PlaceEntity
import com.google.gson.Gson

fun PlaceDto.toEntity(searchQuery: String): PlaceEntity {
    return PlaceEntity(
        fsqId = fsq_id,
        name = name,
        address = location?.address,
        latitude = location?.lat,
        longitude = location?.lng,
        categories = categories?.map { it.name }?.let { Gson().toJson(it) },
        distance = distance,
        searchQuery = searchQuery
    )
}

fun PlaceEntity.toDomain(): Place {
    val categoryList = try {
        Gson().fromJson(categories, Array<String>::class.java)?.toList() ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    return Place(
        fsqId = fsqId,
        name = name,
        address = address,
        latitude = latitude,
        longitude = longitude,
        categories = categoryList,
        distance = distance,
        isFavorite = isFavorite
    )
}