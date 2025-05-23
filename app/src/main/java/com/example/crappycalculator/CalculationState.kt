package com.example.crappycalculator

import net.objecthunter.exp4j.ExpressionBuilder
import net.objecthunter.exp4j.operator.Operator

data class CalculationState(
    val bsHistory: List<Token>,
    val result: String,
) {
    override fun toString(): String {
        val expr = getExpression(this.bsHistory, null)
        return "$expr = $result"
    }
}

fun getExpression(tokens: List<Token>, cursor: Int?): String {
    val exprBuilder = StringBuilder()

    var wasDigit = false
    var wasLog = false
    tokens.forEachIndexed { i, v ->
        val tokenStr = v.toString()
        val isDigit = tokenStr.length == 1 && (tokenStr[0].isDigit() || tokenStr[0] == '.')
        val isLog = v == Token.LOG

        exprBuilder.append(
            if (cursor == i) "|$tokenStr"
            else if (i == 0 || wasDigit && isDigit || wasLog && isDigit) tokenStr
            else " $tokenStr"
        )

        wasDigit = isDigit
        wasLog = isLog
    }

    if (cursor == tokens.size) exprBuilder.append('|')

    return exprBuilder.toString()
}

fun eval(tokens: List<Token>): Double {
    val expr = getExpression(tokens, null)

    val factorial = object : Operator("!", 1, true, PRECEDENCE_POWER + 1) {
        override fun apply(vararg args: Double): Double {
            if (args[0] < 0) {
                throw IllegalArgumentException("Factorial can't be less than zero")
            }

            return (1..args[0].toInt()).fold(1.0) { acc, i -> acc * i }
        }
    }

    return ExpressionBuilder(expr)
        .operator(factorial)
        .build()
        .evaluate()
}