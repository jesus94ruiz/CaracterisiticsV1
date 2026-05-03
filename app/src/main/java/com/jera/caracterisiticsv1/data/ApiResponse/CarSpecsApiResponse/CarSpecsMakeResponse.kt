package com.jera.caracterisiticsv1.data.ApiResponse.CarSpecsApiResponse

import com.google.gson.annotations.SerializedName

data class CarSpecsMake(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String
)
