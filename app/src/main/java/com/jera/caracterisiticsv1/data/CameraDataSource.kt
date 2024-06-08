package com.jera.caracterisiticsv1.data

import com.jera.caracterisiticsv1.data.ApiResponse.ApiResponse
import retrofit2.Response
import java.io.File


interface CameraDataSource {

    suspend fun getModel(image: File): Response<ApiResponse>

}