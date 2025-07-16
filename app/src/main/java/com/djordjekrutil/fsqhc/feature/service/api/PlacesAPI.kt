package com.djordjekrutil.fsqhc.feature.service.api

import com.djordjekrutil.fsqhc.feature.model.FoursquareResponse
import com.djordjekrutil.fsqhc.feature.model.PlaceDetailsDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PlacesAPI {

    companion object {
        private const val PLACES = "v3/places/search"
        private const val PLACE = "v3/places{fsqId}"
    }

    @GET(PLACES)
    suspend fun searchPlaces(
        @Query("query") query: String,
        @Query("ll") ll: String,
        @Query("cursor") nextCursor: String? = null,
        @Query("fields") fields: String = "fsq_id,name,location,categories,distance,rating,price,hours,photos"
    ): Response<FoursquareResponse>

    @GET(PLACE)
    suspend fun getPlace(
        @Path("fsqId") fsqId: String,
        @Query("fields") fields: String = "fsq_id,name,location,categories,description,website,tel,email,hours,photos,tips,rating,stats,price,menu"

    ): Response<PlaceDetailsDto>

}