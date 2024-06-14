package com.jera.caracterisiticsv1.repository

import com.jera.caracterisiticsv1.data.ApiResponse.ApiResponse
import com.jera.caracterisiticsv1.data.CameraDataSource
import com.jera.caracterisiticsv1.data.GoogleApiResponse.GoogleApiResponse
import com.jera.caracterisiticsv1.utilities.ResourceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject

class CameraRepository @Inject constructor( private val cameraDataSource: CameraDataSource){

    suspend fun getModel(image : File): Flow<ResourceState<ApiResponse>>{
        return flow{

            emit(ResourceState.Loading())

            val response = cameraDataSource.getModel(image)
            println("API response: $response")

            if(response.isSuccessful && response.body() != null){
                println("API Success: ${response.body()}")
                emit(ResourceState.Success(response.body()!!))
            } else {
                println("API Error: ${response.errorBody()}")
                emit( ResourceState.Error("Error fetching model data"))
            }

        }.catch { e -> emit(ResourceState.Error(e?.localizedMessage ?: "Error happened in flow")) }
    }

    suspend fun getModelPictures(query: String): Flow<ResourceState<GoogleApiResponse>> {
        return flow{
            emit(ResourceState.Loading())

            val response = cameraDataSource.getModelPictures(query)
            println(response)

            if(response.isSuccessful && response.body() != null){
                emit(ResourceState.Success(response.body()!!))
            } else {
                emit( ResourceState.Error("Error fetching model pictures"))
            }
        }.catch { e -> emit(ResourceState.Error(e?.localizedMessage ?: "Error happened in flow")) }
    }
}