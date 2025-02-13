package com.herbarium.viewmodel

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.herbarium.data.model.Plant
import com.herbarium.data.remote.api.PlantRepository
import com.herbarium.util.ImageUtil.uriToByteArray
import com.herbarium.util.LocationClient
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
    private val locationClient: LocationClient,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _plantName = MutableStateFlow("")
    val plantName: Flow<String> = _plantName
    fun onPlantNameChange(name: String) {
        _plantName.value = name
    }

    private val _plantPhoto = MutableStateFlow<Uri?>(null)
    val plantPhoto: Flow<Uri?> = _plantPhoto
    fun onPlantPhotoChange(photo: Uri?) {
        _plantPhoto.value = photo
    }

    private val _plantDescription = MutableStateFlow("")
    val plantDescription: Flow<String> = _plantDescription
    fun onPlantDescriptionChange(description: String) {
        _plantDescription.value = description
    }

    private val _latitude = MutableStateFlow("")
    val latitude: Flow<String> = _latitude
    fun onLatitudeChange(latitude: String) {
        _latitude.value = latitude
    }

    private val _longitude = MutableStateFlow("")
    val longitude: Flow<String> = _longitude
    fun onLongitudeChange(longitude: String) {
        _longitude.value = longitude
    }

    private val _isLoading = MutableStateFlow(false)
    val isLoading : Flow<Boolean> = _isLoading

    private val _showSuccessMessage = MutableStateFlow(false)
    val showSuccessMessage: Flow<Boolean> = _showSuccessMessage

    fun onCreatePlant(name: String, photo: Uri?, description: String, latitude: String, longitude: String) {
        viewModelScope.launch {
            _isLoading.value = true

            val imageData =
                _plantPhoto.value?.let {
                    uriToByteArray(
                        context = context,
                        uri = it,
                        maxSizeKB = 512
                    )
                }

            val location = createLocation(
                latitude = _latitude.value,
                longitude = _longitude.value
            )

            val plant = Plant(
                userId = plantRepository.getUserId(),
                id = UUID.randomUUID().toString(),
                name = _plantName.value,
                photo = imageData,
                description = _plantDescription.value,
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

    fun getCurrentLocation() {
        viewModelScope.launch {
            try {
                val location = locationClient.getCurrentLocation()
                location?.let {
                    _latitude.emit("%.6f".format(it.latitude))
                    _longitude.emit("%.6f".format(it.longitude))
                    println(_latitude.value + " " + _longitude.value)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}