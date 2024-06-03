package com.jera.caracterisiticsv1.data.ApiResponse

data class Detection(
    val angle: List<Angle>,
    val box: Box,
    val `class`: Class,
    val color: List<Color>,
    val mm: List<Any>,
    val mmg: List<Mmg>,
    val status: Status,
    val subclass: List<Subclas>
)