package com.djordjekrutil.fsqhc.feature.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.djordjekrutil.fsqhc.feature.model.PlaceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {
    @Query("SELECT * FROM places WHERE searchQuery LIKE '%' || :query || '%' OR name LIKE '%' || :query || '%'")
    fun searchPlaces(query: String): Flow<List<PlaceEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlaces(places: List<PlaceEntity>)

    @Query("SELECT * FROM places WHERE fsqId = :fsqId")
    suspend fun getPlaceById(fsqId: String): PlaceEntity?

    @Query("UPDATE places SET isFavorite = :isFavorite WHERE fsqId = :fsqId")
    suspend fun updateFavoriteStatus(fsqId: String, isFavorite: Boolean)

    @Query("SELECT * FROM places WHERE isFavorite = 1")
    fun getFavoritePlaces(): Flow<List<PlaceEntity>>
}