package com.jera.caracterisiticsv1.data

import com.jera.caracterisiticsv1.data.ApiResponse.CarSpecsApiResponse.CarSpecsGeneration
import com.jera.caracterisiticsv1.data.ApiResponse.CarSpecsApiResponse.CarSpecsMake
import com.jera.caracterisiticsv1.data.ApiResponse.CarSpecsApiResponse.CarSpecsModel
import com.jera.caracterisiticsv1.data.ApiResponse.CarSpecsApiResponse.CarSpecsTrimBasic
import com.jera.caracterisiticsv1.data.ApiResponse.CarSpecsApiResponse.CarSpecsTrimDetail
import retrofit2.Response

interface CarSpecsDataSource {
    suspend fun getMakes(name: String?): Response<List<CarSpecsMake>>
    suspend fun getModels(makeId: String, name: String?): Response<List<CarSpecsModel>>
    suspend fun getGenerations(modelId: String): Response<List<CarSpecsGeneration>>
    suspend fun getTrims(generationId: String): Response<List<CarSpecsTrimBasic>>
    suspend fun getTrimDetail(trimId: String): CarSpecsTrimDetail?
}
