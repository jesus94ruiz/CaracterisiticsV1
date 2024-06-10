package com.jera.caracterisiticsv1.data.GoogleApiResponse

data class GoogleApiResponse(
    val context: Context,
    val items: List<Item>,
    val kind: String,
    val queries: Queries,
    val searchInformation: SearchInformation,
    val url: Url
)