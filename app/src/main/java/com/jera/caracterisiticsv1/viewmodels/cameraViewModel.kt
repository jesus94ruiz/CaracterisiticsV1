package com.jera.caracterisiticsv1.viewmodels

import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jera.caracterisiticsv1.data.ApiResponse.ApiResponse
import com.jera.caracterisiticsv1.data.ApiResponse.Detection
import com.jera.caracterisiticsv1.data.GoogleApiResponse.GoogleApiResponse
import com.jera.caracterisiticsv1.data.modelDetected.ModelDetected
import com.jera.caracterisiticsv1.repository.CameraRepository
import com.jera.caracterisiticsv1.utilities.ResourceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.Executor
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val cameraRepository: CameraRepository
) : ViewModel() {

    val _model: MutableStateFlow<ResourceState<ApiResponse>> =
        MutableStateFlow(ResourceState.NotInitialized())
    val model: StateFlow<ResourceState<ApiResponse>> = _model

    val _modelPictures: MutableStateFlow<ResourceState<GoogleApiResponse>> =
        MutableStateFlow(ResourceState.NotInitialized())
    val modelPictures: MutableStateFlow<ResourceState<GoogleApiResponse>> = _modelPictures

    var detections: List<Detection> = emptyList()
    val _modelsDetected: MutableStateFlow<ResourceState<MutableList<ModelDetected>>> =
        MutableStateFlow(ResourceState.Loading())
    val modelsDetected: StateFlow<ResourceState<MutableList<ModelDetected>>> = _modelsDetected
    val models: MutableList<ModelDetected> = mutableListOf()

    fun getModel(image: File) {
        viewModelScope.launch(Dispatchers.IO) {
            cameraRepository.getModel(image)
                .collectLatest { cameraResponse ->
                    _model.value = cameraResponse
                    if (cameraResponse is ResourceState.Success) {
                        println(cameraResponse)
                        detections = cameraResponse.data.detections
                        if (detections.isNotEmpty())
                            if (detections[0].mmg.isEmpty()) {
                                _model.value = ResourceState.Error(detections[0].status.message)
                                _modelPictures.value =
                                    ResourceState.Error(detections[0].status.message)
                                _modelsDetected.value =
                                    ResourceState.Error(detections[0].status.message)
                            } else {
                                detections.forEach { detection ->
                                    detection.mmg.forEach { mmg ->
                                        val model = ModelDetected(
                                            mmg.make_name,
                                            mmg.model_name,
                                            mmg.years,
                                            mmg.probability,
                                            mutableListOf<String>()
                                        )
                                        getModelPictures(model)
                                    }
                                }
                            }
                        if (detections.isEmpty()) {
                            _model.value = ResourceState.Error("No se ha detectado ningún modelo")
                            _modelPictures.value =
                                ResourceState.Error("No se ha detectado ningún modelo")
                            _modelsDetected.value =
                                ResourceState.Error("No se ha detectado ningún modelo")
                        }
                    }
                }
        }
    }


    fun takePicture(cameraController: LifecycleCameraController, executor: Executor) {
        val file = File.createTempFile("imagentest", ".jpg")
        val outputDirectory = ImageCapture.OutputFileOptions.Builder(file).build()

        cameraController.takePicture(
            outputDirectory,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    println(outputFileResults.savedUri.toString())
                    getModel(file)
                }

                override fun onError(exception: ImageCaptureException) {
                    println("Error al guardar la imagen: ${exception.message}")
                }
            },
        )
    }

    fun getModelPictures(model: ModelDetected) {
        println("--------GETMODELPICTURES-----------")
        val query = "${model.make_name}" + " " + "${model.model_name}"
        println(query)
        viewModelScope.launch(Dispatchers.IO) {
            cameraRepository.getModelPictures(query)
                .collectLatest { googleApiResponse ->
                    if (googleApiResponse is ResourceState.Success) {
                        val responseData = googleApiResponse.data
                        Log.d("CameraViewModel", "Response: ${responseData}")
                        responseData.items.forEach { item ->
                            model.searchedImages.add(item.link)
                            println("----")
                            println(model)
                            println("----")
                        }
                        models.add(model)
                        println(models)
                        println(_modelsDetected)
                        _modelsDetected.value = ResourceState.Success(models)
                        println(_modelsDetected)
                        _modelPictures.value = googleApiResponse
                    } else if (googleApiResponse is ResourceState.Error) {
                        Log.e("CameraViewModel", "Error: ${googleApiResponse.error}")
                    }
                }
        }
        println(model)
        println(modelsDetected)
        println("------------------------------")
    }

    fun resetResourceStates() {
        _model.value = ResourceState.NotInitialized()
        _modelPictures.value = ResourceState.NotInitialized()
        _modelsDetected.value = ResourceState.NotInitialized()
    }
}
