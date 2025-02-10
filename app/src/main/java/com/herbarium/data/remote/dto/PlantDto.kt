package com.herbarium.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class PlantDto(

    @SerialName("id")
    val id: String,

    @SerialName("name")
    val name: String,

    @SerialName("user_id")
    val user_id: String,

    @SerialName("description")
    val description: String?,

    @SerialName("location")
    val location: JsonObject?
)