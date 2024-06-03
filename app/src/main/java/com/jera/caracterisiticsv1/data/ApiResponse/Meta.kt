package com.jera.caracterisiticsv1.data.ApiResponse

data class Meta(
    val classifier: Int,
    val md5: String,
    val parameters: Parameters,
    val time: Double
)