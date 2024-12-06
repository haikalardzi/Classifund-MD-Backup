package com.bangkit.classifund.model

data class Transaction(
    val id: String = "",
    val category: String = "",
    val date: String = "",
    val description: String = "",
    val total: Long = 0L,
    val type: String = ""
)