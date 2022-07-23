package com.techatonic.sudokuapp.backend.sudoku

import kotlin.Throws
import com.techatonic.sudokuapp.frontend.game.SudokuGame

object RetrieveSudoku {
    fun retrieveClassicSudoku(sudokuGame: SudokuGame) {
        GetClassicSudokuFromDatabase().retrieveClassicSudoku(sudokuGame)
    }

    @Throws(InterruptedException::class)
    fun retrieveKillerSudoku(sudokuGame: SudokuGame) {
        GetKillerSudokuFromDatabase().retrieveKillerSudoku(sudokuGame)
    }
}