package com.example.crappycalculator

import net.objecthunter.exp4j.ExpressionBuilder
import net.objecthunter.exp4j.operator.Operator

data class CalculationState(
    val cursorPos: Int = 0,
    val bsHistory: List<Token> = emptyList(),
)

fun getExpression(self: CalculationState, cursor: Boolean): String {
    val exprBuilder = StringBuilder()

    var wasDigit = false
    var wasLog = false
    self.bsHistory.forEachIndexed { i, v ->
        val tokenStr = v.toString()
        val isDigit = tokenStr.length == 1 && tokenStr[0].isDigit()
        val isLog = v == Token.LN
        val isE = v == Token.E

        exprBuilder.append(
            if (cursor && self.cursorPos == i) "|$tokenStr"
            else if (i == 0 || wasDigit && isDigit || wasLog && (isDigit || isE)) tokenStr
            else " $tokenStr"
        )

        wasDigit = isDigit
        wasLog = isLog
    }

    if (cursor && self.cursorPos == self.bsHistory.size) exprBuilder.append('|')

    return exprBuilder.toString()
}

fun eval(self: CalculationState): Double {
    val expr = getExpression(self, false)

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