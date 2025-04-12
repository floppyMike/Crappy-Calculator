package com.example.crappycalculator

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.objecthunter.exp4j.ExpressionBuilder
import net.objecthunter.exp4j.operator.Operator
import kotlin.math.max
import kotlin.math.min

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

    fun cursorBackward() {
        val self = _state.value
        cursorMov(max(0, self.cursorPos - 1))
    }

    fun cursorForward() {
        val self = _state.value
        cursorMov(min(self.bsHistory.size, self.cursorPos + 1))
    }

    fun cursorFront() {
        val self = _state.value
        cursorMov(self.bsHistory.size)
    }

    fun cursorBack() {
        val self = _state.value
        cursorMov(0)
    }

    private fun cursorMov(newPos: Int) {
        val self = _state.value
        val oldPos = self.cursorPos

        val oldPosStr = self.bsHistory.take(oldPos).sum()
        val newPosStr = self.bsHistory.take(newPos).sum()

        val noCursorStr =
            if (oldPos == 0 || oldPos == self.bsHistory.size ||
                (self.expression[oldPosStr + 1].isDigit()
                        && self.expression[oldPosStr - 1].isDigit())
            )
                self.expression.removeIndex(oldPosStr)
            else
                self.expression.setCharAtIndex(' ', oldPosStr)

        val newCursorStr =
            if (newPos == 0 || newPos == self.bsHistory.size ||
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
        val strCursorPos = self.bsHistory.take(self.cursorPos).sum()

        val isDigit = digit.length == 1 && digit[0].isDigit()
        val wasDigit = self.bsHistory.isEmpty() || self.expression[strCursorPos - 1].isDigit()
        val doSpace = isDigit && wasDigit
        val d = if (doSpace) digit else " $digit"

        val expr = self.expression.insertStrAtIndex(d, strCursorPos)
        val bs = self.bsHistory + d.length
        val cursorPos = self.cursorPos + 1

        _state.value = self.copy(
            expression = expr,
            bsHistory = bs,
            cursorPos = cursorPos,
        )
    }
}