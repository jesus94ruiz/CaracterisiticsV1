package com.jera.caracterisiticsv1.data

import com.google.gson.JsonElement
import com.jera.caracterisiticsv1.data.ApiResponse.CarSpecsApiResponse.CarSpecsGeneration
import com.jera.caracterisiticsv1.data.ApiResponse.CarSpecsApiResponse.CarSpecsMake
import com.jera.caracterisiticsv1.data.ApiResponse.CarSpecsApiResponse.CarSpecsModel
import com.jera.caracterisiticsv1.data.ApiResponse.CarSpecsApiResponse.CarSpecsTrimBasic
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CarSpecsRetrofitService {

    @GET("v2/cars/makes")
    suspend fun getMakes(
        @Query("name") name: String? = null
    ): Response<List<CarSpecsMake>>

    @GET("v2/cars/makes/{makeId}/models")
    suspend fun getModels(
        @Path("makeId") makeId: String,
        @Query("name") name: String? = null
    ): Response<List<CarSpecsModel>>

    @GET("v2/cars/models/{modelId}/generations")
    suspend fun getGenerations(
        @Path("modelId") modelId: String
    ): Response<List<CarSpecsGeneration>>

    @GET("v2/cars/generations/{generationId}/trims")
    suspend fun getTrims(
        @Path("generationId") generationId: String
    ): Response<List<CarSpecsTrimBasic>>

    @GET("v2/cars/trims/{id}")
    suspend fun getTrimDetail(
        @Path("id") trimId: String
    ): Response<JsonElement>
}
