package com.jera.caracterisiticsv1.data.ApiResponse

data class Mmg(
    val generation_id: Int,
    val generation_name: String,
    val make_id: Int,
    val make_name: String,
    val model_id: Int,
    val model_name: String,
    val probability: Double,
    val years: String
)