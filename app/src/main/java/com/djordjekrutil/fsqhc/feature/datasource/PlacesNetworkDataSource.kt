package com.djordjekrutil.fsqhc.feature.datasource

import com.djordjekrutil.fsqhc.core.exception.Failure
import com.djordjekrutil.fsqhc.core.functional.Either
import com.djordjekrutil.fsqhc.core.platform.NetworkHandler
import com.djordjekrutil.fsqhc.core.util.extractCursorFromLinkHeader
import com.djordjekrutil.fsqhc.feature.model.PlaceDto
import com.djordjekrutil.fsqhc.feature.model.PlaceDetailsDto
import com.djordjekrutil.fsqhc.feature.service.PlacesService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

interface PlacesNetworkDataSource {
    suspend fun searchPlaces(
        query: String,
        lat: Double,
        long: Double,
        nextCursor: String? = null
    ): Either<Failure, NetworkSearchResult>

    suspend fun getPlaceDetails(fsqId: String): Either<Failure, PlaceDetailsDto>
}

class PlacesNetworkDataSourceImpl @Inject constructor(
    private val networkHandler: NetworkHandler,
    private val placesService: PlacesService
) : PlacesNetworkDataSource {

    override suspend fun searchPlaces(
        query: String,
        lat: Double,
        long: Double,
        nextCursor: String?
    ): Either<Failure, NetworkSearchResult> {
        return if (networkHandler.isConnected) {
            try {
                val response = placesService.searchPlaces(query, "$lat,$long", nextCursor)
                response.body()?.let { body ->
                    val linkHeader = response.headers()["Link"]
                    val nextCursor = extractCursorFromLinkHeader(linkHeader.orEmpty())

                    Either.Right(
                        NetworkSearchResult(
                            places = flowOf(body.results),
                            nextCursor = nextCursor
                        )
                    )
                } ?: Either.Left(Failure.ServerError)
            } catch (_: Exception) {
                Either.Left(Failure.ServerError)
            }
        } else {
            Either.Left(Failure.NetworkConnection)
        }
    }

    override suspend fun getPlaceDetails(fsqId: String): Either<Failure, PlaceDetailsDto> {
        return if (networkHandler.isConnected) {
            try {
                val response = placesService.getPlace(fsqId)
                response.body()?.let { body ->
                    Either.Right(body)
                } ?: Either.Left(Failure.ServerError)
            } catch (_: Exception) {
                Either.Left(Failure.ServerError)
            }
        } else {
            Either.Left(Failure.NetworkConnection)
        }
    }
}

data class NetworkSearchResult(
    val places: Flow<List<PlaceDto>>,
    val nextCursor: String?
)