package com.techatonic.sudokuapp.frontend.viewmodel

import androidx.lifecycle.ViewModel
import com.techatonic.sudokuapp.frontend.game.SudokuGame


class PlaySudokuViewModel: ViewModel() {
    val sudokuGame = SudokuGame()
}