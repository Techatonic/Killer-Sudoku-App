package com.techatonic.sudokututorial.viewmodel

import androidx.lifecycle.ViewModel
import com.techatonic.sudokututorial.game.SudokuGame


class PlaySudokuViewModel : ViewModel() {

    val sudokuGame = SudokuGame()

}