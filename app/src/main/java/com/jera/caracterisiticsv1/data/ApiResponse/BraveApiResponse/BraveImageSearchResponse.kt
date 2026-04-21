package com.jera.caracterisiticsv1.data.ApiResponse.BraveApiResponse

import com.google.gson.annotations.SerializedName

/**
 * Modelos mínimos para consumir Brave Search API y extraer URLs de imágenes.
 * Basado en la respuesta de /res/v1/images/search (Brave Search API).
 */
data class BraveImageSearchResponse(
    val type: String? = null,
    val results: List<BraveImageResult> = emptyList()
)

data class BraveImageResult(
    val title: String? = null,
    val url: String? = null,

    // Algunos resultados incluyen una propiedad extra con info multimedia.
    @SerializedName("thumbnail")
    val thumbnail: BraveThumbnail? = null
)

data class BraveThumbnail(
    val src: String? = null,
    val height: Int? = null,
    val width: Int? = null
)
