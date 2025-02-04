package com.herbarium.data.remote.api

import com.herbarium.data.remote.dto.PlantDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

class SupabaseApi(private val supabase: SupabaseClient) {
    suspend fun getPlantsByUser(userId: String): List<PlantDto> {
        return supabase.from("plants")
            .select {
                filter {
                    eq("user_id", userId)
                }
            }.decodeList<PlantDto>()
    }

    suspend fun insertPlant(plant: PlantDto): PlantDto {
        return supabase.from("plants")
            .insert(plant)
            .decodeSingle<PlantDto>()
    }

    suspend fun updatePlant(plant: PlantDto): PlantDto {
        return supabase.from("plants").update(
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

    suspend fun deletePlant(plantId: String): PlantDto {
        return supabase.from("plants").delete {
            select()
            filter {
                eq("id", plantId)
            }
        }.decodeSingle<PlantDto>()
    }
}