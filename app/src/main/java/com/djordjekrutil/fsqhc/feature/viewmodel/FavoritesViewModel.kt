package com.djordjekrutil.fsqhc.feature.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djordjekrutil.fsqhc.core.functional.Either
import com.djordjekrutil.fsqhc.core.interactor.UseCase
import com.djordjekrutil.fsqhc.feature.model.Place
import com.djordjekrutil.fsqhc.feature.usecase.GetFavoritePlacesUseCase
import com.djordjekrutil.fsqhc.feature.usecase.SetFavoritePlaceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoritePlacesUseCase: GetFavoritePlacesUseCase,
    private val setFavoritePlaceUseCase: SetFavoritePlaceUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow<FavoritesScreenState>(FavoritesScreenState.Initial)
    val uiState: StateFlow<FavoritesScreenState> = _uiState.asStateFlow()

    init {
        loadFavoritePlaces()
    }

    private fun loadFavoritePlaces() {
        viewModelScope.launch {
            _uiState.value = FavoritesScreenState.Loading
            val result = getFavoritePlacesUseCase.run(UseCase.None())
            handleFavoritesPlacesResult(result)
        }
    }

    private fun handleFavoritesPlacesResult(result: Either<*, Flow<List<Place>>>) {
        when (result) {
            is Either.Right -> {
                viewModelScope.launch {
                    val places = result.b
                    places.collect { result->
                        _uiState.value = if (result.isEmpty()) {
                            FavoritesScreenState.Empty
                        } else {
                            FavoritesScreenState.Success(result)
                        }
                    }
                }
            }
            is Either.Left -> {
                _uiState.value = FavoritesScreenState.Error
            }
        }
    }

    fun toggleFavorite(fsqId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            var params = SetFavoritePlaceUseCase.Params.create(fsqId, isFavorite)
            setFavoritePlaceUseCase(params)
        }
    }

}

sealed class FavoritesScreenState {
    object Initial : FavoritesScreenState()
    object Loading : FavoritesScreenState()
    data class Success(val places: List<Place>) : FavoritesScreenState()
    object Error : FavoritesScreenState()
    object Empty : FavoritesScreenState()
}