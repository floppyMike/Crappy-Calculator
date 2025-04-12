package com.example.crappycalculator

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.max
import kotlin.math.min

class CalculatorViewModel : ViewModel() {
    data class CalculatorState(
        val calculation: CalculationState = CalculationState(),
        val viewIdx: Int = 0,
        val history: List<CalculationState> = emptyList(),
        val result: String = "0",
    )

    private val _state = MutableStateFlow(CalculatorState())
    val state = _state.asStateFlow()

    fun clear() = _state.update {
        if (it.viewIdx != it.history.size) it.copy(viewIdx = it.history.size)
        else it.copy(calculation = CalculationState())
    }

    fun cursorBackward() = _state.update {
        val cal = it.calculation
        it.copy(calculation = cal.copy(cursorPos = max(0, cal.cursorPos - 1)))
    }

    fun cursorForward() = _state.update {
        val cal = it.calculation
        it.copy(calculation = cal.copy(cursorPos = min(cal.bsHistory.size, cal.cursorPos + 1)))
    }

    fun cursorFront() = _state.update {
        val cal = it.calculation
        it.copy(calculation = cal.copy(cursorPos = cal.bsHistory.size))
    }

    fun cursorBack() = _state.update {
        val cal = it.calculation
        it.copy(calculation = cal.copy(cursorPos = 0))
    }

    fun eval() = _state.update {
        val res = eval(it.calculation).toString()
        val hist = it.history + it.calculation.copy()
        it.copy(history = hist, result = res)
    }

    fun input(token: Token) = _state.update {
        val cal = it.calculation
        it.copy(
            calculation = CalculationState(
                cursorPos = cal.cursorPos + 1,
                bsHistory = cal.bsHistory.subList(0, cal.cursorPos) + token +
                        cal.bsHistory.subList(cal.cursorPos, cal.bsHistory.size)
            )
        )
    }
}