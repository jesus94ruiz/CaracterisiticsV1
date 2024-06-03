package com.jera.caracterisiticsv1.data.ApiResponse

data class ApiResponse(
    val detections: List<Detection>,
    val is_success: Boolean,
    val meta: Meta
)