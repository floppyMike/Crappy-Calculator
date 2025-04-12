package com.example.crappycalculator

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.objecthunter.exp4j.ExpressionBuilder
import net.objecthunter.exp4j.operator.Operator
import kotlin.math.max

fun String.insertStrAtIndex(str: String, index: Int) =
    this.substring(0, index) + str + this.substring(index)

fun String.removeIndex(index: Int) = this.removeRange(index, index + 1)

fun String.setCharAtIndex(char: Char, index: Int) =
    this.replaceRange(index, index + 1, char.toString())

class CalculatorViewModel : ViewModel() {
    private val _state = MutableStateFlow(CalculatorState())
    val state = _state.asStateFlow()

    private val _history = MutableStateFlow(emptyList<CalculatorState>())
    val history = _history.asStateFlow()

    init {
        clear()
    }

    fun clear() {
        _state.value = CalculatorState()
    }

    fun cursorBack() {
        val self = _state.value

        val oldPos = self.cursorPos
        val newPos = max(0, self.cursorPos - 1)
        val oldPosStr = self.backspaceHistory.take(oldPos).sum()
        val newPosStr = self.backspaceHistory.take(newPos).sum()

        val noCursorStr =
            if (oldPos == 0 || oldPos == self.backspaceHistory.size ||
                (self.expression[oldPosStr + 1].isDigit()
                        && self.expression[oldPosStr - 1].isDigit())
            )
                self.expression.removeIndex(oldPosStr)
            else
                self.expression.setCharAtIndex(' ', oldPosStr)

        val newCursorStr =
            if (newPos == 0 || newPos == self.backspaceHistory.size ||
                (noCursorStr[newPosStr].isDigit()
                        && noCursorStr[newPosStr - 1].isDigit())
            )
                noCursorStr.insertStrAtIndex("|", newPosStr)
            else
                noCursorStr.setCharAtIndex('|', newPosStr)

        _state.value = self.copy(expression = newCursorStr, cursorPos = newPos)
    }

    fun eval() {
        val self = _state.value
        val expr = self.expression.replace("|", "").replace(" ", "")

        val factorial = object : Operator("!", 1, true, PRECEDENCE_POWER + 1) {
            override fun apply(vararg args: Double): Double {
                if (args[0] < 0) {
                    throw IllegalArgumentException("Factorial can't be less than zero")
                }

                return (1..args[0].toInt()).fold(1.0) { acc, i -> acc * i }
            }
        }

        val res = ExpressionBuilder(expr)
            .operator(factorial)
            .build()
            .evaluate()
            .toString()

        val newState = self.copy(result = res)
        _history.value += newState
        _state.value = newState
    }

    fun input(digit: String) {
        val self = _state.value
        val strCursorPos = self.backspaceHistory.take(self.cursorPos).sum()
        val doSpace = self.backspaceHistory.isEmpty() || self.expression[strCursorPos - 1].isDigit()
        val d = if (doSpace) digit else " $digit"

        val expr = self.expression.insertStrAtIndex(d, strCursorPos)
        val bs = self.backspaceHistory + d.length
        val cursorPos = self.cursorPos + 1

        _state.value = self.copy(
            expression = expr,
            backspaceHistory = bs,
            cursorPos = cursorPos,
        )
    }
}