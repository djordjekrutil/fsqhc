package com.djordjekrutil.fsqhc.feature.repository

import com.djordjekrutil.fsqhc.core.exception.Failure
import com.djordjekrutil.fsqhc.core.functional.Either
import com.djordjekrutil.fsqhc.core.interactor.UseCase
import com.djordjekrutil.fsqhc.feature.datasource.PlacesLocalDataSource
import com.djordjekrutil.fsqhc.feature.datasource.PlacesNetworkDataSource
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class PlacesRepositoryTest {

    private lateinit var repository: PlacesRepositoryImpl
    private lateinit var networkDataSource: PlacesNetworkDataSource
    private lateinit var localDataSource: PlacesLocalDataSource

    @Before
    fun setup() {
        networkDataSource = mock()
        localDataSource = mock()
        repository = PlacesRepositoryImpl(networkDataSource, localDataSource)
    }

    @Test
    fun `setFavorite should return success when local data source succeeds`() = runTest {
        // Given
        val fsqId = "test123"
        val isFavorite = true
        whenever(localDataSource.setFavorite(fsqId, isFavorite))
            .thenReturn(Either.Right(UseCase.None()))

        // When
        val result = repository.setFavorite(fsqId, isFavorite)

        // Then
        assertTrue(result is Either.Right)
        verify(localDataSource).setFavorite(fsqId, isFavorite)
    }

    @Test
    fun `setFavorite should return failure when local data source fails`() = runTest {
        // Given
        val fsqId = "test123"
        val isFavorite = false
        whenever(localDataSource.setFavorite(fsqId, isFavorite))
            .thenReturn(Either.Left(Failure.DatabaseError))

        // When
        val result = repository.setFavorite(fsqId, isFavorite)

        // Then
        assertTrue(result is Either.Left)
        assertEquals(Failure.DatabaseError, (result as Either.Left).a)
        verify(localDataSource).setFavorite(fsqId, isFavorite)
    }
} 