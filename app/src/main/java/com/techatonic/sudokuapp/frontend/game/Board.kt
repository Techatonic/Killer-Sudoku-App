package com.techatonic.sudokuapp.frontend.game

class Board(private val size:Int, val cells: List<Cell>, val cages:  MutableList<Pair<Int, List<Pair<Int, Int>>>>? = null) {

    fun getCell(row:Int, col:Int) = cells[row*size + col]

    fun getGrid(): MutableList<MutableList<Int>>{
        val grid = mutableListOf<MutableList<Int>>()
        for (rowNum in 0..8){
            val row = mutableListOf<Int>()
            for (colNum in 0..8){
                row.add(cells.get(rowNum*9 + colNum).value)
            }
            grid.add(row)
        }
        return grid
    }
}