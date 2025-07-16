package com.djordjekrutil.fsqhc.feature.usecase

import com.djordjekrutil.fsqhc.core.exception.Failure
import com.djordjekrutil.fsqhc.core.functional.Either
import com.djordjekrutil.fsqhc.core.interactor.UseCase
import com.djordjekrutil.fsqhc.feature.repository.PlacesRepository
import javax.inject.Inject

class SetFavoritePlaceUseCase @Inject constructor(
    private val placesRepository: PlacesRepository
) : UseCase<UseCase.None, SetFavoritePlaceUseCase.Params>() {

    override suspend fun run(params: Params): Either<Failure, None> {
        return placesRepository.setFavorite(params.fsqId, params.isFavorite)
    }

    data class Params(val fsqId: String, val isFavorite: Boolean) {
        companion object {
            fun create(fsqId: String, isFavorite: Boolean) = Params(fsqId, isFavorite)
        }
    }
}