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
import com.jera.caracterisiticsv1.data.ApiResponse.CarImagesApiResponse.CarImagesApiResponse
import com.jera.caracterisiticsv1.data.modelDetected.ModelDetected
import com.jera.caracterisiticsv1.repository.CameraRepository
import com.jera.caracterisiticsv1.repository.MissionRepository
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
import com.jera.caracterisiticsv1.data.AppModule
import com.jera.caracterisiticsv1.data.ApplicationScope
import com.jera.caracterisiticsv1.data.modelDetected.toDatabase
import com.jera.caracterisiticsv1.repository.CarSpecsRepository
import com.jera.caracterisiticsv1.repository.DatabaseRepository
import com.jera.caracterisiticsv1.repository.LocationRepository
import com.jera.caracterisiticsv1.repository.UserRepository
import com.jera.caracterisiticsv1.data.database.entities.AchievementEntity
import kotlinx.coroutines.CoroutineScope

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val cameraRepository: CameraRepository,
    private val databaseRepository: DatabaseRepository,
    private val locationRepository: LocationRepository,
    private val carSpecsRepository: CarSpecsRepository,
    private val userRepository: UserRepository,
    private val missionRepository: MissionRepository,
    @ApplicationScope private val applicationScope: CoroutineScope
) : ViewModel() {

    private var currentLatitude: Double? = null
    private var currentLongitude: Double? = null
    private var currentTimestamp: Long? = null

    val _model: MutableStateFlow<ResourceState<ApiResponse>> =
        MutableStateFlow(ResourceState.NotInitialized())
    val model: StateFlow<ResourceState<ApiResponse>> = _model

    val _modelPictures: MutableStateFlow<ResourceState<CarImagesApiResponse>> =
        MutableStateFlow(ResourceState.NotInitialized())
    val modelPictures: MutableStateFlow<ResourceState<CarImagesApiResponse>> = _modelPictures

    var detections: List<Detection> = emptyList()

    val _modelsDetected: MutableStateFlow<ResourceState<MutableList<ModelDetected>>> =
        MutableStateFlow(ResourceState.Loading())
    val modelsDetected: StateFlow<ResourceState<MutableList<ModelDetected>>> = _modelsDetected

    // ── XP / Level Up ─────────────────────────────────────────────────────────
    private val _levelUpEvent = MutableStateFlow<Int?>(null)
    val levelUpEvent: StateFlow<Int?> = _levelUpEvent

    private val _newAchievements = MutableStateFlow<List<AchievementEntity>>(emptyList())
    val newAchievements: StateFlow<List<AchievementEntity>> = _newAchievements

    private val _xpGainedEvent = MutableStateFlow<Int?>(null)
    val xpGainedEvent: StateFlow<Int?> = _xpGainedEvent

    // Misiones completadas tras la captura (título + XP)
    private val _missionsCompletedEvent = MutableStateFlow<List<Pair<String, Int>>>(emptyList())
    val missionsCompletedEvent: StateFlow<List<Pair<String, Int>>> = _missionsCompletedEvent

    val models: MutableList<ModelDetected> = mutableListOf()

    var pageCount: Int = 0

    fun getModel(image: File) {
        viewModelScope.launch(Dispatchers.IO) {
            cameraRepository.getModel(image).collectLatest { cameraResponse ->
                _model.value = cameraResponse
                if (cameraResponse is ResourceState.Success) {
                    println(cameraResponse)
                    detections = cameraResponse.data.detections
                    if (detections.isNotEmpty())
                        if (detections[0].mmg.isEmpty()) {
                            _model.value = ResourceState.Error(detections[0].status.message)
                            _modelPictures.value = ResourceState.Error(detections[0].status.message)
                            _modelsDetected.value = ResourceState.Error(detections[0].status.message)
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
                                    pageCount++
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
        viewModelScope.launch {
            val location = locationRepository.getCurrentLocation()
                ?: locationRepository.getLastKnownLocation()

            currentLatitude = location?.latitude
            currentLongitude = location?.longitude
            currentTimestamp = System.currentTimeMillis()

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
    }

    fun getModelPictures(model: ModelDetected) {
        println("--------GETMODELPICTURES-----------")

        val make = model.make_name.trim()
        val modelName = model.model_name.trim()

        if (make.isBlank() || modelName.isBlank()) {
            _modelsDetected.value = ResourceState.Error("No se pudo construir la query del modelo")
            return
        }

        // Extraemos el primer año del rango si existe (ej: "2010-2015" -> "2010")
        val year = model.years
            ?.split("-")
            ?.firstOrNull()
            ?.trim()
            ?.takeIf { it.matches(Regex("\\d{4}")) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                cameraRepository.getCarImages(make, modelName, year).collectLatest { imagesResponse ->
                    when (imagesResponse) {
                        is ResourceState.Success -> {
                            val url = imagesResponse.data.url
                            model.searchedImages.clear()
                            if (url.isNotBlank()) model.searchedImages.add(url)

                            models.add(model)
                            orderByProbability(models)
                            _modelsDetected.value = ResourceState.Success(models)
                            _modelPictures.value = ResourceState.Success(imagesResponse.data)

                            Log.d("CameraViewModel", "CarImages success: url=$url for $make $modelName")
                            println(models)
                        }
                        is ResourceState.Error -> {
                            Log.e("CameraViewModel", "CarImages error: ${imagesResponse.error}")
                            // Añadimos el modelo sin imágenes para que aparezca igualmente
                            models.add(model)
                            orderByProbability(models)
                            _modelsDetected.value = ResourceState.Success(models)
                        }
                        else -> {}
                    }
                }
            } catch (e: Exception) {
                Log.e("CameraViewModel", "Error fetching car images", e)
                _modelsDetected.value = ResourceState.Error(
                    "Error buscando imágenes: ${e.message}\nModelo detectado: $make $modelName"
                )
            }
        }
    }

    fun orderByProbability(models: MutableList<ModelDetected>) {
        models.sortByDescending { it.probability }
    }

    fun getModelfromGallery(context: Context, uri: Uri) {
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
            val buffer = ByteArray(4 * 1024)
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
        val extension = "jpg"

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsLauncher.launch(
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
                )
            } else {
                return true
            }
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
            permissionsLauncher.launch(emptyArray())
            return true
        }
        println("Return false")
        return false
    }

    fun insertModel(model: ModelDetected) {
        // Usamos applicationScope para que las llamadas de red no se cancelen
        // si el ViewModel es destruido antes de que terminen
        applicationScope.launch {
            var insertedId: Long = -1L
            try {
                val modelEntity = model.toDatabase()

                val entityToInsert = if (currentLatitude != null && currentLongitude != null) {
                    modelEntity.copy(
                        latitude = currentLatitude,
                        longitude = currentLongitude,
                        captureTimestamp = currentTimestamp
                    )
                } else {
                    modelEntity
                }

                insertedId = databaseRepository.insertModel(entityToInsert)

                // ── XP: determinar si es primera captura de este modelo ────────────
                val allModels = databaseRepository.getModelEntitiesFromDatabase()
                val sameModelCount = allModels.count {
                    it.make_name.equals(model.make_name, ignoreCase = true) &&
                    it.model_name.equals(model.model_name, ignoreCase = true)
                }
                val isFirstCapture = sameModelCount <= 1   // el recién insertado ya cuenta

                val carsOfSameBrand = allModels.count {
                    it.make_name.equals(model.make_name, ignoreCase = true)
                }

                val result = userRepository.addXpForCapture(
                    probability = model.probability ?: 0.5,
                    isFirstCapture = isFirstCapture,
                    brandName = model.make_name,
                    carsOfSameBrand = carsOfSameBrand
                )
                if (result.newAchievements.isNotEmpty()) {
                    _newAchievements.value = result.newAchievements
                }
                if (result.leveledUp) {
                    _levelUpEvent.value = result.newLevel
                }
                // Señalizar XP al final (siempre, incluso si es 0)
                _xpGainedEvent.value = result.xpGained

            } catch (e: Exception) {
                Log.e("CameraViewModel", "Error en insertModel", e)
                // Siempre señalizamos para desbloquear la UI (con 0 XP en caso de error)
                _xpGainedEvent.value = 0
            }

            // Obtener specs de CarSpecsAPI y persistirlas (no bloquea la navegación)
            if (insertedId > 0) {
                val year = model.years
                    ?.split("-")
                    ?.firstOrNull()
                    ?.trim()
                    ?.toIntOrNull()

                if (year != null) {
                    try {
                        val specs = carSpecsRepository.fetchCarSpecs(
                            makeName = model.make_name.trim(),
                            modelName = model.model_name.trim(),
                            year = year
                        )
                if (specs != null) {
                            databaseRepository.updateModelSpecs(insertedId.toInt(), specs)
                            Log.d("CameraViewModel", "Specs guardadas para id=$insertedId")
                        } else {
                            Log.w("CameraViewModel", "No se encontraron specs para ${model.make_name} ${model.model_name} $year")
                        }
                    } catch (e: Exception) {
                        Log.e("CameraViewModel", "Error obteniendo specs", e)
                    }
                }

                // Evaluar misiones con la entidad más actualizada (specs incluidas si las hay)
                val entityForMissions = databaseRepository.getModelById(insertedId.toInt())
                if (entityForMissions != null) {
                    evaluateMissions(entityForMissions)
                }
            }
        }
    }

    private suspend fun evaluateMissions(entity: com.jera.caracterisiticsv1.data.database.entities.ModelEntity) {
        try {
            val completed = missionRepository.onCarCaptured(entity)
            if (completed.isNotEmpty()) {
                // Otorgar XP por cada misión completada
                var totalMissionXp = 0
                completed.forEach { mission ->
                    userRepository.addXpDirect(mission.xpReward)
                    totalMissionXp += mission.xpReward
                }
                // Notificar a la UI (título + XP de cada misión)
                _missionsCompletedEvent.value = completed.map { it.title to it.xpReward }
                // Persistir en el repo (singleton) para que CaptureRewardScreen las lea
                missionRepository.setPendingCompleted(completed)
                Log.d("CameraViewModel", "Misiones completadas: ${completed.size}, XP total misiones: $totalMissionXp")
            }
        } catch (e: Exception) {
            Log.e("CameraViewModel", "Error evaluando misiones", e)
        }
    }

    fun consumeLevelUpEvent() { _levelUpEvent.value = null }
    fun consumeXpGainedEvent() { _xpGainedEvent.value = null }
    fun consumeAchievementsEvent() { _newAchievements.value = emptyList() }
    fun consumeMissionsCompletedEvent() { _missionsCompletedEvent.value = emptyList() }
}
