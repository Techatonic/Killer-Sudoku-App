package com.techatonic.sudokuapp.frontend.game

class Cell(
    val row: Int,
    val col: Int,
    var value: Int,
    var isStartingCell: Boolean = false,
    var isValid: Boolean = true,
    var notes: MutableSet<Int> = mutableSetOf(),
)