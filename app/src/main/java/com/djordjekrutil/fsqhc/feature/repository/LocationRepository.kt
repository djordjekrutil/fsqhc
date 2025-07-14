package com.djordjekrutil.fsqhc.feature.repository

import android.location.Location
import com.djordjekrutil.fsqhc.feature.provider.LocationProvider
import javax.inject.Inject
import com.djordjekrutil.fsqhc.core.functional.Either
import com.djordjekrutil.fsqhc.core.exception.Failure

interface LocationRepository {
    suspend fun getCurrentLocation(): Either<Failure, Location>
    fun hasLocationPermission(): Boolean

    class LocationRepositoryImpl @Inject constructor(
        private val locationProvider: LocationProvider
    ) : LocationRepository {
        override suspend fun getCurrentLocation(): Either<Failure, Location> {
            return locationProvider.getCurrentLocation()
        }

        override fun hasLocationPermission(): Boolean {
            return locationProvider.hasLocationPermission()
        }
    }
}