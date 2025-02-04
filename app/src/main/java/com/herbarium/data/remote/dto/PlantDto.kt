package com.herbarium.data.remote.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import org.json.JSONObject

@Serializable
data class PlantDto(
    val id: String,
    val user_id: String,
    val photo_url: String,
    val description: String?,
    val location: JsonObject? // Use JsonObject for flexibility
)