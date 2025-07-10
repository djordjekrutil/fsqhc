package com.djordjekrutil.fsqhc.feature.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.djordjekrutil.fsqhc.feature.model.PlaceEntity

@Dao
interface PlaceDao {
    @Query("SELECT * FROM places WHERE searchQuery LIKE '%' || :query || '%' OR name LIKE '%' || :query || '%'")
    suspend fun searchPlaces(query: String): List<PlaceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaces(places: List<PlaceEntity>)

    @Query("DELETE FROM places WHERE searchQuery = :query")
    suspend fun deleteOldResults(query: String)

    @Query("SELECT * FROM places WHERE fsqId = :fsqId")
    suspend fun getPlaceById(fsqId: String): PlaceEntity?
}