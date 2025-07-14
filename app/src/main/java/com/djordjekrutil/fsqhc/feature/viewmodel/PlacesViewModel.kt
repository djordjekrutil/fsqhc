package com.djordjekrutil.fsqhc.feature.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djordjekrutil.fsqhc.core.functional.Either
import com.djordjekrutil.fsqhc.core.interactor.UseCase
import com.djordjekrutil.fsqhc.feature.model.Place
import com.djordjekrutil.fsqhc.feature.repository.LocationRepository
import com.djordjekrutil.fsqhc.feature.usecase.GetCurrentLocationUseCase
import com.djordjekrutil.fsqhc.feature.usecase.SearchPlacesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlacesViewModel @Inject constructor(
    private val searchPlacesUseCase: SearchPlacesUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlacesScreenState>(PlacesScreenState.Initial)
    val uiState: StateFlow<PlacesScreenState> = _uiState.asStateFlow()

    private var currentLocation: Location? = null

    init {
        initializeLocationCheck()
    }

    private fun initializeLocationCheck() {
        if (locationRepository.hasLocationPermission()) {
            getCurrentLocation()
        } else {
            updateState(PlacesScreenState.PermissionRequired)
        }
    }

    fun onPermissionGranted() {
        getCurrentLocation()
    }

    fun onPermissionDenied() {
        updateState(PlacesScreenState.PermissionDenied)
    }

    fun onPermissionPermanentlyDenied() {
        updateState(PlacesScreenState.PermissionPermanentlyDenied)
    }

    private fun getCurrentLocation() {
        updateState(PlacesScreenState.LoadingLocation)

        viewModelScope.launch {
            val result = getCurrentLocationUseCase.run(UseCase.None())
            handleLocationResult(result)
        }
    }

    private fun handleLocationResult(result: Either<*, Location>) {
        when (result) {
            is Either.Left -> {
                updateState(PlacesScreenState.Error("Failed to get current location"))
            }
            is Either.Right -> {
                currentLocation = result.b
                updateState(PlacesScreenState.ReadyForSearch)
            }
        }
    }

    fun search(query: String) {
        val trimmedQuery = query.trim()

        when {
            !hasValidLocation() -> {
                updateState(PlacesScreenState.Error("Location not available"))
            }
            trimmedQuery.isBlank() -> {
                updateState(PlacesScreenState.ReadyForSearch)
            }
            else -> {
                performSearch(trimmedQuery)
            }
        }
    }

    private fun hasValidLocation(): Boolean = currentLocation != null

    private fun performSearch(query: String) {
        val location = currentLocation ?: return

        updateState(PlacesScreenState.Searching)

        viewModelScope.launch {
            val params = SearchPlacesUseCase.Params(query, location.latitude, location.longitude)
            val result = searchPlacesUseCase.run(params)
            handleSearchResult(result)
        }
    }

    private fun handleSearchResult(result: Either<*, Flow<List<Place>>>) {
        when (result) {
            is Either.Right -> {
                updateState(PlacesScreenState.Content(result.b))
            }
            is Either.Left -> {
                updateState(PlacesScreenState.Error("Failed to search places"))
            }
        }
    }

    fun retryLocation() {
        getCurrentLocation()
    }

    private fun updateState(newState: PlacesScreenState) {
        _uiState.value = newState
    }
}

sealed class PlacesScreenState {
    data object Initial : PlacesScreenState()
    data object PermissionRequired : PlacesScreenState()
    data object PermissionDenied : PlacesScreenState()
    data object PermissionPermanentlyDenied : PlacesScreenState()
    data object LoadingLocation : PlacesScreenState()
    data object ReadyForSearch : PlacesScreenState()
    data object Searching : PlacesScreenState()
    data class Content(val places: Flow<List<Place>>) : PlacesScreenState()
    data class Error(val message: String) : PlacesScreenState()
}