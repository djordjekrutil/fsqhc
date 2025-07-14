package com.djordjekrutil.fsqhc.feature.service

import com.djordjekrutil.fsqhc.feature.model.FoursquareResponse
import com.djordjekrutil.fsqhc.feature.model.PlaceDto
import com.djordjekrutil.fsqhc.feature.service.api.PlacesAPI
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlacesService
@Inject constructor(retrofit: Retrofit) : PlacesAPI {

    private val placesApi by lazy { retrofit.create(PlacesAPI::class.java) }

    override suspend fun searchPlaces(query: String, ll : String): Response<FoursquareResponse> = placesApi.searchPlaces(query, ll)

    override suspend fun getPlace(fsqId: String): Response<PlaceDto> = placesApi.getPlace(fsqId)
}
