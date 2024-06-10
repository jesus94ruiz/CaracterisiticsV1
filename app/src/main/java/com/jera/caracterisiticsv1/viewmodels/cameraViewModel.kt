package com.jera.caracterisiticsv1.viewmodels

import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.jera.caracterisiticsv1.data.ApiResponse.ApiResponse
import com.jera.caracterisiticsv1.data.GoogleApiResponse.GoogleApiResponse
import com.jera.caracterisiticsv1.navigation.AppScreens
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

    private val _model: MutableStateFlow<ResourceState<ApiResponse>> = MutableStateFlow(ResourceState.Loading())
    val model: StateFlow<ResourceState<ApiResponse>> = _model

    private val _modelPictures: MutableStateFlow<ResourceState<GoogleApiResponse>> = MutableStateFlow(ResourceState.Loading())
    val modelPictures: StateFlow<ResourceState<GoogleApiResponse>> = _modelPictures
    fun getModel(image: File){
        viewModelScope.launch (Dispatchers.IO) {
            cameraRepository.getModel(image)
                .collectLatest {
                    cameraResponse -> _model.value = cameraResponse
                    println("---------------Responseeee-------------")
                    println(cameraResponse)
                }
        }
    }

    fun takePicture(cameraController: LifecycleCameraController, executor: Executor, navController: NavHostController) {
        val file = File.createTempFile("imagentest", ".jpg")
        val outputDirectory = ImageCapture.OutputFileOptions.Builder(file).build()

        cameraController.takePicture(
            outputDirectory,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    println(outputFileResults.savedUri.toString())
                    navController.navigate(AppScreens.AnalysingScreen.route)
                    getModel(file)
                    getModelPictures("lamborghini Gallardo")
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
                        cameraResponse -> _modelPictures.value = cameraResponse
                    println("---------------GoogleResponseeee-------------")
                    println(cameraResponse)
                }
        }
    }

}