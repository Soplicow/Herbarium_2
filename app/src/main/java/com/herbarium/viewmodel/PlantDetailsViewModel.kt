package com.herbarium.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.herbarium.data.model.Plant
import com.herbarium.data.remote.api.PlantRepository
import com.herbarium.ui.navigation.PlantDetailDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import javax.inject.Inject

@HiltViewModel
class PlantDetailsViewModel @Inject constructor(
    private val plantRepository: PlantRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _plant = MutableStateFlow<Plant?>(null)
    val plant: Flow<Plant?> = _plant

    private val _name = MutableStateFlow("")
    val name: Flow<String> = _name

    private val _description = MutableStateFlow("")
    val description: Flow<String> = _description

    private val _location = MutableStateFlow("")
    val location: Flow<String> = _location

    private val _latitude = MutableStateFlow("")
    val latitude: Flow<String> = _latitude

    private val _longitude = MutableStateFlow("")
    val longitude: Flow<String> = _longitude

    private val _image = MutableStateFlow<ByteArray?>(null)
    val image: Flow<ByteArray?> = _image

    private val _isLoading = MutableStateFlow(false)
    val isLoading: Flow<Boolean> = _isLoading

    init {
        val plantId = savedStateHandle.get<String>(PlantDetailDestination.plantId)
        print(plantId)
        println(plantId)
        plantId?.let {
            getPlant(plantId)
        }
    }

    private fun getPlant(plantId: String) {
        viewModelScope.launch {
            _isLoading.emit(true)

            try {
                val result = plantRepository.getPlantById(plantId)

                result?.let { plant ->
                    _plant.emit(plant)

                    // Direct string values
                    _name.emit(plant.name)
                    _description.emit(plant.description ?: "")

                    // Handle JSON location
                    plant.location?.let { json ->
                        _location.emit(json.toString())

                        // Get raw values without quotes
                        val latitude = json["latitude"]?.jsonPrimitive?.content.orEmpty()
                        val longitude = json["longitude"]?.jsonPrimitive?.content.orEmpty()

                        _latitude.emit(latitude)
                        _longitude.emit(longitude)
                    } ?: run {
                        _location.emit("")
                        _latitude.emit("")
                        _longitude.emit("")
                    }

                    _image.emit(plant.photo)
                } ?: run {
                    // Handle null result
                    _plant.emit(null)
                    _name.emit("")
                    _description.emit("")
                    _location.emit("")
                    _latitude.emit("")
                    _longitude.emit("")
                    _image.emit(null)
                }
            } finally {
                _isLoading.emit(false)
            }
        }
    }

    fun onNameChange(name: String) {
        _name.value = name
    }

    fun onDescriptionChange(description: String) {
        _description.value = description
    }

    fun onImageChange(image: ByteArray) {
        _image.value = image
    }

    fun onLatitudeChange(latitude: String) {
        _latitude.value = latitude
    }

    fun onLongitudeChange(longitude: String) {
        _longitude.value = longitude
    }

    fun onLocationChange(location: String) {
        _location.value = location
    }

    fun onSavePlant() {
        viewModelScope.launch {
            try {
                _isLoading.emit(true)

                val currentPlant = _plant.value ?: run {
                    return@launch
                }

                val updatedPlant = currentPlant.copy(
                    name = _name.value,
                    description = _description.value,
                    photo = _image.value,
                    location = createLocation()
                )

                plantRepository.updatePlant(updatedPlant)
            } catch (e: Exception) {
                throw e
            } finally {
                _isLoading.emit(false)
            }
        }
    }

    private fun createLocation(): JsonObject {
        return buildJsonObject {
            put("latitude", _latitude.value)
            put("longitude", _longitude.value)
        }
    }
}