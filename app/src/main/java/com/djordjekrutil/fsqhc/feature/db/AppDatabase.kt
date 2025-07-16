package com.djordjekrutil.fsqhc.feature.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.djordjekrutil.fsqhc.feature.db.dao.PlaceDao
import com.djordjekrutil.fsqhc.feature.model.PlaceEntity

@Database(
    entities = [PlaceEntity::class], version = 1, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun PlaceDao(): PlaceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null)
                return tempInstance
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fsqhc-database"
                )
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}