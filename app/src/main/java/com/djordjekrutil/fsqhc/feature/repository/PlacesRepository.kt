package com.djordjekrutil.fsqhc.feature.repository

import com.djordjekrutil.fsqhc.core.exception.Failure
import com.djordjekrutil.fsqhc.core.functional.Either
import com.djordjekrutil.fsqhc.core.interactor.UseCase
import com.djordjekrutil.fsqhc.feature.datasource.PlacesLocalDataSource
import com.djordjekrutil.fsqhc.feature.datasource.PlacesNetworkDataSource
import com.djordjekrutil.fsqhc.feature.model.PagedPlacesResult
import com.djordjekrutil.fsqhc.feature.model.Place
import com.djordjekrutil.fsqhc.feature.model.PlaceDetailsDto
import com.djordjekrutil.fsqhc.feature.model.PlaceEntity
import com.djordjekrutil.fsqhc.feature.model.mappers.*
import com.djordjekrutil.fsqhc.feature.model.util.hasMinimalDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

interface PlacesRepository {
    suspend fun searchPlaces(
        query: String,
        lat: Double,
        long: Double,
        nextCursor: String? = null
    ): Either<Failure, PagedPlacesResult>

    suspend fun getPlaceDetails(fsqId: String): Either<Failure, Place>

    suspend fun setFavorite(fsqId: String, isFavorite: Boolean): Either<Failure, UseCase.None>

    fun getFavoritePlaces(): Flow<List<Place>>
}

class PlacesRepositoryImpl @Inject constructor(
    private val networkDataSource: PlacesNetworkDataSource,
    private val localDataSource: PlacesLocalDataSource
) : PlacesRepository {

    override suspend fun searchPlaces(
        query: String,
        lat: Double,
        long: Double,
        nextCursor: String?
    ): Either<Failure, PagedPlacesResult> {
        Timber.d("Searching places: query='$query', lat=$lat, long=$long")

        return when (val networkResult = networkDataSource.searchPlaces(query, lat, long, nextCursor)) {
            is Either.Right -> handleSearchNetworkSuccess(networkResult.b, query)
            is Either.Left -> handleSearchNetworkFailure(query, networkResult.a)
        }
    }

    override suspend fun getPlaceDetails(fsqId: String): Either<Failure, Place> {
        Timber.d("Getting place details: fsqId='$fsqId'")

        val cachedPlace = try {
            localDataSource.getPlace(fsqId)?.toPlace()
        } catch (_: Exception) {
            null
        }

        return when (val networkResult = networkDataSource.getPlaceDetails(fsqId)) {
            is Either.Right -> handleGetPlaceDetailsNetworkSuccess(networkResult.b)
            is Either.Left -> handleGetPlaceDetailsNetworkFailure(fsqId, networkResult.a, cachedPlace)
        }
    }

    override suspend fun setFavorite(fsqId: String, isFavorite: Boolean): Either<Failure, UseCase.None> {
        return localDataSource.setFavorite(fsqId, isFavorite)
            .also { result ->
                if (result is Either.Left) {
                    Timber.e("Failed to set favorite: ${result.a}")
                }
            }
    }

    override fun getFavoritePlaces(): Flow<List<Place>> {
        return localDataSource.getFavoritePlaces()
            .catch { exception ->
                Timber.e(exception, "Error loading favorite places")
                emit(emptyList())
            }
    }

    private suspend fun handleSearchNetworkSuccess(
        networkResult: com.djordjekrutil.fsqhc.feature.datasource.NetworkSearchResult,
        query: String
    ): Either<Failure, PagedPlacesResult> {
        return try {
            val placesDto = networkResult.places.first()
            val places = placesDto.map { dto ->
                val existingPlace = try {
                    localDataSource.getPlace(dto.fsq_id)?.toPlace()
                } catch (_: Exception) {
                    null
                }

                dto.toPlace().copy(
                    isFavorite = existingPlace?.isFavorite ?: false,
                    details = existingPlace?.details
                )
            }

            val placeEntities = places.map { it.toEntity(query) }
            cacheResultsGracefully(placeEntities)

            Either.Right(
                PagedPlacesResult(
                    places = flowOf(places),
                    nextCursor = networkResult.nextCursor
                )
            )
        } catch (e: Exception) {
            Timber.e(e, "Error processing network search result")
            Either.Left(Failure.DataProcessingError)
        }
    }

    private suspend fun handleSearchNetworkFailure(
        query: String,
        networkFailure: Failure
    ): Either<Failure, PagedPlacesResult> {
        return try {
            val placesFlow = localDataSource.searchPlaces(query).map { entities ->
                entities.map { it.toPlace() }
            }

            val cachedPlaces = placesFlow.first()

            if (cachedPlaces.isNotEmpty()) {
                Either.Right(PagedPlacesResult(flowOf(cachedPlaces), null))
            } else {
                Either.Left(networkFailure)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error loading places from cache")
            Either.Left(networkFailure)
        }
    }

    private suspend fun handleGetPlaceDetailsNetworkSuccess(
        placeDetailsDto: PlaceDetailsDto
    ): Either<Failure, Place> {
        return try {
            val place = placeDetailsDto.toPlace()

            val existingPlace = try {
                localDataSource.getPlace(place.fsqId)?.toPlace()
            } catch (_: Exception) {
                null
            }

            val updatedPlace = place.copy(isFavorite = existingPlace?.isFavorite ?: false)
            val placeEntity = updatedPlace.toEntity("")

            cacheResultsGracefully(listOf(placeEntity))

            Either.Right(updatedPlace)
        } catch (e: Exception) {
            Timber.e(e, "Error processing place details network result")
            Either.Left(Failure.DataProcessingError)
        }
    }

    private fun handleGetPlaceDetailsNetworkFailure(
        fsqId: String,
        networkFailure: Failure,
        cachedPlace: Place?
    ): Either<Failure, Place> {
        return when {
            cachedPlace != null -> {
                if (cachedPlace.hasMinimalDetails()) {
                    Timber.d("Returning cached place with minimal details: ${cachedPlace.name}")
                    Either.Right(cachedPlace)
                } else {
                    Either.Left(Failure.NotFound)
                }
            }
            else -> {
                Timber.w("No cached data available for place: $fsqId")
                Either.Left(networkFailure)
            }
        }
    }

    private suspend fun cacheResultsGracefully(placeEntities: List<PlaceEntity>) {
        localDataSource.insertPlaces(placeEntities)
            .fold(
                { failure -> Timber.w("Failed to cache ${placeEntities.size} places: $failure") },
                { }
            )
    }
}