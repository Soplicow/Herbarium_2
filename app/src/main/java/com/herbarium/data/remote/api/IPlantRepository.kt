package com.herbarium.data.remote.api

import com.herbarium.data.model.Plant
import com.herbarium.data.remote.dto.PlantDto

interface IPlantRepository {
    suspend fun getPlantsByUser(userId: String): List<PlantDto>
    suspend fun insertPlant(plant: Plant): Boolean
    suspend fun updatePlant(plant: Plant)
    suspend fun deletePlant(plantId: String): Boolean
}