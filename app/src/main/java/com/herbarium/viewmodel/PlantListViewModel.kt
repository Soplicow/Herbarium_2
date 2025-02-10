package com.herbarium.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.herbarium.auth.data.source.AuthRepository
import com.herbarium.data.model.Plant
import com.herbarium.data.remote.api.PlantRepository
import com.herbarium.data.remote.dto.PlantDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlantListViewModel @Inject constructor(
    private val plantRepository: PlantRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _plantList = MutableStateFlow<List<Plant>?>(listOf())
    val plantList: Flow<List<Plant>?> = _plantList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: Flow<Boolean> = _isLoading

    init {
        getPlants()
    }

    fun getPlants() {
        viewModelScope.launch {
            _isLoading.emit(true)

            val user = authRepository.getCurrentUserUid()
            val plants = plantRepository.getDomainPlants(user)
            _plantList.emit(plants)

            _isLoading.emit(false)
        }
    }

    fun removePlant(plant: Plant) {
        viewModelScope.launch {
            val newList = mutableListOf<Plant>().apply { _plantList.value?.let { addAll(it) } }
            newList.remove(plant)
            _plantList.emit(newList.toList())

            plantRepository.deletePlant(plantId = plant.id)

            getPlants()
        }
    }
}