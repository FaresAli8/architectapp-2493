package com.generated.procalculator.domain

import java.util.Stack
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * A robust mathematical expression evaluator using Shunting-yard algorithm.
 */
object Evaluator {

    fun evaluate(expression: String): Double {
        val tokens = tokenize(expression)
        val rpn = shuntingYard(tokens)
        return calculateRPN(rpn)
    }

    private fun tokenize(expression: String): List<String> {
        val tokens = mutableListOf<String>()
        var i = 0
        while (i < expression.length) {
            val c = expression[i]
            when {
                c.isWhitespace() -> i++
                c.isDigit() || c == '.' -> {
                    val sb = StringBuilder()
                    while (i < expression.length && (expression[i].isDigit() || expression[i] == '.')) {
                        sb.append(expression[i])
                        i++
                    }
                    tokens.add(sb.toString())
                }
                c == '-' && (i == 0 || expression[i - 1] == '(' || "+-*/^".contains(expression[i - 1])) -> {
                    // Unary minus handling: treat as negative number or handle via 0 - x logic
                    // Simplified: read next number and negate
                    i++
                    val sb = StringBuilder("-")
                    while (i < expression.length && (expression[i].isDigit() || expression[i] == '.')) {
                        sb.append(expression[i])
                        i++
                    }
                    if (sb.length == 1) tokens.add("-") // Just operator
                    else tokens.add(sb.toString())
                }
                // Handle SQRT symbol specifically
                c == '√' -> {
                    tokens.add("sqrt")
                    i++
                }
                else -> {
                    tokens.add(c.toString())
                    i++
                }
            }
        }
        return tokens
    }

    private fun shuntingYard(tokens: List<String>): List<String> {
        val output = mutableListOf<String>()
        val stack = Stack<String>()
        val precedence = mapOf(
            "+" to 1, "-" to 1,
            "*" to 2, "/" to 2,
            "^" to 3, "sqrt" to 4
        )

        for (token in tokens) {
            when {
                token.toDoubleOrNull() != null -> output.add(token)
                token == "(" -> stack.push(token)
                token == ")" -> {
                    while (stack.isNotEmpty() && stack.peek() != "(") {
                        output.add(stack.pop())
                    }
                    if (stack.isNotEmpty()) stack.pop() // Pop '('
                }
                else -> { // Operator
                    while (stack.isNotEmpty() && stack.peek() != "(" &&
                        (precedence[stack.peek()] ?: 0) >= (precedence[token] ?: 0)
                    ) {
                        output.add(stack.pop())
                    }
                    stack.push(token)
                }
            }
        }
        while (stack.isNotEmpty()) {
            output.add(stack.pop())
        }
        return output
    }

    private fun calculateRPN(tokens: List<String>): Double {
        val stack = Stack<Double>()
        for (token in tokens) {
            if (token.toDoubleOrNull() != null) {
                stack.push(token.toDouble())
            } else {
                if (token == "sqrt") {
                    val a = stack.pop()
                    stack.push(sqrt(a))
                } else {
                    if (stack.size < 2) throw IllegalArgumentException("Invalid Expression")
                    val b = stack.pop()
                    val a = stack.pop()
                    val res = when (token) {
                        "+" -> a + b
                        "-" -> a - b
                        "*" -> a * b
                        "/" -> a / b
                        "^" -> a.pow(b)
                        else -> 0.0
                    }
                    stack.push(res)
                }
            }
        }
        return if (stack.isNotEmpty()) stack.pop() else 0.0
    }
}