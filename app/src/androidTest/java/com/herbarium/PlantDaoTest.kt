package com.herbarium

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.herbarium.data.local.AppDatabase
import com.herbarium.data.local.dao.PlantDao
import com.herbarium.data.local.entity.PlantEntity
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

class PlantDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var plantDao: PlantDao

    @Before
    fun setup() {
        // Use in-memory database for testing
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        plantDao = database.plantDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertPlantAndRetrieveById() = runBlocking {
        // Given: A plant entity
        val plant = PlantEntity(
            id = "1",
            userId = "user1",
            photoUrl = "https://example.com/plant.jpg",
            description = "Test plant",
            location = null
        )

        // When: Insert and retrieve
        plantDao.insert(plant)
        val retrievedPlant = plantDao.getPlantById("1")

        // Then: Verify the retrieved plant matches
        assertEquals(retrievedPlant, plant)
    }

    @Test
    fun deletePlant() = runBlocking {
        // Given: Insert a plant
        val plant = PlantEntity("1", "user1", "url", "Test plant", null)
        plantDao.insert(plant)

        // When: Delete the plant
        plantDao.delete(plant)
        val retrievedPlant = plantDao.getPlantById("1")

        // Then: Plant should be deleted
        assertEquals(retrievedPlant, null)
    }
}