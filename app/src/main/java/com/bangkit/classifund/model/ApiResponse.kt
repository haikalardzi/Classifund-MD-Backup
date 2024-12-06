package com.bangkit.classifund.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse(
    val text: String,
    val predicted_category: String,
    val confidence: Double
)