package com.example.appsofware.data

data class HistorialEntry(
    val id: Int,
    val nombre: String,
    val timestamp: Long = System.currentTimeMillis()
)