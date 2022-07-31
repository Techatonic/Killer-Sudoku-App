package com.techatonic.sudokuapp.frontend.game

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.techatonic.sudokuapp.backend.sudoku.RetrieveSudoku
import com.techatonic.sudokuapp.backend.staticdata.Settings
import com.techatonic.sudokuapp.backend.sudoku.sudokutypes.ClassicSudokuType
import com.techatonic.sudokuapp.backend.sudoku.sudokutypes.KillerSudokuType
import com.techatonic.sudokuapp.frontend.custom.PlaySudokuActivity
import kotlinx.android.synthetic.main.activity_play_sudoku.*

class SudokuGame(private val playSudokuActivity: PlaySudokuActivity) {

    var selectedCellLiveData = MutableLiveData<Pair<Int, Int>>()
    var cellsLiveData = MutableLiveData<List<Cell>>()
    val isTakingNotesLiveData = MutableLiveData<Boolean>()
    val highlightedKeysLiveData = MutableLiveData<Set<Int>>()
    val sudokuTypeLiveData = MutableLiveData<ClassicSudokuType.SudokuType>()
    // Killer
    val killerCagesLiveData = MutableLiveData<List<Pair<Int, List<Pair<Int, Int>>>>>()

    private var selectedRow = -1
    private var selectedCol = -1
    private var isTakingNotes = false

    private lateinit var board:Board

    init {

        when(Settings.selectedSudokuType){
            ClassicSudokuType.SudokuType.Classic -> RetrieveSudoku.retrieveClassicSudoku(this)
            ClassicSudokuType.SudokuType.Killer -> RetrieveSudoku.retrieveKillerSudoku(this)
            else -> RetrieveSudoku.retrieveClassicSudoku(this)
        }
    }

    fun classicSudokuGenerated(generatedSudoku: ClassicSudokuType) {
        playSudokuActivity.progressBar.visibility = View.INVISIBLE

        val sudoku: ClassicSudokuType = generatedSudoku

        val cells = mutableListOf<Cell>()

        for (row in 0..8){
            for(col in 0..8){
                if(sudoku.grid[row][col] == 0){
                    cells.add(Cell(row, col, sudoku.grid[row][col], false))
                } else{
                    cells.add(Cell(row, col, sudoku.grid[row][col], true))
                }
            }
        }

        board = Board(9, cells)

        selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
        cellsLiveData.postValue(board.cells)
        isTakingNotesLiveData.postValue(isTakingNotes)
        sudokuTypeLiveData.postValue(ClassicSudokuType.SudokuType.Classic)
    }


    fun killerSudokuGenerated(generatedSudoku: KillerSudokuType){
        playSudokuActivity.progressBar.visibility = View.INVISIBLE
        playSudokuActivity.sudokuBoardView.visibility = View.VISIBLE

        val sudoku: KillerSudokuType = generatedSudoku

        val cells = mutableListOf<Cell>()

        for (row in 0..8){
            for(col in 0..8){
                if(sudoku.grid[row][col] == 0){
                    cells.add(Cell(row, col, sudoku.grid[row][col], false))
                } else{
                    cells.add(Cell(row, col, sudoku.grid[row][col], true))
                }
            }
        }

        board = Board(9, cells, sudoku.cages)

        selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
        cellsLiveData.postValue(board.cells)
        isTakingNotesLiveData.postValue(isTakingNotes)
        sudokuTypeLiveData.postValue(ClassicSudokuType.SudokuType.Killer)
        killerCagesLiveData.postValue(sudoku.cages)
    }


    fun handleInput(number: Int){
        if (!this::board.isInitialized){
            return
        }

        if(selectedRow == -1 || selectedCol == -1) return
        val cell = board.getCell(selectedRow, selectedCol)
        if(cell.isStartingCell) return

        if(isTakingNotes){
            if(cell.notes.contains(number)){
                cell.notes.remove(number)
            } else{
                cell.notes.add(number)
            }
            highlightedKeysLiveData.postValue(cell.notes)
        } else {
            cell.value = number
            cell.isValid = true
            //TODO Check validity
            val validBoard: Boolean = CheckValidBoard.isValidKillerGrid(board)
            if(!validBoard){
                cell.isValid = false
            }
        }
        cellsLiveData.postValue(board.cells)
    }

    fun updateSelectedCell(row:Int, col:Int){
        if (!this::board.isInitialized){
            return
        }

        val cell = board.getCell(row, col)
        if(cell.isStartingCell){
            return
        }
        selectedRow = row
        selectedCol = col
        selectedCellLiveData.postValue(Pair(row, col))

        if(isTakingNotes){
            highlightedKeysLiveData.postValue(cell.notes)
        }
    }

    fun changeNoteTakingState(){
        isTakingNotes = !isTakingNotes
        isTakingNotesLiveData.postValue(isTakingNotes)
        if(selectedRow == -1 || selectedCol == -1){
            return
        }

        val curNotes = if(isTakingNotes) board.getCell(selectedRow, selectedCol).notes else setOf()
        highlightedKeysLiveData.postValue(curNotes)
    }

    fun delete(){
        if (!this::board.isInitialized){
            return
        }
        if(selectedRow == -1 || selectedCol == -1){
            return
        }

        val cell = board.getCell(selectedRow, selectedCol)
        if(isTakingNotes){
            cell.notes.clear()
            highlightedKeysLiveData.postValue(setOf())
        } else{
            cell.value = 0
            cell.isValid = true
        }
        cellsLiveData.postValue(board.cells)
    }

    fun clearSelectedCell() {
        selectedRow = -1
        selectedCol = -1
        selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
    }

}