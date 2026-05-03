package com.jera.caracterisiticsv1.data.ApiResponse.CarSpecsApiResponse

import com.google.gson.annotations.SerializedName

data class CarSpecsGeneration(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String? = null,
    @SerializedName("year_begin") val yearBegin: Int? = null,
    @SerializedName("year_end") val yearEnd: Int? = null,
    @SerializedName("model_id") val modelId: String? = null
)
