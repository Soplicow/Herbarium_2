package com.herbarium.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.herbarium.data.model.Plant
import com.herbarium.data.remote.api.PlantRepository
import com.herbarium.ui.navigation.PlantDetailDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.toJsonObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.buildJsonObject
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

            val result = plantRepository.getPlantById(plantId)
            _plant.emit(result)
            _name.emit(result?.name ?: "" )
            _description.emit(result?.description?: "")
            _location.emit((result?.location?: "").toString())
            _latitude.emit(result?.location?.get("latitude").toString())
            _longitude.emit(result?.location?.get("longitude").toString())
            _image.emit(result?.photo)

            _isLoading.emit(false)
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

    @OptIn(SupabaseInternal::class)
    fun onSavePlant() {
        viewModelScope.launch {
            _isLoading.emit(true)

            _plant.value?.copy(
                name = _name.value,
                description = _description.value,
                photo = _image.value,
                location = createLocation()
            )?.let {
                plantRepository.updatePlant(
                    plant = it
                )
            }

            _isLoading.emit(false)
        }
    }

    private fun createLocation(): JsonObject {
        return buildJsonObject {
            put("latitude", _latitude.value)
            put("longitude", _longitude.value)
        }
    }
}