package com.djordjekrutil.fsqhc.feature.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djordjekrutil.fsqhc.core.functional.Either
import com.djordjekrutil.fsqhc.core.interactor.UseCase
import com.djordjekrutil.fsqhc.feature.model.PagedPlacesResult
import com.djordjekrutil.fsqhc.feature.model.Place
import com.djordjekrutil.fsqhc.feature.repository.LocationRepository
import com.djordjekrutil.fsqhc.feature.usecase.GetCurrentLocationUseCase
import com.djordjekrutil.fsqhc.feature.usecase.GetFavoritePlacesUseCase
import com.djordjekrutil.fsqhc.feature.usecase.SearchPlacesUseCase
import com.djordjekrutil.fsqhc.feature.usecase.SetFavoritePlaceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlacesViewModel @Inject constructor(
    private val searchPlacesUseCase: SearchPlacesUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val setFavoritePlaceUseCase: SetFavoritePlaceUseCase,
    private val getFavoritePlacesUseCase: GetFavoritePlacesUseCase,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlacesScreenState>(PlacesScreenState.Initial)
    val uiState: StateFlow<PlacesScreenState> = _uiState.asStateFlow()

    private val _places = MutableStateFlow<List<Place>>(emptyList())
    private var _favoritesPlaces = MutableStateFlow<List<Place>>(emptyList())

    private val _hasNextPage = MutableStateFlow(false)
    val hasNextPage: StateFlow<Boolean> = _hasNextPage

    private var currentQuery: String? = null
    private var nextCursor: String? = null

    private var currentLocation: Location? = null
    private var loadingMoreInProgress = false

    private fun hasValidLocation(): Boolean = currentLocation != null

    init {
        initializeLocationCheck()
        getFavoritesPlaces()
        syncFavorites()
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
        if (_places.value.isNotEmpty()) {
            updateState(PlacesScreenState.Content(_places))
            return
        } else {
            updateState(PlacesScreenState.LoadingLocation)
            viewModelScope.launch {
                val result = getCurrentLocationUseCase.run(UseCase.None())
                handleLocationResult(result)
            }
        }
    }

    private fun handleLocationResult(result: Either<*, Location>) {
        when (result) {
            is Either.Left -> {
                updateState(PlacesScreenState.Error(PlacesError.Location.FailedToGetLocation))
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
                updateState(PlacesScreenState.Error(PlacesError.Location.NotAvailable))
            }

            trimmedQuery.isBlank() -> {
                updateState(PlacesScreenState.ReadyForSearch)
            }

            else -> {
                performSearch(trimmedQuery)
            }
        }
    }

    private fun performSearch(query: String) {
        val location = currentLocation ?: return
        currentQuery = query

        updateState(PlacesScreenState.Searching)

        viewModelScope.launch {
            val params = SearchPlacesUseCase.Params(query, location.latitude, location.longitude)
            val result = searchPlacesUseCase.run(params)
            handleSearchResult(result)
        }
    }

    fun loadNextPage() {
        if (nextCursor.isNullOrEmpty() || !hasNextPage.value || loadingMoreInProgress) return

        val query = currentQuery ?: return
        val location = currentLocation ?: return

        viewModelScope.launch {
            loadingMoreInProgress = true
            val params =
                SearchPlacesUseCase.Params(query, location.latitude, location.longitude, nextCursor)
            val result = searchPlacesUseCase.run(params)
            handleMoreItemsResult(result)
        }
    }

    private fun handleSearchResult(result: Either<*, PagedPlacesResult>) {
        when (result) {
            is Either.Right -> {
                viewModelScope.launch {
                    _places.value = result.b.places.first()
                    syncFavorites()
                    updateState(PlacesScreenState.Content(_places))
                    _hasNextPage.value = !result.b.nextCursor.isNullOrEmpty()
                    nextCursor = result.b.nextCursor
                }
            }

            is Either.Left -> {
                updateState(PlacesScreenState.Error(PlacesError.SearchFailed))
                _hasNextPage.value = false
                nextCursor = ""
            }
        }
    }

    private fun handleMoreItemsResult(result: Either<*, PagedPlacesResult>) {
        when (result) {
            is Either.Right -> {
                viewModelScope.launch {
                    _places.value = _places.value + result.b.places.first()
                    syncFavorites()
                    updateState(PlacesScreenState.Content(_places))
                    _hasNextPage.value = !result.b.nextCursor.isNullOrEmpty()
                    nextCursor = result.b.nextCursor
                    loadingMoreInProgress = false
                }
            }

            is Either.Left -> {
                _hasNextPage.value = false
                nextCursor = ""
                loadingMoreInProgress = false
            }
        }
    }

    fun retryLocation() {
        getCurrentLocation()
    }

    private fun getFavoritesPlaces() {
        viewModelScope.launch {
            val result = getFavoritePlacesUseCase.run(UseCase.None())
            handleFavoritesPlacesResult(result)
        }
    }

    private fun handleFavoritesPlacesResult(result: Either<*, Flow<List<Place>>>) {
        when (result) {
            is Either.Right -> {
                viewModelScope.launch {
                    result.b.collect { places ->
                        _favoritesPlaces.value = places
                    }
                }
            }

            is Either.Left -> {}
        }
    }

    fun toggleFavorite(fsqId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            val params = SetFavoritePlaceUseCase.Params.create(fsqId, isFavorite)
            setFavoritePlaceUseCase(params)
            _places.update { list ->
                list.map { place ->
                    if (place.fsqId == fsqId) place.copy(isFavorite = isFavorite) else place
                }
            }
            updateState(PlacesScreenState.Content(_places))
        }
    }

    private fun syncFavorites() {
        viewModelScope.launch {
            if (_places.value.isNotEmpty())
            {
                _favoritesPlaces.collect { dbFavoritesList ->
                    val favoriteIds = dbFavoritesList
                        .map { it.fsqId }
                        .toSet()

                    val updatedList = _places.value.map { place ->
                        place.copy(isFavorite = place.fsqId in favoriteIds)
                    }

                    _places.value = updatedList
                    updateState(PlacesScreenState.Content(_places))
                }
            }
        }
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
    data class Content(val places: StateFlow<List<Place>>) : PlacesScreenState()
    data class Error(val error: PlacesError) : PlacesScreenState()
}

sealed class PlacesError {
    sealed class Location : PlacesError() {
        data object FailedToGetLocation : Location()
        data object NotAvailable : Location()
    }

    data object SearchFailed : PlacesError()
}