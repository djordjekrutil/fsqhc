
package com.djordjekrutil.fsqhc.feature.usecase

import com.djordjekrutil.fsqhc.core.exception.Failure
import com.djordjekrutil.fsqhc.core.functional.Either
import com.djordjekrutil.fsqhc.core.interactor.UseCase
import com.djordjekrutil.fsqhc.feature.model.PagedPlacesResult
import com.djordjekrutil.fsqhc.feature.repository.PlacesRepository
import javax.inject.Inject

class SearchPlacesUseCase @Inject constructor(
    private val placesRepository: PlacesRepository
) : UseCase<PagedPlacesResult, SearchPlacesUseCase.Params>() {

    override suspend fun run(params: Params): Either<Failure, PagedPlacesResult> {
        return placesRepository.searchPlaces(params.query, params.lat, params.lon, params.nextCursor)
    }

    data class Params(val query: String, val lat : Double, val lon: Double, val nextCursor: String? = null) {
        companion object {
            fun create(query: String, lat: Double, lon: Double, nextCursor : String): Params {
                return Params(query, lat, lon, nextCursor)
            }
        }
    }
}