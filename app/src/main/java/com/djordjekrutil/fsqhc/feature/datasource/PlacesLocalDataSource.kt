package com.djordjekrutil.fsqhc.feature.datasource

import com.djordjekrutil.fsqhc.core.exception.Failure
import com.djordjekrutil.fsqhc.core.functional.Either
import com.djordjekrutil.fsqhc.core.interactor.UseCase
import com.djordjekrutil.fsqhc.feature.db.AppDatabase
import com.djordjekrutil.fsqhc.feature.model.Place
import com.djordjekrutil.fsqhc.feature.model.PlaceEntity
import com.djordjekrutil.fsqhc.feature.model.mappers.toPlace
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface PlacesLocalDataSource {
    fun searchPlaces(query: String): Flow<List<PlaceEntity>>
    suspend fun getPlace(fsqId: String): PlaceEntity?
    suspend fun insertPlaces(places: List<PlaceEntity>): Either<Failure, UseCase.None>
    suspend fun setFavorite(fsqId: String, isFavorite: Boolean): Either<Failure, UseCase.None>
    fun getFavoritePlaces(): Flow<List<Place>>
}

class PlacesLocalDataSourceImpl @Inject constructor(
    private val appDatabase: AppDatabase
) : PlacesLocalDataSource {

    override fun searchPlaces(query: String): Flow<List<PlaceEntity>> {
        return appDatabase.PlaceDao().searchPlaces(query)
    }

    override suspend fun getPlace(fsqId: String): PlaceEntity? {
        return try {
            appDatabase.PlaceDao().getPlaceById(fsqId)
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun insertPlaces(places: List<PlaceEntity>): Either<Failure, UseCase.None> {
        return try {
            appDatabase.PlaceDao().insertPlaces(places)
            Either.Right(UseCase.None())
        } catch (_: Exception) {
            Either.Left(Failure.DatabaseError)
        }
    }

    override suspend fun setFavorite(fsqId: String, isFavorite: Boolean): Either<Failure, UseCase.None> {
        return try {
            appDatabase.PlaceDao().updateFavoriteStatus(fsqId, isFavorite)
            Either.Right(UseCase.None())
        } catch (_: Exception) {
            Either.Left(Failure.DatabaseError)
        }
    }

    override fun getFavoritePlaces(): Flow<List<Place>> {
        return appDatabase.PlaceDao().getFavoritePlaces().map { entities ->
            entities.map { it.toPlace() }
        }
    }
}
