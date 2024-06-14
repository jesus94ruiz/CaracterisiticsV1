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
) : ViewModel(){

    var _model: MutableStateFlow<ResourceState<ApiResponse>> = MutableStateFlow(ResourceState.Loading())
    var model: StateFlow<ResourceState<ApiResponse>> = _model

    var _modelPictures: MutableStateFlow<ResourceState<GoogleApiResponse>> = MutableStateFlow(ResourceState.Loading())
    var modelPictures: MutableStateFlow<ResourceState<GoogleApiResponse>> = _modelPictures

    var detections: List<Detection> = emptyList()

    fun getModel(image: File){
        viewModelScope.launch (Dispatchers.IO) {
            cameraRepository.getModel(image)
                .collectLatest {
                    cameraResponse -> _model.value = cameraResponse
                    if (cameraResponse is ResourceState.Success) {
                        println(cameraResponse)
                        detections = cameraResponse.data.detections
                        if(detections.isNotEmpty())
                            detections.forEach{ detection -> detection.mmg.forEach{ mmg ->  getModelPictures(mmg.make_name + " " + mmg.model_name + " " + mmg.years) } }
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

    fun getModelPictures(query:String){
        println("--------GETMODELPICTURES-----------")
        viewModelScope.launch (Dispatchers.IO) {
            cameraRepository.getModelPictures(query)
                .collectLatest {
                        googleApiResponse -> _modelPictures.value = googleApiResponse
                    if (googleApiResponse is ResourceState.Success) {
                        val responseData = googleApiResponse.data
                        Log.d("CameraViewModel", "Response: ${responseData}")
                    } else if (googleApiResponse is ResourceState.Error) {
                        Log.e("CameraViewModel", "Error: ${googleApiResponse.error}")
                    }
                }
        }
    }
}
