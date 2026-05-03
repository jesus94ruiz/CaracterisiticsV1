package com.jera.caracterisiticsv1.repository

import com.jera.caracterisiticsv1.data.ApiResponse.CarSpecsApiResponse.CarSpecsTrimDetail
import com.jera.caracterisiticsv1.data.database.dao.ModelEntityDao
import com.jera.caracterisiticsv1.data.database.entities.ModelEntity
import com.jera.caracterisiticsv1.data.domain.model.CarModel
import com.jera.caracterisiticsv1.data.domain.model.toDomain
import javax.inject.Inject

class DatabaseRepository @Inject constructor(private val modelEntityDao: ModelEntityDao) {

    suspend fun getModelsFromDatabase(): List<CarModel> {
        val response: List<ModelEntity> = modelEntityDao.getAllModels()
        return response.map { it.toDomain() }
    }

    suspend fun getModelEntitiesFromDatabase(): List<ModelEntity> {
        return modelEntityDao.getAllModels()
    }

    /** Inserta un coche y devuelve el id generado por Room */
    suspend fun insertModel(modelEntity: ModelEntity): Long {
        return modelEntityDao.insert(modelEntity)
    }

    /** Actualiza los campos de specs de un coche ya guardado usando los datos de CarSpecsAPI */
    suspend fun updateModelSpecs(id: Int, specs: CarSpecsTrimDetail) {
        modelEntityDao.updateSpecs(
            id = id,
            bodyType = specs.bodyType,
            doors = specs.numberOfDoors,
            seats = specs.numberOfSeats,
            engineType = specs.engineType,
            displacementCm3 = specs.engineDisplacementCm3,
            fuelType = specs.fuelType,
            cylinders = specs.numberOfCylinders,
            powerHp = specs.powerHp,
            torqueNm = specs.torqueNm,
            fuelTankL = specs.fuelTankCapacityL,
            acceleration0100 = specs.acceleration0100KmhS,
            topSpeedKmh = specs.maximumSpeedKmh,
            consumptionMixed = specs.fuelConsumptionMixed,
            co2GKm = specs.co2EmissionsGKm,
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
