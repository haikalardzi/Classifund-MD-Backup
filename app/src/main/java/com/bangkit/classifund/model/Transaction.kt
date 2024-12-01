package com.bangkit.classifund.model

data class Transaction (
    val date: String,
    val userId: String,
    val type: String,
    val description: String,
    val value: Double,
)