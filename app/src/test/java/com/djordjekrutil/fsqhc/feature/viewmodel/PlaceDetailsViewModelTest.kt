package com.djordjekrutil.fsqhc.feature.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.djordjekrutil.fsqhc.core.functional.Either
import com.djordjekrutil.fsqhc.core.exception.Failure
import com.djordjekrutil.fsqhc.feature.model.Place
import com.djordjekrutil.fsqhc.feature.usecase.GetPlaceUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class PlaceDetailsViewModelTest {
    private lateinit var getPlaceUseCase: GetPlaceUseCase
    private lateinit var savedStateHandle: SavedStateHandle
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getPlaceUseCase = mock()
        savedStateHandle = SavedStateHandle(mapOf("id" to "test_id"))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState should be Content when getPlaceUseCase returns Right`() = runTest(testDispatcher) {
        // Given
        val place = Place(
            fsqId = "test_id",
            name = "Test Place",
            address = "Test Address",
            latitude = 1.0,
            longitude = 2.0,
            categories = listOf("Category1"),
            distance = 100,
            isFavorite = false
        )
        whenever(getPlaceUseCase.invoke(GetPlaceUseCase.Params("test_id")))
            .thenReturn(Either.Right(place))

        // When
        val viewModel = PlaceDetailsViewModel(savedStateHandle, getPlaceUseCase)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is PlaceDetailsScreenState.Content)
        assertEquals(place, (state as PlaceDetailsScreenState.Content).place)
    }

    @Test
    fun `uiState should be FailedToLoadDetails when getPlaceUseCase returns Left`() = runTest(testDispatcher) {
        // Given
        whenever(getPlaceUseCase.invoke(GetPlaceUseCase.Params("test_id")))
            .thenReturn(Either.Left(Failure.NetworkConnection))

        // When
        val viewModel = PlaceDetailsViewModel(savedStateHandle, getPlaceUseCase)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is PlaceDetailsScreenState.FailedToLoadDetails)
    }
} 