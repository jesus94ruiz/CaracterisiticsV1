package com.jera.caracterisiticsv1.data

import com.jera.caracterisiticsv1.data.ApiResponse.ApiResponse
import com.jera.caracterisiticsv1.data.GoogleApiResponse.GoogleApiResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface RetrofitService {

    @Headers(
        "api-key: 7a8b75f5-5c1b-4811-a6fa-f49284da7dcd"
    )
    @Multipart
    @POST
    suspend fun getModel(
        @Url url: String,
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

    @GET
    suspend fun getModelPictures(
        @Url url: String,
        @Query("q") search_query: String,
        @Query("key") API_KEY: String,
        @Query("num") numberImages: Int,
        @Query("cx") SEARCH_ENGINE_ID: String,
        @Query("searchType") searchType: String,
        @Query("fileType") fileType: String,
    ): Response<GoogleApiResponse>
}