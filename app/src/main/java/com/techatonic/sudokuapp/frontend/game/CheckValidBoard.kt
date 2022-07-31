package com.techatonic.sudokuapp.frontend.game

import java.util.*
import java.util.stream.Collectors
import kotlin.streams.toList

object CheckValidBoard {

    fun isValidClassicGrid(board: Board): Boolean {
        // Checks valid rows
        val grid = board.getGrid()
        for (row in grid) {
            val newRow = row.stream().filter { x: Int -> x != 0 }.toArray()
            val setRow = Arrays.stream(newRow).collect(Collectors.toSet())
            if (newRow.size != setRow.size) {
                return false
            }
        }
        // Checks valid columns
        for (col in grid.indices) {
            val column = IntArray(grid.size)
            for (i in grid.indices) {
                column[i] = grid[i][col]
            }
            val newCol = Arrays.stream(column).filter { x: Int -> x != 0 }.toArray()
            val setCol = Arrays.stream(newCol).boxed().collect(Collectors.toSet())
            if (newCol.size != setCol.size) {
                return false
            }
        }
        // Checks valid boxes
        for (boxX in 0..2) {
            for (boxY in 0..2) {
                val values = IntArray(grid.size)
                for (x in 3 * boxX until 3 * boxX + 3) {
                    for (y in 3 * boxY until 3 * boxY + 3) {
                        values[3 * (x % 3) + y % 3] = grid[x][y]
                    }
                }
                val newVals = Arrays.stream(values).filter { x: Int -> x != 0 }.toArray()
                val setVals = Arrays.stream(values).boxed().filter { x: Int -> x != 0 }.collect(
                    Collectors.toSet()
                )
                if (newVals.size != setVals.size) {
                    return false
                }
            }
        }
        return true
    }

    fun isValidKillerGrid(board: Board): Boolean{
        if(!isValidClassicGrid(board)) {
            return false
        }
        val grid = board.getGrid()
        for (cage in board.cages!!) {
            val sumVal: Int = cage.second.stream().mapToInt { x ->
                grid[x.first][x.second]
            }.sum()
            val newSum: List<Pair<Int, Int>> = cage.second.stream().filter { x ->
                grid[x.first][x.second] != 0
            }.toList()
            if (newSum.size == cage.second.size) {
                if (sumVal != cage.first) {
                    return false
                }
            } else {
                if (sumVal >= cage.first) {
                    return false
                }
                // TODO Improve detection of cage validation
                if (sumVal + 9 * (cage.second.size - newSum.size) < cage.first) {
                    return false
                }
            }
        }
        return true
    }
}