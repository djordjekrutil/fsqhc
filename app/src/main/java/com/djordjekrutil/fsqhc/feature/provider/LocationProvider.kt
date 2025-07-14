package com.djordjekrutil.fsqhc.feature.provider

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.djordjekrutil.fsqhc.core.exception.Failure
import com.djordjekrutil.fsqhc.core.functional.Either
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class LocationProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Either<Failure, Location> {
        return suspendCancellableCoroutine { continuation ->
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

            if (!hasLocationPermission()) {
                continuation.resume(Either.Left(Failure.PermissionDenied))
                return@suspendCancellableCoroutine
            }

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) continuation.resume(Either.Right(location))
                    else continuation.resume(Either.Left<Failure>(Failure.LocationUnavailable))
                }
                .addOnFailureListener {
                    continuation.resume(Either.Left(Failure.ServerError))
                }
        }
    }
}