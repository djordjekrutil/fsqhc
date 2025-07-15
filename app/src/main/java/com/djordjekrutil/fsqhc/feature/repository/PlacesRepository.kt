package com.djordjekrutil.fsqhc.feature.repository

import com.djordjekrutil.fsqhc.core.exception.Failure
import com.djordjekrutil.fsqhc.core.functional.Either
import com.djordjekrutil.fsqhc.core.interactor.UseCase
import com.djordjekrutil.fsqhc.core.platform.NetworkHandler
import com.djordjekrutil.fsqhc.core.util.extractCursorFromLinkHeader
import com.djordjekrutil.fsqhc.feature.db.AppDatabase
import com.djordjekrutil.fsqhc.feature.model.Place
import com.djordjekrutil.fsqhc.feature.model.PlaceDto
import com.djordjekrutil.fsqhc.feature.model.PlaceEntity
import com.djordjekrutil.fsqhc.feature.service.PlacesService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface PlacesRepository {

    suspend fun searchPlaces(
        query: String,
        lat: Double,
        long: Double,
        nextCursor: String? = null
    ): Either<Failure, PagedPlacesResult>

    suspend fun getPlace(fsqId: String): Either<Failure, Place>

    class Network @Inject constructor(
        private val networkHandler: NetworkHandler,
        private val placesService: PlacesService
    ) {
        suspend fun searchPlaces(
            query: String,
            lat: Double,
            long: Double,
            nextCursor: String? = null
        ): Either<Failure, PagedPlacesResult> {
            return if (networkHandler.isConnected) {
                val response = placesService.searchPlaces(query, "$lat,$long", nextCursor)
                response.body()?.let {
                    val next = response.headers()["Link"]
                    return Either.Right(
                        PagedPlacesResult(
                            places = flowOf(it.results.map { it.toPlace() }),
                            nextCursor = extractCursorFromLinkHeader(next.orEmpty())
                        )
                    )
                } ?: Either.Left(Failure.ServerError)
            } else {
                Either.Left(Failure.NetworkConnection)
            }
        }

        suspend fun getPlace(fsqId: String): Either<Failure, PlaceDto> {
            return if (networkHandler.isConnected) {
                val response = placesService.getPlace(fsqId)
                response.body()?.let {
                    Either.Right(it)
                } ?: Either.Left(Failure.ServerError)
            } else {
                Either.Left(Failure.NetworkConnection)
            }
        }
    }

    class Database @Inject constructor(
        private val appDatabase: AppDatabase
    ) {
        fun searchPlaces(query: String): Flow<List<PlaceEntity>> {
            return appDatabase.PlaceDao().searchPlaces(query).map { entities ->
                entities
            }
        }

        suspend fun getPlace(fsqId: String): PlaceEntity? {
            return appDatabase.PlaceDao().getPlaceById(fsqId)
        }

        suspend fun insertPlaces(places: List<PlaceEntity>): Either<Failure, UseCase.None> {
            return try {
                appDatabase.PlaceDao().insertPlaces(places)
                Either.Right(UseCase.None())
            } catch (_: Exception) {
                Either.Left(Failure.DatabaseError)
            }
        }
    }

    class PlacesRepositoryImpl @Inject constructor(
        private val network: Network,
        private val database: Database
    ) : PlacesRepository {

        override suspend fun searchPlaces(
            query: String,
            lat: Double,
            long: Double,
            nextCursor: String?
        ): Either<Failure, PagedPlacesResult> {
            return when (val networkResult = network.searchPlaces(query, lat, long, nextCursor)) {
                is Either.Right -> {
                    try {
                        val placesFlow = networkResult.b.places
                        val nextCursor = networkResult.b.nextCursor.orEmpty()

                        val places = placesFlow.first()
                        val placeEntities = places.map { it.toEntity(query) }
                        database.insertPlaces(placeEntities)

                        return Either.Right(PagedPlacesResult(placesFlow, nextCursor))
                    } catch (_: Exception) {
                        Either.Left(Failure.DatabaseError)
                    }
                }

                is Either.Left -> {
                    try {
                        val placesFlow = database.searchPlaces(query).map { entities ->
                            entities.map { it.toPlace() }
                        }
                        Either.Right(PagedPlacesResult(placesFlow, null))
                    } catch (_: Exception) {
                        Either.Left(Failure.DatabaseError)
                    }
                }
            }
        }

        override suspend fun getPlace(fsqId: String): Either<Failure, Place> {
            return when (val networkResult = network.getPlace(fsqId)) {
                is Either.Right -> {
                    val place = networkResult.b.toPlace()
                    val placeEntity = place.toEntity("")
                    database.insertPlaces(listOf(placeEntity))

                    Either.Right(place)
                }

                is Either.Left -> {
                    try {
                        val localPlace = withContext(Dispatchers.IO) {
                            database.getPlace(fsqId)
                        }
                        localPlace?.let {
                            Either.Right(it.toPlace())
                        } ?: Either.Left(Failure.DatabaseError)
                    } catch (_: Exception) {
                        Either.Left(Failure.DatabaseError)
                    }
                }
            }
        }
    }
}

data class PagedPlacesResult(
    val places: Flow<List<Place>>,
    val nextCursor: String?
)


fun PlaceDto.toPlace(): Place {
    return Place(
        fsqId = this.fsq_id,
        name = this.name,
        address = this.location?.address,
        latitude = this.location?.lat,
        longitude = this.location?.lng,
        categories = this.categories?.map { it.name } ?: emptyList(),
        distance = this.distance
    )
}

fun Place.toEntity(searchQuery: String): PlaceEntity {
    return PlaceEntity(
        fsqId = this.fsqId,
        name = this.name,
        address = this.address,
        latitude = this.latitude,
        longitude = this.longitude,
        categories = this.categories.joinToString(","),
        distance = this.distance,
        searchQuery = searchQuery
    )
}

fun PlaceEntity.toPlace(): Place {
    return Place(
        fsqId = this.fsqId,
        name = this.name,
        address = this.address,
        latitude = this.latitude,
        longitude = this.longitude,
        categories = this.categories?.split(",") ?: emptyList(),
        distance = this.distance
    )
}