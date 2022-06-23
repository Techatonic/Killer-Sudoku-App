package com.techatonic.sudokututorial.view.custom

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.techatonic.sudokututorial.R
import com.techatonic.sudokututorial.game.Cell
import com.techatonic.sudokututorial.viewmodel.PlaySudokuViewModel
import kotlinx.android.synthetic.main.activity_play_sudoku.*

class PlaySudokuActivity : AppCompatActivity(), SudokuBoardView.OnTouchListener {

    private lateinit var viewModel: PlaySudokuViewModel
    private lateinit var numberButtons: List<Button>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_sudoku)

        sudokuBoardView.registerListener(this)

        viewModel = ViewModelProvider(this).get(PlaySudokuViewModel::class.java)
        viewModel.sudokuGame.selectedCellLiveData.observe(this) { updateSelectedCellUI(it) }
        viewModel.sudokuGame.cellsLiveData.observe(this) { updateCells(it) }
        viewModel.sudokuGame.isTakingNotesLiveData.observe(this) {updateNoteTakingUI(it)}
        viewModel.sudokuGame.highlightedKeysLiveData.observe(this) {updateHighlightedKeys(it)}

        numberButtons = listOf(oneButton, twoButton, threeButton, fourButton, fiveButton, sixButton, sevenButton, eightButton, nineButton)
        numberButtons.forEachIndexed{ index, button ->
            button.setOnClickListener {
                viewModel.sudokuGame.handleInput(index+1)
            }
        }
        notesButton.setOnClickListener{viewModel.sudokuGame.changeNoteTakingState()}
    }

    private fun updateNoteTakingUI(isNoteTaking: Boolean?) = isNoteTaking?.let {
        if(it){
            notesButton.setBackgroundColor(ContextCompat.getColor(this, com.google.android.material.R.color.design_default_color_primary))
        } else{
            notesButton.setBackgroundColor(Color.LTGRAY)
        }
    }
    private fun updateHighlightedKeys(set: Set<Int>?) = set?.let {
        // I disagreed with the tutorial on doing this

        //numberButtons.forEachIndexed { index, button ->
            //val color = if(set.contains(index+1)) ContextCompat.getColor(this, com.google.android.material.R.color.design_default_color_primary) else Color.LTGRAY
        //    button.setBackgroundColor(com.google.android.material.R.color.design_default_color_primary)
        //}
    }

    private fun updateCells(cells: List<Cell>?) = cells?.let {
        sudokuBoardView.updateCells(cells)
    }

    private fun updateSelectedCellUI(cell: Pair<Int, Int>?) = cell?.let {
        sudokuBoardView.updateSelectedCellUI(cell.first, cell.second)
    }

    override fun onCellTouched(row: Int, col: Int) {
        viewModel.sudokuGame.updateSelectedCell(row, col)
    }

}