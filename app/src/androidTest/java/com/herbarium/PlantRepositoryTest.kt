package com.herbarium

import com.herbarium.data.local.dao.PlantDao
import com.herbarium.data.local.entity.PlantEntity
import com.herbarium.data.remote.api.SupabaseApi
import com.herbarium.data.remote.dto.PlantDto
import com.herbarium.data.repository.PlantRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.MockKAnnotations
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class PlantRepositoryTest {
    private val plantDao = mockk<PlantDao>()
    private val supabaseApi = mockk<SupabaseApi>()
    private lateinit var repository: PlantRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        repository = PlantRepository(plantDao, supabaseApi)
    }

    @Test
    fun getPlantsByUser_LocalDataAvailable() = runTest {
        // Given: Local data exists
        val localPlants = listOf(
            PlantEntity("1", "user1", "url1", "Plant 1", null),
            PlantEntity("2", "user1", "url2", "Plant 2", null)
        )

        coEvery { plantDao.getPlantsByUser("user1") } returns localPlants

        // When: Fetch plants
        val result = repository.getPlantsByUser("user1")

        // Then: Return local data without calling remote
        assertEquals(result.size, 2)
        coVerify(exactly = 0) { supabaseApi.getPlantsByUser(any()) }
    }

    @Test
    fun getPlantsByUser_RemoteFallback() = runTest {
        // Given: No local data, mock remote response
        coEvery { plantDao.getPlantsByUser("user1") } returns emptyList()
        val remotePlants = listOf(
            PlantDto("1", "user1", "url1", "Plant 1", null),
            PlantDto("2", "user1", "url2", "Plant 2", null)
        )
        coEvery { supabaseApi.getPlantsByUser("user1") } returns remotePlants
        coEvery { plantDao.insertAll(any()) } just Runs

        // When: Fetch plants
        val result = repository.getPlantsByUser("user1")

        // Then: Return remote data and save to local DB
        assertEquals(result.size, 2)
        coVerify { supabaseApi.getPlantsByUser("user1") }
        coVerify { plantDao.insertAll(any()) }
    }
}