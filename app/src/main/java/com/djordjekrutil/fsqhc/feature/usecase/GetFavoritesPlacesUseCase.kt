package com.djordjekrutil.fsqhc.feature.usecase

import com.djordjekrutil.fsqhc.core.exception.Failure
import com.djordjekrutil.fsqhc.core.functional.Either
import com.djordjekrutil.fsqhc.core.interactor.UseCase
import com.djordjekrutil.fsqhc.feature.model.Place
import com.djordjekrutil.fsqhc.feature.repository.PlacesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoritePlacesUseCase @Inject constructor(
    private val placesRepository: PlacesRepository
) : UseCase<Flow<List<Place>>, UseCase.None>() {

    override suspend fun run(params: None): Either<Failure, Flow<List<Place>>> {
        return try {
            val favorites: Flow<List<Place>> = placesRepository.getFavoritePlaces()
            Either.Right(favorites)
        } catch (_: Exception) {
            Either.Left(Failure.DatabaseError)
        }
    }
}