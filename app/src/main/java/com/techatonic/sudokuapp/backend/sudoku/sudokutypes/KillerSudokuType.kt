package com.techatonic.sudokuapp.backend.sudoku.sudokutypes

import kotlin.Pair

class KillerSudokuType : ClassicSudokuType {
    var cages: MutableList<Pair<Int, List<Pair<Int, Int>>>>? = mutableListOf()

    constructor() : super(SudokuType.Killer) {}
    constructor(sudokuType: SudokuType, unfilledGrid: Array<IntArray>, filledGrid: Array<IntArray>) : super(sudokuType, unfilledGrid, filledGrid) {}

    fun getCage(pos: Pair<Int, Int>): Pair<Int, List<Pair<Int, Int>>>? {
        for (cage in cages!!) {
            if (cage.second.contains(pos)) {
                return cage
            }
        }
        return null
    }

    fun getBox(pos: Pair<Int, Int>): ArrayList<Pair<Int, Int>> {
        val result = ArrayList<Pair<Int, Int>>()
        for (row in pos.first / 3 * 3 until pos.first / 3 * 3 + 3) {
            for (col in pos.second / 3 * 3 until pos.second / 3 * 3 + 3) {
                if (!(row == pos.first && col == pos.second)) {
                    result.add(Pair(row, col))
                }
            }
        }
        return result
    }

    fun getRow(pos: Pair<Int, Int>): ArrayList<Pair<Int, Int>> {
        val result = ArrayList<Pair<Int, Int>>()
        for (col in 0..8) {
            if (col != pos.second) {
                result.add(Pair(pos.first, col))
            }
        }
        return result
    }

    fun getCol(pos: Pair<Int, Int>): ArrayList<Pair<Int, Int>> {
        val result = ArrayList<Pair<Int, Int>>()
        for (row in 0..8) {
            if (row != pos.first) {
                result.add(Pair(row, pos.second))
            }
        }
        return result
    }

    fun addCage(cage: Pair<Int, List<Pair<Int, Int>>>) {
        cages!!.add(cage)
    }

    override fun PrintSudoku() {
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
    }

    override fun PrintSudokuStats() {
        super.PrintSudokuStats(grid)
        //super.PrintSudokuStats();
        if (cages == null) {
            println("Cages: null")
            return
        }
        println("Num of cages: " + cages!!.size)
        for (cage in cages!!) {
            println(
                """
    
    Sum: ${cage.first}
    """.trimIndent()
            )
            print("Points: ")
            for (point in cage.second) {
                print("$point   ")
            }
            print("\n")
        }
    }
}