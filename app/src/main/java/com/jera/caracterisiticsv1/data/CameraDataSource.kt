package com.jera.caracterisiticsv1.data

import com.jera.caracterisiticsv1.data.ApiResponse.ApiResponse
import com.jera.caracterisiticsv1.data.ApiResponse.BraveApiResponse.BraveImageSearchResponse
import com.jera.caracterisiticsv1.data.ApiResponse.GoogleApiResponse.GoogleApiResponse
import retrofit2.Response
import java.io.File


interface CameraDataSource {

    suspend fun getModel(image: File): Response<ApiResponse>

    suspend fun getModelPictures(query: String): Response<GoogleApiResponse>

    suspend fun getModelPicturesBrave(query: String, count: Int = 5): Response<BraveImageSearchResponse>

}
