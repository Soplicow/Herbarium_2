package com.herbarium.data.model

import kotlinx.serialization.json.JsonObject

data class Plant(
    val id: String,
    val name: String,
    val userId: String,
    val photo: ByteArray?,
    val description: String?,
    val location: JsonObject?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Plant

        if (id != other.id) return false
        if (name != other.name) return false
        if (userId != other.userId) return false
        if (!photo.contentEquals(other.photo)) return false
        if (description != other.description) return false
        if (location != other.location) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + userId.hashCode()
        result = 31 * result + photo.contentHashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (location?.hashCode() ?: 0)
        return result
    }
}