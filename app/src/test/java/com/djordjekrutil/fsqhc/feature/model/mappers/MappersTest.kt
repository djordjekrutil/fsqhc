package com.djordjekrutil.fsqhc.feature.model.mappers

import com.djordjekrutil.fsqhc.feature.model.Place
import com.djordjekrutil.fsqhc.feature.model.PlaceDto
import com.djordjekrutil.fsqhc.feature.model.PlaceEntity
import com.djordjekrutil.fsqhc.feature.model.LocationDto
import com.djordjekrutil.fsqhc.feature.model.CategoryDto
import com.djordjekrutil.fsqhc.feature.model.HoursDto
import com.djordjekrutil.fsqhc.feature.model.PhotoDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

class MappersTest {

    @Test
    fun `PlaceDto to Place should map correctly`() {
        // Given
        val placeDto = PlaceDto(
            fsq_id = "test123",
            name = "Test Restaurant",
            location = LocationDto(
                address = "Test Address 123",
                lat = 44.7866,
                lng = 20.4489
            ),
            categories = listOf(
                CategoryDto(name = "Restaurant", icon = null),
                CategoryDto(name = "Italian", icon = null)
            ),
            distance = 500,
            rating = 4.5,
            price = 2,
            hours = HoursDto(open_now = true, display = null, is_local_holiday = null, regular = null),
            photos = listOf(
                PhotoDto(
                    id = "test",
                    prefix = "https://example.com/",
                    suffix = "/photo.jpg",
                    width = 300,
                    height = 300
                )
            )
        )

        // When
        val result = placeDto.toPlace()

        // Then
        assertEquals("test123", result.fsqId)
        assertEquals("Test Restaurant", result.name)
        assertEquals("Test Address 123", result.address)
        assertEquals(44.7866, result.latitude!!, 0.001)
        assertEquals(20.4489, result.longitude!!, 0.001)
        assertEquals(listOf("Restaurant", "Italian"), result.categories)
        assertEquals(500, result.distance)
        assertEquals(4.5, result.rating!!, 0.001)
        assertEquals(2, result.price)
        assertEquals(true, result.isOpen)
        assertEquals("https://example.com/300x300/photo.jpg", result.photoUrl)
        assertFalse(result.isFavorite) // Default value
        assertNull(result.details) // Basic search doesn't have details
    }

    @Test
    fun `Place to PlaceEntity should map correctly`() {
        // Given
        val place = Place(
            fsqId = "test123",
            name = "Test Restaurant",
            address = "Test Address 123",
            latitude = 44.7866,
            longitude = 20.4489,
            categories = listOf("Restaurant", "Italian"),
            distance = 500,
            isFavorite = true,
            rating = 4.5,
            price = 2,
            isOpen = true,
            photoUrl = "https://example.com/photo.jpg"
        )

        // When
        val result = place.toEntity("pizza")

        // Then
        assertEquals("test123", result.fsqId)
        assertEquals("Test Restaurant", result.name)
        assertEquals("Test Address 123", result.address)
        assertEquals(44.7866, result.latitude!!, 0.001)
        assertEquals(20.4489, result.longitude!!, 0.001)
        assertEquals("[\"Restaurant\",\"Italian\"]", result.categories)
        assertEquals(500, result.distance)
        assertEquals("pizza", result.searchQuery)
        assertEquals(true, result.isFavorite)
        assertEquals(4.5, result.rating!!, 0.001)
        assertEquals(2, result.price)
        assertEquals(true, result.isOpen)
        assertEquals("https://example.com/photo.jpg", result.photoUrl)
        assertNull(result.detailsJson) // No details in this test
        assertEquals(false, result.hasFullDetails)
        assertNull(result.detailsLastUpdated)
    }
} 