package com.herbarium.data.remote.api

import com.herbarium.data.model.Plant
import com.herbarium.data.remote.dto.PlantDto

interface IPlantRepository {
    suspend fun getPlantsByUser(userId: String): List<PlantDto>
    suspend fun insertPlant(plant: Plant): PlantDto
    suspend fun updatePlant(plant: PlantDto): PlantDto
    suspend fun deletePlant(plantId: String): PlantDto
}