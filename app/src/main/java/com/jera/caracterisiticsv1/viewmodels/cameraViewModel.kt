package com.jera.caracterisiticsv1.viewmodels

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jera.caracterisiticsv1.data.ApiResponse.ApiResponse
import com.jera.caracterisiticsv1.data.ApiResponse.Detection
import com.jera.caracterisiticsv1.data.ApiResponse.Meta
import com.jera.caracterisiticsv1.data.ApiResponse.Parameters
import com.jera.caracterisiticsv1.data.GoogleApiResponse.GoogleApiResponse
import com.jera.caracterisiticsv1.data.GoogleApiResponse.Queries
import com.jera.caracterisiticsv1.data.GoogleApiResponse.SearchInformation
import com.jera.caracterisiticsv1.data.GoogleApiResponse.Url
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
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import java.util.concurrent.Executor
import javax.inject.Inject
import android.content.pm.PackageManager
import androidx.compose.ui.platform.LocalContext
import com.jera.caracterisiticsv1.data.modelDetected.toDatabase
import com.jera.caracterisiticsv1.repository.databaseRepository

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val cameraRepository: CameraRepository,
    private val databaseRepository: databaseRepository
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

    var pageCount: Int = 0;


    // TEST //
/*val modelObject = ModelDetected( "Ferrari","F50", "1995-1997", 0.9653,
    mutableListOf(
        "https://upload.wikimedia.org/wikipedia/commons/a/a5/1999_Ferrari_F50.jpg",
        "https://s1.cdn.autoevolution.com/images/gallery/FERRARI-F50-1067_49.jpg",
        "https://static.wikia.nocookie.net/hypercarss/images/9/9b/5402EC17-CBF7-4EBE-8F40-1D3A148C3464.jpeg/revision/latest?cb=20180411122943",
        "https://static0.topspeedimages.com/wordpress/wp-content/uploads/jpg/201309/ferrari-f50.jpg",
        "https://www.ultimatecarpage.com/images/car/186/Ferrari-F50-67449.jpg"),
    )*/


