package com.djordjekrutil.fsqhc.feature.model.mappers

import com.djordjekrutil.fsqhc.feature.model.Place
import com.djordjekrutil.fsqhc.feature.model.PlaceDetails
import com.djordjekrutil.fsqhc.feature.model.PlaceDetailsDto
import com.djordjekrutil.fsqhc.feature.model.PlaceDto
import com.djordjekrutil.fsqhc.feature.model.PlaceEntity
import com.djordjekrutil.fsqhc.feature.model.PopularTime
import com.djordjekrutil.fsqhc.feature.model.SocialMedia
import com.djordjekrutil.fsqhc.feature.model.Tip
import com.djordjekrutil.fsqhc.feature.model.WorkingHours
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private val gson = Gson()

fun PlaceDto.toPlace(): Place {
    return Place(
        fsqId = this.fsq_id,
        name = this.name,
        address = this.location?.address,
        latitude = this.location?.lat,
        longitude = this.location?.lng,
        categories = this.categories?.map { it.name } ?: emptyList(),
        distance = this.distance,
        isFavorite = false,
        rating = this.rating,
        price = this.price,
        isOpen = this.hours?.open_now,
        photoUrl = this.photos?.firstOrNull()?.getPhotoUrl(),
        details = null // Basic search doesn't have full details
    )
}

fun PlaceDetailsDto.toPlace(): Place {
    return Place(
        fsqId = this.fsq_id,
        name = this.name,
        address = this.location?.address,
        latitude = this.location?.lat,
        longitude = this.location?.lng,
        categories = this.categories?.map { it.name } ?: emptyList(),
        distance = null, // Details API doesn't return distance
        isFavorite = false,
        rating = this.rating,
        price = this.price,
        isOpen = this.hours?.open_now,
        photoUrl = this.photos?.firstOrNull()?.getPhotoUrl(),
        details = this.toPlaceDetails()
    )
}

fun PlaceDetailsDto.toPlaceDetails(): PlaceDetails {
    return PlaceDetails(
        description = this.description,
        website = this.website,
        phone = this.tel,
        email = this.email,
        hours = this.hours?.regular?.map {
            WorkingHours(
                dayOfWeek = it.day,
                openTime = it.open,
                closeTime = it.close,
                isClosed = it.open == null && it.close == null
            )
        } ?: emptyList(),
        photos = this.photos?.map { it.getPhotoUrl("400x400") } ?: emptyList(),
        tips = this.tips?.map { tip ->
            Tip(
                id = tip.id,
                text = tip.text,
                authorName = "${tip.user?.first_name.orEmpty()} ${tip.user?.last_name.orEmpty()}".trim()
                    .takeIf { it.isNotEmpty() },
                createdAt = tip.created_at,
                likes = tip.agree_count ?: 0
            )
        } ?: emptyList(),
        rating = this.rating,
        ratingCount = this.stats?.total_ratings,
        priceLevel = this.price,
        menu = this.menu,
        socialMedia = this.social_media?.let {
            SocialMedia(
                instagram = it.instagram,
                facebook = it.facebook,
                twitter = it.twitter
            )
        },
        popularTimes = this.popular_times?.flatMap { day ->
            day.popularity?.map { pop ->
                PopularTime(
                    dayOfWeek = day.day,
                    hour = pop.hour,
                    popularity = pop.popularity
                )
            } ?: emptyList()
        } ?: emptyList()
    )
}

fun Place.toEntity(searchQuery: String): PlaceEntity {
    return PlaceEntity(
        fsqId = this.fsqId,
        name = this.name,
        address = this.address,
        latitude = this.latitude,
        longitude = this.longitude,
        categories = gson.toJson(this.categories),
        distance = this.distance,
        searchQuery = searchQuery,
        isFavorite = this.isFavorite,
        rating = this.rating,
        price = this.price,
        isOpen = this.isOpen,
        photoUrl = this.photoUrl,
        detailsJson = this.details?.let { gson.toJson(it) },
        hasFullDetails = this.details != null,
        detailsLastUpdated = this.details?.lastUpdated
    )
}

fun PlaceEntity.toPlace(): Place {
    val categoriesList = try {
        gson.fromJson<List<String>>(this.categories ?: "[]", object : TypeToken<List<String>>() {}.type) ?: emptyList()
    } catch (_: Exception) {
        this.categories?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
    }

    val details = try {
        this.detailsJson?.let {
            gson.fromJson(it, PlaceDetails::class.java)
        }
    } catch (_: Exception) {
        null
    }

    return Place(
        fsqId = this.fsqId,
        name = this.name,
        address = this.address,
        latitude = this.latitude,
        longitude = this.longitude,
        categories = categoriesList,
        distance = this.distance,
        isFavorite = this.isFavorite,
        rating = this.rating,
        price = this.price,
        isOpen = this.isOpen,
        photoUrl = this.photoUrl,
        details = details
    )
}