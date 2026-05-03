package com.jera.caracterisiticsv1.data

import android.util.Log
import com.google.gson.Gson
import com.jera.caracterisiticsv1.data.ApiResponse.CarSpecsApiResponse.CarSpecsGeneration
import com.jera.caracterisiticsv1.data.ApiResponse.CarSpecsApiResponse.CarSpecsMake
import com.jera.caracterisiticsv1.data.ApiResponse.CarSpecsApiResponse.CarSpecsModel
import com.jera.caracterisiticsv1.data.ApiResponse.CarSpecsApiResponse.CarSpecsTrimBasic
import com.jera.caracterisiticsv1.data.ApiResponse.CarSpecsApiResponse.CarSpecsTrimDetail
import retrofit2.Response
import javax.inject.Inject

class CarSpecsDataSourceImpl @Inject constructor(
    private val carSpecsRetrofitService: CarSpecsRetrofitService
) : CarSpecsDataSource {

    private val gson = Gson()

    companion object {
        private const val TAG = "CarSpecsDataSourceImpl"
    }

    override suspend fun getMakes(name: String?): Response<List<CarSpecsMake>> =
        carSpecsRetrofitService.getMakes(name)

    override suspend fun getModels(makeId: String, name: String?): Response<List<CarSpecsModel>> =
        carSpecsRetrofitService.getModels(makeId, name)

    override suspend fun getGenerations(modelId: String): Response<List<CarSpecsGeneration>> =
        carSpecsRetrofitService.getGenerations(modelId)

    override suspend fun getTrims(generationId: String): Response<List<CarSpecsTrimBasic>> =
        carSpecsRetrofitService.getTrims(generationId)

    /**
     * El endpoint /v2/cars/trims/{id} puede devolver un objeto {} o un array [{}].
     * Usamos JsonElement para manejar ambos casos sin lanzar JsonSyntaxException.
     */
    override suspend fun getTrimDetail(trimId: String): CarSpecsTrimDetail? {
        val response = carSpecsRetrofitService.getTrimDetail(trimId)
        if (!response.isSuccessful) {
            Log.w(TAG, "getTrimDetail failed HTTP ${response.code()} for trimId=$trimId")
            return null
        }
        val element = response.body() ?: run {
            Log.w(TAG, "getTrimDetail body null for trimId=$trimId")
            return null
        }
        return try {
            when {
                element.isJsonObject -> {
                    gson.fromJson(element.asJsonObject, CarSpecsTrimDetail::class.java)
                }
                element.isJsonArray -> {
                    val arr = element.asJsonArray
                    if (arr.size() > 0) {
                        gson.fromJson(arr[0].asJsonObject, CarSpecsTrimDetail::class.java)
                    } else {
                        Log.w(TAG, "getTrimDetail returned empty array for trimId=$trimId")
                        null
                    }
                }
                else -> {
                    Log.w(TAG, "getTrimDetail unexpected JSON type for trimId=$trimId")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "getTrimDetail parse error for trimId=$trimId", e)
            null
        }
    }
}
