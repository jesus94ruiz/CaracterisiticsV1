package com.jera.caracterisiticsv1.data

import com.jera.caracterisiticsv1.data.ApiResponse.ApiResponse
import com.jera.caracterisiticsv1.data.ApiResponse.CarImagesApiResponse.CarImagesApiResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface RetrofitService {

    @Headers(
        "api-key: 2036319c-1733-4f46-95bd-ca42ef105043"
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

    /**
     * CarImagesAPI - Signed URL
     * Doc: https://carimagesapi.com/docs#quickstart
     *
     * Endpoint:
     *  GET https://carimagesapi.com/api/v1/signed-url?make=BMW&model=3+Series&year=2022&api_key=...
     *
     * Respuesta: { "url": "https://carimagesapi.com/image?..." }
     * Auth: Query param "api_key" añadido automáticamente via OkHttp Interceptor (AppModule).
     */
    @GET
    suspend fun getCarImages(
        @Url url: String,
        @Query("make") make: String,
        @Query("model") model: String,
        @Query("year") year: String? = null,
    ): Response<CarImagesApiResponse>
}
