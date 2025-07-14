
package com.djordjekrutil.fsqhc.feature.usecase

import com.djordjekrutil.fsqhc.core.exception.Failure
import com.djordjekrutil.fsqhc.core.functional.Either
import com.djordjekrutil.fsqhc.core.interactor.UseCase
import com.djordjekrutil.fsqhc.feature.model.Place
import com.djordjekrutil.fsqhc.feature.repository.PlacesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchPlacesUseCase @Inject constructor(
    private val placesRepository: PlacesRepository
) : UseCase<Flow<List<Place>>, SearchPlacesUseCase.Params>() {

    override suspend fun run(params: Params): Either<Failure, Flow<List<Place>>> {
        return placesRepository.searchPlaces(params.query, params.lat, params.lon)
    }

    data class Params(val query: String, val lat : Double, val lon: Double) {
        companion object {
            fun create(query: String, lat: Double, lon: Double): Params {
                return Params(query, lat, lon)
            }
        }
    }
}