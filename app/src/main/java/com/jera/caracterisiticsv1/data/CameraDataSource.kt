package com.jera.caracterisiticsv1.data

import com.jera.caracterisiticsv1.data.ApiResponse.ApiResponse
import com.jera.caracterisiticsv1.data.GoogleApiResponse.GoogleApiResponse
import retrofit2.Response
import java.io.File


interface CameraDataSource {

    suspend fun getModel(image: File): Response<ApiResponse>

    suspend fun getModelPictures(query: String): Response<GoogleApiResponse>

}