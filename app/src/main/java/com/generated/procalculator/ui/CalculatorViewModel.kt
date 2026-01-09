package com.generated.procalculator.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.generated.procalculator.data.Calculation
import com.generated.procalculator.data.CalculatorRepository
import com.generated.procalculator.domain.Evaluator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class CalculatorViewModel : ViewModel() {
    private val repository = CalculatorRepository()
    val history = repository.history

    private val _display = MutableStateFlow("0")
    val display: StateFlow<String> = _display.asStateFlow()

    private val _result = MutableStateFlow("")
    val result: StateFlow<String> = _result.asStateFlow()

    private var isNewEquation = true
    private val decimalFormat = DecimalFormat("#.##########")

    fun onAction(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.Number -> enterNumber(action.number)
            is CalculatorAction.Operator -> enterOperator(action.symbol)
            CalculatorAction.Clear -> clear()
            CalculatorAction.Delete -> delete()
            CalculatorAction.Calculate -> calculate()
            CalculatorAction.Decimal -> enterDecimal()
            CalculatorAction.ClearHistory -> repository.clearHistory()
            is CalculatorAction.HistoryItemClick -> restoreHistory(action.calc)
        }
    }

    private fun enterNumber(number: Int) {
        if (isNewEquation) {
            _display.value = number.toString()
            isNewEquation = false
        } else {
            if (_display.value == "0") _display.value = number.toString()
            else _display.value += number
        }
        calculatePreview()
    }

    private fun enterOperator(symbol: String) {
        isNewEquation = false
        val current = _display.value
        // Prevent double operators if needed, but allow negatives
        _display.value += symbol
    }

    private fun enterDecimal() {
        if (!_display.value.endsWith(".")) {
            _display.value += "."
            isNewEquation = false
        }
    }

    private fun clear() {
        _display.value = "0"
        _result.value = ""
        isNewEquation = true
    }

    private fun delete() {
        val current = _display.value
        if (current.isNotEmpty()) {
            _display.value = current.dropLast(1)
            if (_display.value.isEmpty()) {
                _display.value = "0"
                isNewEquation = true
            }
        }
        calculatePreview()
    }

    private fun calculate() {
        try {
            val expression = _display.value
                .replace("×", "*")
                .replace("÷", "/")
            
            val evalResult = Evaluator.evaluate(expression)
            val formatted = decimalFormat.format(evalResult)
            
            repository.addCalculation(Calculation(expression, formatted))
            
            _display.value = formatted
            _result.value = ""
            isNewEquation = true
        } catch (e: Exception) {
            _result.value = "Error"
        }
    }
    
    private fun calculatePreview() {
        // Optional: Realtime preview
    }
    
    private fun restoreHistory(calc: Calculation) {
        _display.value = calc.result // or calc.expression to edit
        isNewEquation = true
    }
}

sealed class CalculatorAction {
    data class Number(val number: Int) : CalculatorAction()
    data class Operator(val symbol: String) : CalculatorAction()
    object Clear : CalculatorAction()
    object Delete : CalculatorAction()
    object Calculate : CalculatorAction()
    object Decimal : CalculatorAction()
    object ClearHistory : CalculatorAction()
    data class HistoryItemClick(val calc: Calculation) : CalculatorAction()
}