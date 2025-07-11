package com.djordjekrutil.fsqhc.feature.viewmodel

import androidx.lifecycle.ViewModel
import com.djordjekrutil.fsqhc.core.functional.Either
import com.djordjekrutil.fsqhc.feature.model.Place
import com.djordjekrutil.fsqhc.feature.usecase.SearchPlacesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class PlacesViewModel @Inject constructor(
    private val searchPlacesUseCase: SearchPlacesUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow<PlacesScreenState>(PlacesScreenState.StartSearch)
    val uiState : StateFlow<PlacesScreenState> = _uiState.asStateFlow()

    fun searchPlaces(query : String)
    {
        _uiState.value = PlacesScreenState.Loading
        searchPlacesUseCase(SearchPlacesUseCase.Params(query)) {result ->
            when(result){
                is Either.Right -> {
                    val places = result.b
                    _uiState.value = PlacesScreenState.Content(places)
                }
                is Either.Left -> {
                    _uiState.value = PlacesScreenState.Error("Failed to get search result")
                }
            }
        }
    }
}

sealed class PlacesScreenState {
    data object StartSearch : PlacesScreenState()
    data object Loading : PlacesScreenState()
    data class Content(val places : Flow<List<Place>>) : PlacesScreenState()
    data class Error(val message : String) : PlacesScreenState()
}