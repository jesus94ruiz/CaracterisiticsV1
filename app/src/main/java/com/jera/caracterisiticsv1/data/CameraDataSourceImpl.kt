package com.jera.caracterisiticsv1.data

import com.jera.caracterisiticsv1.data.ApiResponse.ApiResponse
import com.jera.caracterisiticsv1.data.GoogleApiResponse.GoogleApiResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class CameraDataSourceImpl @Inject constructor( private val retrofitService: RetrofitService) : CameraDataSource{

    override suspend fun getModel(image: File): Response<ApiResponse> {

        val imageRequestBody = image.asRequestBody("application/octet-stream".toMediaTypeOrNull())
        val imageMultipart = MultipartBody.Part.createFormData("ImageToAnalise", image.name, imageRequestBody)
        val features = arrayOf("mmg", "color", "angle");

        return retrofitService.getModel(
            "https://api.carnet.ai/v2/mmg/detect",
            0,
            180,
            180,
            1f,
            3.15f,
            "all",
            features,
            "DEF",
            imageMultipart
        )
    }

    override suspend fun getModelPictures(query: String): Response<GoogleApiResponse> {
        val url = "https://customsearch.googleapis.com/customsearch/v1"
        return retrofitService.getModelPictures(
            url,
            query,
            "AIzaSyDo2g-a8dUTC3ZcTZsoVMXMucFtRMPhpcw",
            5,
            "608be117b629e4b7d",
            "image",
        )
    }
}