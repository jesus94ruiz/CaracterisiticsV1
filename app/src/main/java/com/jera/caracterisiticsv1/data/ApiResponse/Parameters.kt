package com.jera.caracterisiticsv1.data.ApiResponse

data class Parameters(
    val box_max_ratio: Double,
    val box_min_height: Int,
    val box_min_ratio: Int,
    val box_min_width: Int,
    val box_select: String,
    val features: List<String>,
    val region: List<String>
)