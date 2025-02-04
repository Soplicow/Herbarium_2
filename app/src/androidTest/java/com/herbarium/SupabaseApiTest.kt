package com.herbarium

import com.herbarium.data.remote.api.SupabaseApi
import com.herbarium.data.remote.dto.PlantDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.PostgrestQueryBuilder
import io.mockk.just
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.runs
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SupabaseApiTest {

    private lateinit var supabaseClient: SupabaseClient
    private lateinit var postgrest: Postgrest
    private lateinit var supabaseApi: SupabaseApi
    private lateinit var queryBuilder: PostgrestQueryBuilder

    @Before
    fun setup() {
        supabaseClient = mockk<SupabaseClient>(relaxed = true)
        postgrest = mockk<Postgrest>(relaxed = true)
        queryBuilder = mockk(relaxed = true)
        every {
            supabaseClient.postgrest
        } returns postgrest
        every {
            postgrest.from("plants")
        } returns queryBuilder
        supabaseApi = SupabaseApi(supabaseClient)
    }

    @Test
    fun getPlantsByUser() = runTest {
        // Given: Mock Supabase response
        val mockPlants = listOf(
            PlantDto("1", "user1", "url1", "Plant 1", null),
            PlantDto("2", "user1", "url2", "Plant 2", null)
        )

        coEvery {
            supabaseClient.from("plants")
                .select {
                    filter {
                        eq("user_id", "user1")
                    }
                }
                .decodeList<PlantDto>()
        } returns mockPlants

        // When: Fetch plants by user
        val plants = supabaseApi.getPlantsByUser("user1")

        // Then: Verify the response matches the mock
        assertEquals(plants, mockPlants)
    }

    @Test
    fun insertPlant() = runTest {
        // Mock the insert operation
        coEvery { queryBuilder.insert(any<PlantDto>()) } returns mockk(relaxed = true)

        // Call the method under test
        supabaseApi.insertPlant(PlantDto("1", "user1", "url", "Test plant", null))

        // Verify the insert was called
        coVerify { queryBuilder.insert(any<PlantDto>()) }
    }
}