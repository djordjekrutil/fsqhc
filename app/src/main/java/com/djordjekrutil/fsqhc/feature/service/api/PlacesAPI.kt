package com.djordjekrutil.fsqhc.feature.service.api

import com.djordjekrutil.fsqhc.feature.model.FoursquareResponse
import com.djordjekrutil.fsqhc.feature.model.PlaceDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PlacesAPI {

    companion object{
        private const val PLACES = "search"
        private const val PLACE = "v3/places/{fsqId}"
    }

    @GET(PLACES)
    suspend fun searchPlaces(@Query("query") query: String, @Query("ll") ll: String): Response<FoursquareResponse>

    @GET(PLACE)
    suspend fun getPlace(@Path("fsqId") fsqId: String): Response<PlaceDto>

}