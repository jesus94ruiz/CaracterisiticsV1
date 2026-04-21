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
import com.jera.caracterisiticsv1.BuildConfig
import com.jera.caracterisiticsv1.data.ApiResponse.Meta
import com.jera.caracterisiticsv1.data.ApiResponse.Parameters
import com.jera.caracterisiticsv1.data.ApiResponse.GoogleApiResponse.GoogleApiResponse
import com.jera.caracterisiticsv1.data.ApiResponse.GoogleApiResponse.Queries
import com.jera.caracterisiticsv1.data.ApiResponse.GoogleApiResponse.SearchInformation
import com.jera.caracterisiticsv1.data.ApiResponse.GoogleApiResponse.Url
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
import android.os.Build
import androidx.compose.ui.platform.LocalContext
import com.jera.caracterisiticsv1.data.modelDetected.toDatabase
import com.jera.caracterisiticsv1.repository.DatabaseRepository

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val cameraRepository: CameraRepository,
    private val databaseRepository: DatabaseRepository
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
            cameraRepository.getModel(image).collectLatest {
                    cameraResponse -> _model.value = cameraResponse
                    if (cameraResponse is ResourceState.Success) {
                        println(cameraResponse)
                        detections = cameraResponse.data.detections
                        if (detections.isNotEmpty())
                            if (detections[0].mmg.isEmpty()) {
                                _model.value = ResourceState.Error(detections[0].status.message)
                                _modelPictures.value = ResourceState.Error(detections[0].status.message)
                                _modelsDetected.value = ResourceState.Error(detections[0].status.message)
                            } else {
                                detections.forEach {
                                        detection -> detection.mmg.forEach { mmg ->
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
                            _modelPictures.value = ResourceState.Error("No se ha detectado ningún modelo")
                            _modelsDetected.value = ResourceState.Error("No se ha detectado ningún modelo")
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

    private fun pickFirstUniqueThumbnailUrl(
        urlsAlreadyPicked: Set<String>,
        results: List<com.jera.caracterisiticsv1.data.ApiResponse.BraveApiResponse.BraveImageResult>
    ): String? {
        return results
            .asSequence()
            .mapNotNull { it.thumbnail?.src }
            .map { it.trim() }
            .firstOrNull { it.isNotBlank() && !urlsAlreadyPicked.contains(it) }
    }

    fun getModelPictures(model: ModelDetected) {
        println("--------GETMODELPICTURES-----------")

        val baseQuery = "${model.make_name} ${model.model_name} ${model.years}".trim()
        if (baseQuery.isBlank()) {
            _modelsDetected.value = ResourceState.Error("No se pudo construir la query del modelo")
            return
        }

        if (BuildConfig.BRAVE_API_KEY.isBlank()) {
            _modelPictures.value = ResourceState.Error(
                "BRAVE_API_KEY no configurada. Añade tu token en app/build.gradle (buildConfigField BRAVE_API_KEY)."
            )
            _modelsDetected.value = ResourceState.Error(
                "BRAVE_API_KEY no configurada.\nEl modelo detectado ha sido:\n$baseQuery"
            )
            return
        }

        // 5 vistas: 3/4 front, top, side, rear, interior
        // Usamos 1 query por vista y nos quedamos con 1 imagen por cada una (primer resultado con thumbnail.src).
        // Añadimos “16:9” a la query para favorecer resultados panorámicos que encajen con la Card 300x169.
        // Brave no expone un filtro oficial de aspect ratio en este endpoint, así que esto es un sesgo vía texto.
        val viewQueries: List<Pair<String, String>> = listOf(
            "3/4 front view" to "$baseQuery 3/4 front view 16:9 wallpaper",
            "top view" to "$baseQuery top view 16:9 wallpaper",
            "side view" to "$baseQuery side view 16:9 wallpaper",
            "rear view" to "$baseQuery rear view 16:9 wallpaper",
            "interior" to "$baseQuery interior 16:9 wallpaper"
        )

        viewModelScope.launch(Dispatchers.IO) {
            try {
                model.searchedImages.clear()

                // Para evitar imágenes repetidas entre vistas, mantenemos un set de URLs ya elegidas.
                val pickedUrls = linkedSetOf<String>()

                for ((label, q) in viewQueries) {
                    // Log/print explícito de la query para depuración
                    Log.d("CameraViewModel", "Brave query [$label]: $q")
                    println("Brave query [$label]: $q")

                    // Intentamos hasta 3 veces (mismo query) si el primer resultado repite URL.
                    // Cada intento incrementa el count para aumentar variedad de resultados.
                    var chosenUrl: String? = null
                    val maxAttempts = 3

                    for (attempt in 0 until maxAttempts) {
                        val count = 5 + attempt * 5

                        cameraRepository.getModelPicturesBrave(q, count).collectLatest { braveResponse ->
                            if (braveResponse is ResourceState.Success) {
                                val responseData = braveResponse.data
                                chosenUrl = pickFirstUniqueThumbnailUrl(pickedUrls, responseData.results)
                            } else if (braveResponse is ResourceState.Error) {
                                Log.e("CameraViewModel", "Brave error [$label]: ${braveResponse.error}")
                            }
                        }

                        if (!chosenUrl.isNullOrBlank()) break
                    }

                    val finalUrl = chosenUrl?.takeIf { it.isNotBlank() } ?: ""
                    model.searchedImages.add(finalUrl)
                    if (finalUrl.isNotBlank()) pickedUrls.add(finalUrl)
                }

                models.add(model)
                orderByProbability(models)
                _modelsDetected.value = ResourceState.Success(models)

                // Para no tocar el resto de UI (que observa modelPictures), dejamos este state en NotInitialized.
                _modelPictures.value = ResourceState.NotInitialized()

                println(models)
            } catch (e: Exception) {
                Log.e("CameraViewModel", "Error building Brave view images", e)
                _modelsDetected.value = ResourceState.Error(
                    "Error buscando imágenes: ${e.message}\nEl modelo detectado ha sido:\n$baseQuery"
                )
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
        val length = 12
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
    ): Boolean {
        println("PERMISOS")

        //Permisos Android 14
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED
            ) {

            permissionsLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                )
            )} else{
                return true
            }
        // Permisos Android anteriores
        } else if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            println("Permisos No Concedidos")
            // Permisos no concedidos, solicitarlos
            permissionsLauncher.launch(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_AUDIO,
                )
            )
        } else {
            println("Permisos Concedidos")
            // Permisos ya concedidos, actualizar el estado
            permissionsLauncher.launch(emptyArray())
            return true;
        }
        println("Return false")
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
