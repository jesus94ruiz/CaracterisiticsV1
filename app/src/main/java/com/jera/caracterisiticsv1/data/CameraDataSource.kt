package com.jera.caracterisiticsv1.data

import com.jera.caracterisiticsv1.data.ApiResponse.ApiResponse
import com.jera.caracterisiticsv1.data.ApiResponse.CarImagesApiResponse.CarImagesApiResponse
import retrofit2.Response
import java.io.File


interface CameraDataSource {

    suspend fun getModel(image: File): Response<ApiResponse>

    suspend fun getCarImages(make: String, model: String, year: String? = null): Response<CarImagesApiResponse>

}
