package com.jera.caracterisiticsv1.data

import com.jera.caracterisiticsv1.data.ApiResponse.ApiResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface RetrofitService {

    @Headers(
        "api-key: '7a8b75f5-5c1b-4811-a6fa-f49284da7dcd"
    )
    @Multipart
    @POST("/detect")
    suspend fun getModel(
        @Query("box_offset") box_offset: Int,
        @Query("box_min_width") box_min_width: Int,
        @Query("box_min_height") box_min_height: Int,
        @Query("box_min_ratio") box_min_ratio: Float,
        @Query("box_min_ratio") box_max_ratio: Float,
        @Query("box_select") box_select: String,
        @Query("features") features: Array<String>,
        @Query("region") region: String,
        @Part image: MultipartBody.Part
    ): Response<ApiResponse>
}