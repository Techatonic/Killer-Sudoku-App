package com.techatonic.sudokuapp.frontend.custom

import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import com.techatonic.sudokuapp.R
import com.techatonic.sudokuapp.backend.sudoku.sudokutypes.ClassicSudokuType
import com.techatonic.sudokuapp.frontend.game.Cell
import com.techatonic.sudokuapp.frontend.viewmodel.PlaySudokuViewModel
import kotlinx.android.synthetic.main.activity_play_sudoku.*

class PlaySudokuActivity : AppCompatActivity(), SudokuBoardView.OnTouchListener {

    private lateinit var viewModel: PlaySudokuViewModel
    private lateinit var numberButtons: List<Button>

    lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_sudoku)

        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE

        // Set back button in app bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //

        sudokuBoardView.registerListener(this)

        viewModel =
            PlaySudokuViewModel(this) //ViewModelProvider(this)//[PlaySudokuViewModel::class.java]
        viewModel.sudokuGame.selectedCellLiveData.observe(this) { updateSelectedCellUI(it) }
        viewModel.sudokuGame.cellsLiveData.observe(this) { updateCells(it) }
        viewModel.sudokuGame.isTakingNotesLiveData.observe(this) { updateNoteTakingUI(it) }
        viewModel.sudokuGame.highlightedKeysLiveData.observe(this) { updateHighlightedKeys(it) }
        viewModel.sudokuGame.sudokuTypeLiveData.observe(this) { updateSudokuType(it) }
        viewModel.sudokuGame.killerCagesLiveData.observe(this) { setKillerCages(it) }

        numberButtons = listOf(
            oneButton,
            twoButton,
            threeButton,
            fourButton,
            fiveButton,
            sixButton,
            sevenButton,
            eightButton,
            nineButton
        )
        numberButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                viewModel.sudokuGame.handleInput(index + 1)
            }
        }
        notesButton.setOnClickListener { viewModel.sudokuGame.changeNoteTakingState() }
        deleteButton.setOnClickListener { viewModel.sudokuGame.delete() }
        playScreen.setOnClickListener { viewModel.sudokuGame.clearSelectedCell() }
    }

    private fun updateNoteTakingUI(isNoteTaking: Boolean?) = isNoteTaking?.let {
        val color = if (it) ContextCompat.getColor(this, R.color.colorAccent) else Color.LTGRAY
        notesButton.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
    }
    private fun updateHighlightedKeys(set: Set<Int>?) = set?.let {
    }

    private fun updateCells(cells: List<Cell>?) = cells?.let {
        sudokuBoardView.updateCells(cells)
    }

    private fun updateSelectedCellUI(cell: Pair<Int, Int>?) = cell?.let {
        sudokuBoardView.updateSelectedCellUI(cell.first, cell.second)
    }

    private fun updateSudokuType(sudokuType: ClassicSudokuType.SudokuType?) = sudokuType?.let {
        sudokuBoardView.updateSudokuType(sudokuType)
    }

    private fun setKillerCages(cages: List<Pair<Int, List<Pair<Int, Int>>>>?) {
        sudokuBoardView.setKillerCages(cages)
    }

    override fun onCellTouched(row: Int, col: Int) {
        viewModel.sudokuGame.updateSelectedCell(row, col)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> println(item.itemId)
        }
        return super.onContextItemSelected(item)
    }

}