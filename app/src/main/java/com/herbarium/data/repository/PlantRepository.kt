package com.herbarium.data.repository

import com.herbarium.data.local.dao.PlantDao
import com.herbarium.data.model.Plant
import com.herbarium.data.model.toPlant
import com.herbarium.data.model.toPlantDto
import com.herbarium.data.model.toPlantEntity
import com.herbarium.data.remote.api.SupabaseApi
import javax.inject.Inject

class PlantRepository @Inject constructor(
    private val plantDao: PlantDao,
    private val supabaseApi: SupabaseApi
) {
    suspend fun getPlantsByUser(userId: String): List<Plant> {
        // Try to fetch from local database first
        val localPlants = plantDao.getPlantsByUser(userId)
        if (localPlants.isNotEmpty()) {
            return localPlants.map { it.toPlant() }
        }

        // Fetch from remote if local data is empty
        val remotePlants = supabaseApi.getPlantsByUser(userId)
        val plantEntities = remotePlants.map { it.toPlant().toPlantEntity() }
        plantDao.insertAll(plantEntities)

        return remotePlants.map { it.toPlant() }
    }

    suspend fun insertPlant(plant: Plant) {
        // Insert into remote database
        supabaseApi.insertPlant(plant.toPlantDto())

        // Insert into local database
        plantDao.insert(plant.toPlantEntity())
    }

    suspend fun updatePlant(plant: Plant) {
        // Update in remote database
        supabaseApi.updatePlant(plant.toPlantDto())

        // Update in local database
        plantDao.update(plant.toPlantEntity())
    }

    suspend fun deletePlant(plantId: String) {
        // Delete from remote database
        supabaseApi.deletePlant(plantId)

        // Delete from local database
        val plantEntity = plantDao.getPlantById(plantId)
        plantEntity?.let { plantDao.delete(it) }
    }
}