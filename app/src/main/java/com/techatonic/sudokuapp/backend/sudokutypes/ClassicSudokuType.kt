package com.techatonic.sudokuapp.backend.sudokutypes

import com.techatonic.sudokuapp.backend.sudokutypes.ClassicSudokuType

open class ClassicSudokuType {
    enum class SudokuType {
        Classic, Arrow, Thermo, Killer
    }

    var type: SudokuType
        private set
    var grid: Array<IntArray>
        private set
    lateinit var filledGrid: Array<IntArray>

    constructor() {
        type = SudokuType.Classic
        grid = Array(9) { IntArray(9) }
    }

    constructor(type: SudokuType, grid: Array<IntArray>) {
        this.type = type
        this.grid = grid
    }

    constructor(grid: Array<IntArray>) {
        type = SudokuType.Classic
        this.grid = grid
    }

    constructor(type: SudokuType) {
        this.type = type
        grid = Array(9) { IntArray(9) }
    }

    constructor(sudoku: ClassicSudokuType) {
        type = sudoku.type
        grid = sudoku.grid
        filledGrid = sudoku.filledGrid
    }

    constructor(type: SudokuType, unfilledGrid: Array<IntArray>, filledGrid: Array<IntArray>) {
        this.type = type
        grid = unfilledGrid
        this.filledGrid = filledGrid
    }

    fun setPosition(row: Int, col: Int, `val`: Int) {
        grid[row][col] = `val`
    }

    open fun PrintSudoku() {
        PrintSudoku(grid)
    }

    fun PrintSudoku(grid: Array<IntArray>) {
        for (row in 0..8) {
            if (row % 3 == 0) {
                println(String(CharArray(22)).replace("\u0000", "-"))
            }
            for (col in 0..8) {
                if (col % 3 == 0) {
                    print("|")
                }
                if (grid[row][col] == 0) {
                    print("  ")
                } else {
                    print(" " + grid[row][col])
                }
                if (col == 8) {
                    print("|\n")
                }
            }
        }
        println(String(CharArray(22)).replace("\u0000", "-"))
        //System.out.println("\n");
    }

    open fun PrintSudokuStats() {
        PrintSudokuStats(grid)
    }

    fun PrintSudokuStats(grid: Array<IntArray>?) {
        println("Number of cells given: " + GetCellsGiven())
    }

    private fun GetCellsGiven(): Int {
        var cellsGiven = 0
        for (row in grid) {
            for (cell in row) {
                if (cell != 0) {
                    cellsGiven++
                }
            }
        }
        return cellsGiven
    }
}