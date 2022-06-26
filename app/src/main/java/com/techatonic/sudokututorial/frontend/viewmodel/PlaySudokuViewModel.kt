package com.techatonic.sudokututorial.frontend.viewmodel

import androidx.lifecycle.ViewModel
import com.techatonic.sudokututorial.frontend.game.SudokuGame


class PlaySudokuViewModel: ViewModel() {
    val sudokuGame = SudokuGame()
}