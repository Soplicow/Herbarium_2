package com.herbarium.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import com.herbarium.data.local.dao.PlantDao
import com.herbarium.data.local.entity.PlantEntity
import androidx.room.RoomDatabase

@Database(entities = [PlantEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "plant_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// AppDatabase provides access to the database. getDatabase should be used to get an instance of the database