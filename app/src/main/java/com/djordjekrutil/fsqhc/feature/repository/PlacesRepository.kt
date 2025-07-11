package com.djordjekrutil.fsqhc.feature.repository

import com.djordjekrutil.fsqhc.core.exception.Failure
import com.djordjekrutil.fsqhc.core.functional.Either
import com.djordjekrutil.fsqhc.core.interactor.UseCase
import com.djordjekrutil.fsqhc.core.platform.NetworkHandler
import com.djordjekrutil.fsqhc.feature.db.AppDatabase
import com.djordjekrutil.fsqhc.feature.model.Place
import com.djordjekrutil.fsqhc.feature.model.PlaceDto
import com.djordjekrutil.fsqhc.feature.model.PlaceEntity
import com.djordjekrutil.fsqhc.feature.service.PlacesService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface PlacesRepository {

    suspend fun searchPlaces(query: String): Either<Failure, Flow<List<Place>>>
    suspend fun getPlace(fsqId: String): Either<Failure, Place>

    class Network @Inject constructor(
        private val networkHandler: NetworkHandler,
        private val placesService: PlacesService
    ) {
        fun searchPlaces(query: String): Either<Failure, List<PlaceDto>> {
            return if (networkHandler.isConnected) {
                val response = placesService.searchPlaces(query).execute()
                response.body()?.let {
                    Either.Right(it.results)
                } ?: Either.Left(Failure.ServerError)
            } else {
                Either.Left(Failure.NetworkConnection)
            }
        }

        fun getPlace(fsqId: String): Either<Failure, PlaceDto> {
            return if (networkHandler.isConnected) {
                val response = placesService.getPlace(fsqId).execute()
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
            } catch (e: Exception) {
                Either.Left(Failure.DatabaseError)
            }
        }
    }

    class PlacesRepositoryImpl @Inject constructor(
        private val network: Network,
        private val database: Database
    ) : PlacesRepository {

        override suspend fun searchPlaces(query: String): Either<Failure, Flow<List<Place>>> {
            return when (val networkResult = network.searchPlaces(query)) {
                is Either.Right -> {
                    try {
                        val places = networkResult.b.map { it.toPlace() }
                        val placeEntities = places.map { it.toEntity(query) }
                        database.insertPlaces(placeEntities)

                        val directFlow = kotlinx.coroutines.flow.flowOf(places)
                        Either.Right(directFlow)
                    } catch (e: Exception) {
                        Either.Left(Failure.DatabaseError)
                    }
                }
                is Either.Left -> {
                    try {
                        val placesFlow = database.searchPlaces(query).map { entities ->
                            entities.map { it.toPlace() }
                        }
                        Either.Right(placesFlow)
                    } catch (e: Exception) {
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
                    } catch (e: Exception) {
                        Either.Left(Failure.DatabaseError)
                    }
                }
            }
        }
    }
}


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