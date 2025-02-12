package com.herbarium.data.remote.api

import com.google.firebase.storage.StorageException
import com.herbarium.BuildConfig
import com.herbarium.data.model.Plant
import com.herbarium.data.model.toPlant
import com.herbarium.data.model.toPlantDto
import com.herbarium.data.remote.dto.PlantDto
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.exceptions.NotFoundRestException
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.update
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlantRepository @Inject constructor (
    private val postgrest: Postgrest,
    private val storage: Storage,
    private val auth: Auth
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

    suspend fun List<PlantDto>.listPlants(): List<Plant> = coroutineScope {
        map { plant ->
            async { // Process downloads in parallel
                try {
                    val image = storage.from("plant_images")
                        .downloadAuthenticated("${plant.user_id}/${plant.id}.jpg")
                    plant.toPlant(image)
                } catch (e: NotFoundRestException) {
                    when (e) {
                        e -> plant.toPlant(null)
                        else -> throw e
                    }
                }
            }
        }.awaitAll()
    }

    suspend fun getDomainPlants(userId: String): List<Plant> {
        return getPlantsByUser(userId).listPlants()
    }

    suspend fun getPlantById(plantId: String): Plant? = coroutineScope {
        val image: ByteArray
        withContext(Dispatchers.IO) {
            val plantDto = postgrest.from("plants")
                .select {
                    filter {
                        eq("id", plantId)
                    }
                }
               .decodeSingleOrNull<PlantDto>()

            if (plantDto != null) {
                try {
                    image = storage.from("plant_images")
                        .downloadAuthenticated(
                            "${plantDto.user_id}/${plantDto.id}.jpg"
                        )
                    return@withContext plantDto.toPlant(image)

                } catch (e: NotFoundRestException) {
                    when (e) {
                        e -> return@withContext plantDto.toPlant(null)
                        else -> throw e
                    }
                }
            } else {
                return@withContext null
            }
        }
    }

    override suspend fun insertPlant(plant: Plant): Boolean {
        val image = plant.photo
        val newPlant = plant.toPlantDto()
        return try {
            withContext(Dispatchers.IO) {
                postgrest.from("plants")
                    .insert(newPlant)

                if (image != null){
                    storage.from("plant_images").upload(
                        path = "${newPlant.user_id}/${plant.id}.jpg",
                        data = image,
                        ) {
                        upsert = true
                    }
                }
                true
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updatePlant(plant: Plant) {
        val newPlant = plant.toPlantDto()
        println("new plant id " + newPlant.id)
        println("new plant name " + newPlant.name)
        val image = plant.photo
        try {
            withContext(Dispatchers.IO) {
                postgrest.from("plants").update(
                    {
                        set("description", newPlant.description)
                        Plant::location setTo newPlant.location
                        set("name", newPlant.name)
                    }
                ) {
                    filter {
                        eq("id", newPlant.id)
                    }
                }

                if (image != null) {
                    storage.from("plant_images").update(
                        path = "${newPlant.user_id}/${newPlant.id}.jpg",
                        data = image,
                    ) {
                        upsert = true
                    }
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deletePlant(plantId: String): Boolean {
        return try {
            postgrest.from("plants").delete {
                select()
                filter {
                    eq("id", plantId)
                }
            }
            true
        } catch (e: Exception) {
            throw e
        }
    }

    fun getUserId(): String {
        return auth.currentUserOrNull()?.id ?: "nwah"
    }

    private fun constructImageUrl(fileName: String): String =
        "${BuildConfig.SUPABASE_URL}/storage/v1/s3" +
                auth.currentUserOrNull()?.id.toString() +
                fileName
}