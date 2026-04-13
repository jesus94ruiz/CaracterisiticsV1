package com.jera.caracterisiticsv1.data.ApiResponse.GoogleApiResponse

data class SearchInformation(
    val formattedSearchTime: String,
    val formattedTotalResults: String,
    val searchTime: Double,
    val totalResults: String
)