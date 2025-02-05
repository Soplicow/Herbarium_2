package com.herbarium.data.remote.api

import com.herbarium.data.model.Plant
import com.herbarium.data.model.toPlantDto
import com.herbarium.data.remote.dto.PlantDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlantRepository @Inject constructor (
    private val postgrest: Postgrest,
    private val storage: Storage,
): IPlantRepository {
    override suspend fun getPlantsByUser(userId: String): List<PlantDto> {
        return withContext(Dispatchers.IO) {
            postgrest.from("plants")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }.decodeList<PlantDto>()
        }
    }

    override suspend fun insertPlant(plant: Plant): PlantDto {
        val newPlant = plant.toPlantDto()
        return withContext(Dispatchers.IO) {
            postgrest.from("plants")
                .insert(newPlant)
                .decodeSingle<PlantDto>()
        }
    }

    override suspend fun updatePlant(plant: PlantDto): PlantDto {
        return withContext(Dispatchers.IO) {
            postgrest.from("plants").update(
                {
                    set("description", plant.description)
                    set("location", plant.location)
                    set("photo_url", plant.photo_url)
                }
            ) {
                select()
                filter {
                    eq("id", plant.id)
                }
            }.decodeSingle<PlantDto>()
        }
    }

    override suspend fun deletePlant(plantId: String): PlantDto {
        return postgrest.from("plants").delete {
            select()
            filter {
                eq("id", plantId)
            }
        }.decodeSingle<PlantDto>()
    }
}