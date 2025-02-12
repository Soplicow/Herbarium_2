package com.herbarium.data.model

import com.herbarium.data.local.entity.PlantEntity
import com.herbarium.data.remote.dto.PlantDto
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

fun Plant.toPlantEntity(): PlantEntity {
    return PlantEntity(
        id = this.id,
        name = this.name,
        userId = this.userId,
        photoUrl = "",
        description = this.description,
        location = this.location?.toString()
    )
}

fun PlantEntity.toPlant(image: ByteArray): Plant {
    return Plant(
        id = this.id,
        name = this.name,
        userId = this.userId,
        photo = TODO(),
        description = this.description,
        location = this.location?.let { Json.parseToJsonElement(it) as? JsonObject }
    )
}

fun Plant.toPlantDto(): PlantDto {
    return PlantDto(
        id = this.id,
        name = this.name,
        user_id = this.userId,
        description = this.description,
        location = this.location
    )
}

fun PlantDto.toPlant(image: ByteArray?): Plant {
    return Plant(
        id = this.id,
        name = this.name,
        userId = this.user_id,
        photo = image,
        description = this.description,
        location = this.location
    )
}