package com.djordjekrutil.fsqhc.feature.usecase

import com.djordjekrutil.fsqhc.core.exception.Failure
import com.djordjekrutil.fsqhc.core.functional.Either
import com.djordjekrutil.fsqhc.core.interactor.UseCase
import com.djordjekrutil.fsqhc.feature.model.Place
import com.djordjekrutil.fsqhc.feature.repository.PlacesRepository
import javax.inject.Inject

class GetPlaceUseCase @Inject constructor(
    private val placesRepository: PlacesRepository
) : UseCase<Place, GetPlaceUseCase.Params>() {

    override suspend fun run(params: Params): Either<Failure, Place> {
        return placesRepository.getPlace(params.fsqId)
    }

    data class Params(val fsqId: String)
}