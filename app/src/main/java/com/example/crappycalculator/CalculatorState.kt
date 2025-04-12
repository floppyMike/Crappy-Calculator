package com.example.crappycalculator

data class CalculatorState(
    val expression: String = "|",
    val cursorPos: Int = 0,
    val result: String = "0",
    val bsHistory: List<Int> = emptyList<Int>(),
)