package com.generated.procalculator.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CalculatorRepository {
    private val _history = MutableStateFlow<List<Calculation>>(emptyList())
    val history: StateFlow<List<Calculation>> = _history.asStateFlow()

    fun addCalculation(calc: Calculation) {
        _history.update { current ->
            listOf(calc) + current
        }
    }

    fun clearHistory() {
        _history.value = emptyList()
    }
}