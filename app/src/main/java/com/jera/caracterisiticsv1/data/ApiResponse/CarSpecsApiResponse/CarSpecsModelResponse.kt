package com.jera.caracterisiticsv1.data.ApiResponse.CarSpecsApiResponse

import com.google.gson.annotations.SerializedName

data class CarSpecsModel(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("make_id") val makeId: String? = null
)
