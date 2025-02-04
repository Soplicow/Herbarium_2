package com.herbarium.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.herbarium.data.local.entity.PlantEntity

@Dao
interface PlantDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(plant: PlantEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plants: List<PlantEntity>)

    @Update
    suspend fun update(plant: PlantEntity)

    @Delete
    suspend fun delete(plant: PlantEntity)

    @Query("SELECT * FROM plants WHERE user_id = :userId")
    suspend fun getPlantsByUser(userId: String): List<PlantEntity>

    @Query("SELECT * FROM plants WHERE id = :plantId")
    suspend fun getPlantById(plantId: String): PlantEntity?
}

// PlantDao interacts with the Room Database (local database)