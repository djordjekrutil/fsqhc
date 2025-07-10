
package com.djordjekrutil.fsqhc.feature.usecase

import com.djordjekrutil.fsqhc.core.exception.Failure
import com.djordjekrutil.fsqhc.core.functional.Either
import com.djordjekrutil.fsqhc.core.interactor.UseCase
import com.djordjekrutil.fsqhc.feature.model.Place
import com.djordjekrutil.fsqhc.feature.repository.PlacesRepository
import javax.inject.Inject

class SearchPlacesUseCase @Inject constructor(
    private val placesRepository: PlacesRepository
) : UseCase<List<Place>, SearchPlacesUseCase.Params>() {

    override suspend fun run(params: Params): Either<Failure, List<Place>> {
        return placesRepository.searchPlaces(params.query)
    }

    data class Params(val query: String)
}