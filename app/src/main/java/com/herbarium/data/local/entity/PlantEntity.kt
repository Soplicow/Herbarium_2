package com.herbarium.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plants")
data class PlantEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "photo_url") val photoUrl: String,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "location") val location: String? // Store as JSON string
)

// PlantEntity is the schema for the plant inside the Room database (local database)