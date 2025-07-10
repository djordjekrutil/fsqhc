package com.djordjekrutil.fsqhc.feature.service

import com.djordjekrutil.fsqhc.feature.model.FoursquareResponse
import com.djordjekrutil.fsqhc.feature.model.PlaceDto
import com.djordjekrutil.fsqhc.feature.service.api.PlacesAPI
import retrofit2.Call
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlacesService
@Inject constructor(retrofit: Retrofit) : PlacesAPI {

    private val placesApi by lazy { retrofit.create(PlacesAPI::class.java) }

    override fun searchPlaces(query: String): Call<FoursquareResponse> = placesApi.searchPlaces(query)

    override fun getPlace(fsqId: String): Call<PlaceDto> = placesApi.getPlace(fsqId)
}
