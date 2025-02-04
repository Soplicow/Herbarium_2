package com.herbarium.data.model

import com.herbarium.data.local.entity.PlantEntity
import com.herbarium.data.remote.dto.PlantDto
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

fun Plant.toPlantEntity(): PlantEntity {
    return PlantEntity(
        id = this.id,
        userId = this.userId,
        photoUrl = this.photoUrl,
        description = this.description,
        location = this.location?.toString()
    )
}

fun PlantEntity.toPlant(): Plant {
    return Plant(
        id = this.id,
        userId = this.userId,
        photoUrl = this.photoUrl,
        description = this.description,
        location = this.location?.let { Json.parseToJsonElement(it) as? JsonObject }
    )
}

fun Plant.toPlantDto(): PlantDto {
    return PlantDto(
        id = this.id,
        user_id = this.userId,
        photo_url = this.photoUrl,
        description = this.description,
        location = this.location
    )
}

fun PlantDto.toPlant(): Plant {
    return Plant(
        id = this.id,
        userId = this.user_id,
        photoUrl = this.photo_url,
        description = this.description,
        location = this.location
    )
}