package com.example.crappycalculator

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.max
import kotlin.math.min

data class LocalState(
    val input: List<Token> = emptyList(),
    val cursorPos: Int = input.size,
    val result: String? = null,
)

data class CalculatorState(
    val viewIdx: Int = 0,
    val history: List<CalculationState> = emptyList(),
    val cur: LocalState = LocalState(),
)

fun <T> List<T>.insertAtIndex(x: T, idx: Int) =
    this.subList(0, idx) + x + this.subList(idx, this.size)

fun <T> List<T>.removeAtIndex(idx: Int) =
    this.subList(0, idx - 1) + this.subList(idx, this.size)

class CalculatorViewModel : ViewModel() {
    private val _state = MutableStateFlow(CalculatorState())
    val state = _state.asStateFlow()

    private fun appendToHistory(s: CalculatorState): CalculatorState {
        if (s.cur.result == null || s.history.lastOrNull()?.bsHistory == s.cur.input) return s

        return s.copy(
            history = s.history + CalculationState(
                bsHistory = s.cur.input,
                result = s.cur.result
            ),
        )
    }

    // History Movement

    fun backward() = _state.update {
        if (it.viewIdx <= 0) throw IllegalStateException("Can't go further back.")
        it.copy(viewIdx = it.viewIdx - 1)
    }

    fun forward() = _state.update {
        if (it.viewIdx >= it.history.size) throw IllegalStateException("Can't go further forward.")
        it.copy(viewIdx = it.viewIdx + 1)
    }

    fun goto(histIdx: Int) = _state.update {
        assert(histIdx <= it.history.size && histIdx >= 0)
        it.copy(viewIdx = histIdx)
    }

    // Cursor Movement

    fun cursorBackward() = _state.update {
        val cur = it.cur
        it.copy(
            viewIdx = it.history.size,
            cur = cur.copy(cursorPos = max(0, cur.cursorPos - 1))
        )
    }

    fun cursorForward() = _state.update {
        val cur = it.cur
        it.copy(
            viewIdx = it.history.size,
            cur = cur.copy(cursorPos = min(cur.input.size, cur.cursorPos + 1))
        )
    }

    fun cursorFront() = _state.update {
        val cur = it.cur
        it.copy(viewIdx = it.history.size, cur = cur.copy(cursorPos = cur.input.size))
    }

    fun cursorBack() = _state.update {
        val cur = it.cur
        it.copy(viewIdx = it.history.size, cur = cur.copy(cursorPos = 0))
    }

    // Calculation

    fun eval() = _state.update {
        if (it.viewIdx == it.history.size)
            // This could be problematic for larger expressions due to it running on the GUI Thread,
            // however tests show that it doesn't take up really any time. Maybe someday in the
            // future :)
            it.copy(cur = it.cur.copy(result = eval(it.cur.input).toString()))
        else {
            val h = appendToHistory(it)
            h.copy(
                viewIdx = h.history.size,
                cur = LocalState(input = h.history[h.viewIdx].bsHistory)
            )
        }
    }

    // Text Manipulation

    fun clear() = _state.update {
        if (it.viewIdx != it.history.size) it.copy(viewIdx = it.history.size)
        else {
            val h = appendToHistory(it)
            h.copy(viewIdx = h.history.size, cur = LocalState())
        }
    }

    fun backspace() = _state.update {
        if (it.cur.cursorPos <= 0) return@update it
        val h = appendToHistory(it)
        h.copy(
            viewIdx = h.history.size,
            cur = h.cur.copy(
                cursorPos = h.cur.cursorPos - 1,
                input = h.cur.input.removeAtIndex(h.cur.cursorPos),
                result = null,
            )
        )
    }

    fun input(token: Token) = _state.update {
        val h = appendToHistory(it)
        h.copy(
            viewIdx = h.history.size,
            cur = h.cur.copy(
                cursorPos = h.cur.cursorPos + 1,
                input = h.cur.input.insertAtIndex(token, h.cur.cursorPos),
                result = null,
            )
        )
    }

}