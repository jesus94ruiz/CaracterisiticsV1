package com.jera.caracterisiticsv1.data

import com.jera.caracterisiticsv1.BuildConfig
import com.jera.caracterisiticsv1.data.ApiResponse.ApiResponse
import com.jera.caracterisiticsv1.data.ApiResponse.CarImagesApiResponse.CarImagesApiResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class CameraDataSourceImpl @Inject constructor(private val retrofitService: RetrofitService) : CameraDataSource {

    override suspend fun getModel(image: File): Response<ApiResponse> {
        val imageRequestBody = image.asRequestBody("application/octet-stream".toMediaTypeOrNull())
        val imageMultipart = MultipartBody.Part.createFormData("ImageToAnalise", image.name, imageRequestBody)
        val features = arrayOf("mmg", "color", "angle")

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

    override suspend fun getCarImages(
        make: String,
        model: String,
        year: String?
    ): Response<CarImagesApiResponse> {
        val url = "https://carimagesapi.com/api/v1/signed-url"
        return retrofitService.getCarImages(
            url = url,
            make = make,
            model = model,
            year = year
        )
    }
}
