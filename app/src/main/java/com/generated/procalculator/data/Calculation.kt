package com.generated.procalculator.data

data class Calculation(
    val expression: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)