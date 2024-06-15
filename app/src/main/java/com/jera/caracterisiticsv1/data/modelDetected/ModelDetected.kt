package com.jera.caracterisiticsv1.data.modelDetected
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ModelDetected (
    val make_name: String,
    val model_name: String,
    val years: String,
    val probability: Double,
    val searchedImages: MutableList<String>
): Parcelable