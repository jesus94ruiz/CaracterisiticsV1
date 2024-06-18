package com.jera.caracterisiticsv1.repository

import com.jera.caracterisiticsv1.data.database.dao.ModelEntityDao
import com.jera.caracterisiticsv1.data.database.entities.ModelEntity
import com.jera.caracterisiticsv1.data.domain.model.CarModel
import com.jera.caracterisiticsv1.data.domain.model.toDomain
import javax.inject.Inject

class DatabaseRepository @Inject constructor(private val modelEntityDao: ModelEntityDao){

    suspend fun getModelsFromDatabase(): List<CarModel>{
        val response: List<ModelEntity> = modelEntityDao.getAllModels()
        return response.map { it.toDomain() }
    }

    suspend fun insertModel(modelEntity: ModelEntity){
        modelEntityDao.insert(modelEntity)
    }
}