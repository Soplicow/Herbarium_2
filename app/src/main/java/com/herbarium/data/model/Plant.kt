package com.herbarium.data.model

import kotlinx.serialization.json.JsonObject

data class Plant(
    val id: String,
    val userId: String,
    val photoUrl: String,
    val description: String?,
    val location: JsonObject?
)