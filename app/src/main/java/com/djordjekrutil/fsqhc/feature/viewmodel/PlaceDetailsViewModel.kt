package com.djordjekrutil.fsqhc.feature.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djordjekrutil.fsqhc.core.functional.Either
import com.djordjekrutil.fsqhc.feature.model.Place
import com.djordjekrutil.fsqhc.feature.usecase.GetPlaceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaceDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPlaceUseCase: GetPlaceUseCase
) : ViewModel() {

    private val itemId: String = savedStateHandle["id"] ?: ""
    private val _uiState = MutableStateFlow<PlaceDetailsScreenState>(PlaceDetailsScreenState.Loading)
    val uiState: StateFlow<PlaceDetailsScreenState> = _uiState.asStateFlow()

    init {
        getPlaceDetails(itemId)
    }

    private fun getPlaceDetails(fsqId: String) {
        viewModelScope.launch {
            val result = getPlaceUseCase(GetPlaceUseCase.Params(fsqId))
            when (result) {
                is Either.Right -> {
                    updateState(PlaceDetailsScreenState.Content(result.b))
                }
                is Either.Left -> {
                    updateState(PlaceDetailsScreenState.FailedToLoadDetails)
                }
            }
        }
    }

    private fun updateState(newState: PlaceDetailsScreenState) {
        _uiState.value = newState
    }
}

sealed class PlaceDetailsScreenState {
    object Loading : PlaceDetailsScreenState()
    data class Content(val place: Place) : PlaceDetailsScreenState()
    object FailedToLoadDetails : PlaceDetailsScreenState()
}