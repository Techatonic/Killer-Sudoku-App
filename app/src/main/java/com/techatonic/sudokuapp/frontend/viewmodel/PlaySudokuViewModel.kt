package com.techatonic.sudokuapp.frontend.viewmodel

import androidx.lifecycle.ViewModel
import com.techatonic.sudokuapp.frontend.custom.PlaySudokuActivity
import com.techatonic.sudokuapp.frontend.game.SudokuGame

class PlaySudokuViewModel(playSudokuActivity: PlaySudokuActivity) : ViewModel() {
    val sudokuGame = SudokuGame(playSudokuActivity)
}
