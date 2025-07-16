package com.djordjekrutil.fsqhc.feature.usecase

import android.location.Location
import com.djordjekrutil.fsqhc.core.exception.Failure
import com.djordjekrutil.fsqhc.core.functional.Either
import com.djordjekrutil.fsqhc.core.interactor.UseCase
import com.djordjekrutil.fsqhc.feature.repository.LocationRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class GetCurrentLocationUseCaseTest {

    private lateinit var useCase: GetCurrentLocationUseCase
    private lateinit var locationRepository: LocationRepository

    @Before
    fun setup() {
        locationRepository = mock()
        useCase = GetCurrentLocationUseCase(locationRepository)
    }

    @Test
    fun `should return location when repository returns success`() = runTest {
        // Given
        val expectedLocation = mock<Location>()
        whenever(locationRepository.getCurrentLocation()).thenReturn(Either.Right(expectedLocation))

        // When
        val result = useCase.run(UseCase.None())

        // Then
        assertTrue(result is Either.Right)
        assertEquals(expectedLocation, (result as Either.Right).b)
    }

    @Test
    fun `should return failure when repository returns failure`() = runTest {
        // Given
        whenever(locationRepository.getCurrentLocation()).thenReturn(Either.Left(Failure.LocationUnavailable))

        // When
        val result = useCase.run(UseCase.None())

        // Then
        assertTrue(result is Either.Left)
        assertEquals(Failure.LocationUnavailable, (result as Either.Left).a)
    }
} 