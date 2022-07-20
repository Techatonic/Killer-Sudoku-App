package com.techatonic.sudokuapp.frontend.game

class Board(private val size:Int, val cells: List<Cell>) {

    fun getCell(row:Int, col:Int) = cells[row*size + col]
}