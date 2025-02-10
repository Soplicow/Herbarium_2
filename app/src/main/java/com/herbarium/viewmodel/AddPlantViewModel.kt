package com.herbarium.viewmodel

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.herbarium.data.model.Plant
import com.herbarium.data.remote.api.PlantRepository
import com.herbarium.util.ImageUtil.uriToByteArray
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddPlantViewModel @Inject constructor(
    private val plantRepository: PlantRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading : Flow<Boolean> = _isLoading

    private val _showSuccessMessage = MutableStateFlow(false)
    val showSuccessMessage: Flow<Boolean> = _showSuccessMessage

    fun onCreatePlant(name: String, photo: Uri?, description: String, latitude: String, longitude: String) {
        viewModelScope.launch {
            _isLoading.value = true

            val imageData = photo?.let {
                uriToByteArray(
                    context = context,
                    uri = photo,
                    maxSizeKB = 512
                )
            }
            val location = createLocation(latitude, longitude)

            val plant = Plant(
                userId = plantRepository.getUserId(),
                id = UUID.randomUUID().toString(),
                name = name,
                photo = imageData,
                description = description,
                location = location
            )
            plantRepository.insertPlant(plant)

            _isLoading.value = false
            _showSuccessMessage.emit(true)
        }
    }

    private fun createLocation(latitude: String, longitude: String): JsonObject {
        return buildJsonObject {
            put("latitude", latitude)
            put("longitude", longitude)
        }
    }
}