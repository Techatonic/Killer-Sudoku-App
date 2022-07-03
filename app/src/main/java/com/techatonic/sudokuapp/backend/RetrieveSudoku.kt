package com.techatonic.sudokuapp.backend

import com.techatonic.sudokuapp.backend.sudokutypes.ClassicSudokuType
import kotlin.Throws
import com.techatonic.sudokuapp.backend.sudokutypes.KillerSudokuType
import com.techatonic.sudokuapp.backend.GetKillerSudokuFromDatabase
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