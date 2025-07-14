package com.djordjekrutil.fsqhc.feature.usecase

import android.location.Location
import com.djordjekrutil.fsqhc.core.exception.Failure
import com.djordjekrutil.fsqhc.core.functional.Either
import com.djordjekrutil.fsqhc.core.interactor.UseCase
import com.djordjekrutil.fsqhc.feature.repository.LocationRepository
import javax.inject.Inject

class GetCurrentLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) : UseCase<Location, UseCase.None>() {

    override suspend fun run(params: None): Either<Failure, Location> {
        return locationRepository.getCurrentLocation()
    }
}