package com.jera.caracterisiticsv1.repository

import com.jera.caracterisiticsv1.data.ApiResponse.CarSpecsApiResponse.CarSpecsTrimDetail
import com.jera.caracterisiticsv1.data.database.dao.ModelEntityDao
import com.jera.caracterisiticsv1.data.database.entities.ModelEntity
import com.jera.caracterisiticsv1.data.domain.model.CarModel
import com.jera.caracterisiticsv1.data.domain.model.toDomain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseRepository @Inject constructor(
    private val modelEntityDao: ModelEntityDao,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val firestoreRepository: FirestoreRepository
) {

    private val repoScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    suspend fun getModelsFromDatabase(): List<CarModel> {
        val response: List<ModelEntity> = modelEntityDao.getAllModels()
        return response.map { it.toDomain() }
    }

    suspend fun getModelEntitiesFromDatabase(): List<ModelEntity> {
        return modelEntityDao.getAllModels()
    }

    /** Inserta un coche en Room y en Firestore/mapa global si el usuario está logueado */
    suspend fun insertModel(modelEntity: ModelEntity): Long {
        val newId = modelEntityDao.insert(modelEntity)

        val uid = authRepository.getCurrentUid()
        if (uid != null) {
            // Recuperar la entidad con el id generado para tener el id correcto
            val savedEntity = modelEntityDao.getModelById(newId.toInt()) ?: modelEntity.copy(id = newId.toInt())
            val username = userRepository.getUserProfileOnce()?.username ?: "Driver"
            val photoUrl = authRepository.currentUser.value?.photoUrl?.toString() ?: ""

            repoScope.launch {
                runCatching {
                    firestoreRepository.addCarToGarage(uid, savedEntity)
                }
                runCatching {
                    firestoreRepository.publishCarToMap(uid, username, photoUrl, savedEntity)
                }
            }
        }

        return newId
    }

    /** Devuelve una entidad por su id (con specs ya actualizadas si las hay) */
    suspend fun getModelById(id: Int): ModelEntity? {
        return modelEntityDao.getModelById(id)
    }

    /** Actualiza los campos de specs de un coche ya guardado usando los datos de CarSpecsAPI */
    suspend fun updateModelSpecs(id: Int, specs: CarSpecsTrimDetail) {
        modelEntityDao.updateSpecs(
            id = id,
            bodyType = specs.bodyType,
            seats = specs.numberOfSeats,
            engineType = specs.engineType,
            displacementCm3 = specs.engineDisplacementCm3,
            cylinders = specs.numberOfCylinders,
            powerHp = specs.powerHp,
            torqueNm = specs.torqueNm,
            fuelTankL = specs.fuelTankCapacityL,
            acceleration0100 = specs.acceleration0100KmhS,
            topSpeedKmh = specs.maximumSpeedKmh,
            consumptionMixed = specs.fuelConsumptionMixed,
            lengthMm = specs.lengthMm,
            widthMm = specs.widthMm,
            heightMm = specs.heightMm,
            wheelbaseMm = specs.wheelbaseMm,
            curbWeightKg = specs.curbWeightKg,
            gearbox = specs.gearbox,
            driveWheels = specs.driveWheels
        )
    }
}
