package com.jera.caracterisiticsv1.data

import com.jera.caracterisiticsv1.data.ApiResponse.ApiResponse
import com.jera.caracterisiticsv1.data.ApiResponse.BraveApiResponse.BraveImageSearchResponse
import com.jera.caracterisiticsv1.data.ApiResponse.GoogleApiResponse.GoogleApiResponse
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

    @GET
    suspend fun getModelPictures(
        @Url url: String,
        @Query("q") search_query: String,
        @Query("key") API_KEY: String,
        @Query("num") numberImages: Int,
        @Query("cx") SEARCH_ENGINE_ID: String,
        @Query("searchType") searchType: String,
    ): Response<GoogleApiResponse>

    /**
     * Brave Search API - Image Search
     * Doc: https://brave.com/search/api/
     *
     * Endpoint típico:
     *  GET https://api.search.brave.com/res/v1/images/search?q=...&count=5&search_lang=es&country=ES&spellcheck=1&safesearch=moderate
     *
     * Auth: Header "X-Subscription-Token: <token>"
     *
     * Nota: el header se añade automáticamente mediante OkHttp Interceptor (AppModule).
     */
    @GET
    suspend fun braveImageSearch(
        @Url url: String,
        @Query("q") query: String,
        @Query("count") count: Int,
        @Query("search_lang") searchLang: String = "es",
        @Query("country") country: String = "ES",
        @Query("spellcheck") spellcheck: Int = 1,
        @Query("safesearch") safesearch: String = "off",
    ): Response<BraveImageSearchResponse>
}
