package com.jera.caracterisiticsv1.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jera.caracterisiticsv1.data.domain.model.CarModel
import com.jera.caracterisiticsv1.repository.CameraRepository
import com.jera.caracterisiticsv1.repository.DatabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GarageViewModel @Inject constructor(
    private val cameraRepository: CameraRepository,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    var carModels: List<CarModel> = emptyList()

    private val _models = MutableStateFlow<List<CarModel>>(emptyList())
    val models: StateFlow<List<CarModel>> = _models

    init{
        getAllCarModels()
    }

    fun getAllCarModels(){
        viewModelScope.launch(){
            _models.value = databaseRepository.getModelsFromDatabase()
            println(_models)
            println(_models.value)
        }
    }

}