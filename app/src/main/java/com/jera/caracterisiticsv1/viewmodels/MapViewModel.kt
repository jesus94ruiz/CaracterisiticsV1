package com.jera.caracterisiticsv1.viewmodels

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.jera.caracterisiticsv1.data.database.entities.ModelEntity
import com.jera.caracterisiticsv1.repository.DatabaseRepository
import com.jera.caracterisiticsv1.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MapUiState(
    val currentLocation: LatLng? = null,
    val isLoadingLocation: Boolean = true,
    val capturedCars: List<ModelEntity> = emptyList(),
    val hasLocationPermission: Boolean = false
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        loadCapturedCars()
    }

    fun updateLocationPermission(hasPermission: Boolean) {
        _uiState.value = _uiState.value.copy(hasLocationPermission = hasPermission)
        if (hasPermission) {
            getCurrentLocation()
        }
    }

    fun getCurrentLocation() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingLocation = true)
            
            val location = locationRepository.getCurrentLocation() 
                ?: locationRepository.getLastKnownLocation()
            
            _uiState.value = _uiState.value.copy(
                currentLocation = location?.let { LatLng(it.latitude, it.longitude) },
                isLoadingLocation = false
            )
        }
    }

    private fun loadCapturedCars() {
        viewModelScope.launch {
            val cars = databaseRepository.getModelEntitiesFromDatabase()
            _uiState.value = _uiState.value.copy(
                capturedCars = cars.filter { it.latitude != null && it.longitude != null }
            )
        }
    }

    fun getCarsWithLocation(): List<Pair<LatLng, ModelEntity>> {
        return _uiState.value.capturedCars.mapNotNull { car ->
            if (car.latitude != null && car.longitude != null) {
                LatLng(car.latitude, car.longitude) to car
            } else {
                null
            }
        }
    }
}