/*fun getModel(image: File) {
        // Success //
    _model.value = ResourceState.Success(ApiResponse(
        mutableListOf()
        ,true, meta = Meta(5,"md5",
        Parameters(0.25,180,180,181, "center", emptyList(), emptyList()), 14.5)))
    _modelPictures.value = ResourceState.Success(GoogleApiResponse(com.jera.caracterisiticsv1.data.GoogleApiResponse.Context("Context"), emptyList(), "kind",
        Queries(emptyList(), emptyList()),
        SearchInformation("format","Formal",14.2, "0"),
        Url("ey", "type")

    ))
    val model = ModelDetected( "Ferrari","F50", "1995-1997", 0.9653,
        mutableListOf(
            "https://upload.wikimedia.org/wikipedia/commons/a/a5/1999_Ferrari_F50.jpg",
            "https://s1.cdn.autoevolution.com/images/gallery/FERRARI-F50-1067_49.jpg",
            "https://static.wikia.nocookie.net/hypercarss/images/9/9b/5402EC17-CBF7-4EBE-8F40-1D3A148C3464.jpeg/revision/latest?cb=20180411122943",
            "https://static0.topspeedimages.com/wordpress/wp-content/uploads/jpg/201309/ferrari-f50.jpg",
            "https://www.ultimatecarpage.com/images/car/186/Ferrari-F50-67449.jpg"),
        image
    )
    _modelsDetected.value = ResourceState.Success(mutableListOf(model))
}*/


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
                                            mutableListOf<String>(),
                                            file = image
                                        )
                                        getModelPictures(model)
                                        pageCount++;
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
        val query = "wallpaper" + " " + "${model.make_name}" + " " + "${model.model_name}" + " " + "${model.years}"
        println(query)
        viewModelScope.launch(Dispatchers.IO) {
            cameraRepository.getModelPictures(query)
                .collectLatest { googleApiResponse ->
                    if (googleApiResponse is ResourceState.Success) {
                        val responseData = googleApiResponse.data
                        Log.d("CameraViewModel", "Response: ${responseData}")
                        responseData.items.forEach { item ->
                            model.searchedImages.add(item.link)
                        }
                        models.add(model)
                        orderByProbability(models)
                        _modelsDetected.value = ResourceState.Success(models)
                        _modelPictures.value = googleApiResponse
                        println(models)
                    } else if (googleApiResponse is ResourceState.Error) {
                        Log.e("CameraViewModel", "Error: ${googleApiResponse.error}")
                        _modelPictures.value = ResourceState.Error("${googleApiResponse.error}")
                        _modelsDetected.value = ResourceState.Error("${googleApiResponse.error}\n"  + "El modelo detectado ha sido: \n" + "${model.make_name}" + " " + "${model.model_name}" + " " + "${model.years}" )
                    }
                }
        }
    }

    fun orderByProbability(models: MutableList<ModelDetected>) {
        models.sortByDescending { it.probability }
    }

    fun getModelfromGallery(context:Context , uri: Uri){
        val fileName: String = generateRandomFileName()
        val file = createFileFromUri(context, uri, fileName)
        getModel(file)
    }

    fun createFileFromUri(context: Context, uri: Uri, fileName: String): File {
        val contentResolver = context.contentResolver
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val file = File(context.filesDir, fileName)

        if (inputStream != null) {
            writeInputStreamToFile(inputStream, file)
        }
        return file
    }

    fun writeInputStreamToFile(inputStream: InputStream, file: File) {
        var outputStream: OutputStream? = null
        try {
            outputStream = FileOutputStream(file)
            val buffer = ByteArray(4 * 1024) // Buffer de 4KB
            var read: Int
            while (inputStream.read(buffer).also { read = it } != -1) {
                outputStream.write(buffer, 0, read)
            }
            outputStream.flush()
        } finally {
            inputStream.close()
            outputStream?.close()
        }
    }

    fun generateRandomFileName(): String {
        val characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val length = 12 // Puedes ajustar la longitud del nombre del archivo aquí
        val random = Random()
        val fileName = StringBuilder(length)
        val extension: String = "jpg"

        for (i in 0 until length) {
            val index = random.nextInt(characters.length)
            fileName.append(characters[index])
        }

        return "$fileName.$extension"
    }

    fun resetResourceStates() {
        _model.value = ResourceState.NotInitialized()
        _modelPictures.value = ResourceState.NotInitialized()
        _modelsDetected.value = ResourceState.NotInitialized()
    }

    fun saveFileToStorage(image: File, context: Context) {
        println("imageName: ${image.name}")
        try {
            val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            if (!storageDir!!.exists()) {
                storageDir.mkdirs()
            }

            val file = File(storageDir, image.name)
            val inputStream: InputStream = image.inputStream()
            val outputStream: FileOutputStream = FileOutputStream(file)

            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            println("File saved to ${file.absolutePath}")
        } catch (e: Exception) {
            e.printStackTrace()
            println("Failed to save file: ${e.message}")
        }
    }

    fun checkAndRequestPermissions(
        context: Context,
        permissionsLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>
    ):Boolean{
        println("PERMISOS")
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Permisos no concedidos, solicitarlos
            permissionsLauncher.launch(arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ))
        } else {
            // Permisos ya concedidos, actualizar el estado
            permissionsLauncher.launch(emptyArray())
            return true;
        }
        return false
    }

    fun insertModel(model: ModelDetected) {
        viewModelScope.launch { databaseRepository.insertModel(model.toDatabase()) }
    }

}
// TEST //
/*val modelObject = ModelDetected( "Ferrari","F50", "1995-1997", 0.9653,
    mutableListOf(
        "https://upload.wikimedia.org/wikipedia/commons/a/a5/1999_Ferrari_F50.jpg",
        "https://s1.cdn.autoevolution.com/images/gallery/FERRARI-F50-1067_49.jpg",
        "https://static.wikia.nocookie.net/hypercarss/images/9/9b/5402EC17-CBF7-4EBE-8F40-1D3A148C3464.jpeg/revision/latest?cb=20180411122943",
        "https://static0.topspeedimages.com/wordpress/wp-content/uploads/jpg/201309/ferrari-f50.jpg",
        "https://www.ultimatecarpage.com/images/car/186/Ferrari-F50-67449.jpg"))


fun getModel(image: File) {
        // Success //
    _model.value = ResourceState.Success(ApiResponse(emptyList(),true, meta = Meta(5,"md5",
        Parameters(0.25,180,180,181, "center", emptyList(), emptyList()), 14.5)))
    _modelPictures.value = ResourceState.Success(GoogleApiResponse(com.jera.caracterisiticsv1.data.GoogleApiResponse.Context("Context"), emptyList(), "kind",
        Queries(emptyList(), emptyList()),
        SearchInformation("format","Formal",14.2, "0"),
        Url("ey", "type")

        // Errores //
        _model.value = ResourceState.Error("Error test")
        _modelPictures.value = ResourceState.Error("Error test")
        _modelsDetected.value = ResourceState.Error("Error test")
    ))
}*/
