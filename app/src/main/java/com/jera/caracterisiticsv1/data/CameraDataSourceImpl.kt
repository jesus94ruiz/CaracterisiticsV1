package com.jera.caracterisiticsv1.data

import com.jera.caracterisiticsv1.data.ApiResponse.ApiResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class CameraDataSourceImpl @Inject constructor( private val apiService: RetrofitService) : CameraDataSource{

    override suspend fun getModel(image: File): Response<ApiResponse> {

        val imageRequestBody = image.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart = MultipartBody.Part.createFormData("ImageToAnalise", image.name, imageRequestBody)
        val features = arrayOf("mmg", "color", "angle");

        return apiService.getModel(
            0,
            180,
            180,
            1f,
            3.15f,
            "center",
            features,
            "DEF",
            imageMultipart
        )
    }
}