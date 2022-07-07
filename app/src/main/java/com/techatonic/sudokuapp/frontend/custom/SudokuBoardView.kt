package com.techatonic.sudokuapp.frontend.custom

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.ContentInfoCompat
import com.techatonic.sudokuapp.R
import com.techatonic.sudokuapp.backend.sudokutypes.ClassicSudokuType
import com.techatonic.sudokuapp.frontend.game.Cell
import kotlin.math.min

class SudokuBoardView (context: Context, attributeSet: AttributeSet) : View(context, attributeSet){

    private var sqrtSize = 3
    private var size = 9

    private var sudokuType : ClassicSudokuType.SudokuType? = null

    // set in onDraw when we get a size of the view
    private var cellSizePixels = 0F
    private var noteSizePixels = 0F

    private var selectedRow = -1
    private var selectedCol = -1

    private var listener: OnTouchListener? = null

    private var cells: List<Cell>? = null

    private val thickLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context, R.color.opposite)
        strokeWidth = 4f
    }
    private val thinLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context, R.color.opposite)
        strokeWidth = 2f
    }
    private val selectedCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = ContextCompat.getColor(context, R.color.selectedPaint)
    }
    private val conflictingCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = ContextCompat.getColor(context, R.color.conflictingPaint)
    }
    private val textPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        //color = ContextCompat.getColor(context, R.color.opposite)
        color = ContextCompat.getColor(context, R.color.enteredTextPaint)
    }
    private val startingCellTextPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = ContextCompat.getColor(context, R.color.opposite)
        typeface = Typeface.DEFAULT_BOLD
    }
    private val noteTextPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = ContextCompat.getColor(context, R.color.opposite)
    }
    private val cageTextPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = ContextCompat.getColor(context, R.color.opposite)
        typeface = Typeface.DEFAULT_BOLD
    }

    // BEGIN Killer Sudokus
    var cages : List<Pair<Int, List<Pair<Int, Int>>>>? = null
    var cageOffset : Float = 0F // Set in onDraw
    // END Killer Sudokus



    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val sizePixels = min(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(sizePixels, sizePixels)
    }

    override fun onDraw(canvas: Canvas) {
        updateMeasurements(width)

        fillCells(canvas)
        drawLines(canvas)
        drawText(canvas)
        if(sudokuType == ClassicSudokuType.SudokuType.Killer){
            drawCages(canvas)
        }
    }

    private fun updateMeasurements(width: Int){
        cellSizePixels = (width/size).toFloat()
        noteSizePixels = cellSizePixels / sqrtSize.toFloat() / 1.5F
        noteTextPaint.textSize = cellSizePixels / sqrtSize.toFloat() / 1.5F
        textPaint.textSize = cellSizePixels / 1.5F
        startingCellTextPaint.textSize = cellSizePixels / 1.5F
        cageTextPaint.textSize = cellSizePixels / sqrtSize.toFloat() / 1.25F

        cageOffset = cellSizePixels / 10f
    }

    private fun fillCells(canvas: Canvas){
        cells?.forEach{
            val row = it.row
            val col = it.col

            if(row == selectedRow && col == selectedCol){
                fillCell(canvas, row, col, selectedCellPaint)
            } else if(row == selectedRow || col == selectedCol){
                fillCell(canvas, row, col, conflictingCellPaint)
            } else if(selectedRow != -1 && selectedCol != -1 &&
                      row / sqrtSize == selectedRow / sqrtSize &&
                      col / sqrtSize == selectedCol / sqrtSize){
                fillCell(canvas, row, col, conflictingCellPaint)
            }
        }
    }

    private fun fillCell(canvas: Canvas, row: Int, col: Int, paint: Paint){
        canvas.drawRect(
            col * cellSizePixels,
            row * cellSizePixels,
            (col+1)*cellSizePixels,
            (row+1)*cellSizePixels, paint
        )
    }

    private fun drawLines(canvas: Canvas){
        canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), thickLinePaint)
        for (i in 1 until size){
            val paintToUse = when (i % sqrtSize){
                0 -> thickLinePaint
                else -> thinLinePaint
            }
            canvas.drawLine(i * cellSizePixels, 0F, i * cellSizePixels, height.toFloat(), paintToUse)

            canvas.drawLine(0F, i * cellSizePixels, width.toFloat(), i * cellSizePixels, paintToUse)
        }
    }

    private fun drawText(canvas: Canvas){
        cells?.forEach { cell ->
            val value = cell.value
            val textBounds = Rect()

            if(value == 0){
                // Draw Notes
                cell.notes.forEach{ note ->
                    val rowInCell = (note-1) / sqrtSize
                    val colInCell = (note-1) % sqrtSize
                    val valueString = note.toString()
                    noteTextPaint.getTextBounds(valueString, 0, valueString.length, textBounds)
                    val textWidth = noteTextPaint.measureText(valueString) / 1.25F
                    val textHeight = textBounds.height()

                    canvas.drawText(
                        valueString, (cell.col * cellSizePixels) + (colInCell * noteSizePixels) + noteSizePixels - textWidth/2f,
                                     (cell.row * cellSizePixels) + (rowInCell * noteSizePixels) + noteSizePixels*3/2f + textHeight/2f,
                        noteTextPaint
                    )
                }
            } else {
                val row = cell.row
                val col = cell.col
                val valueString = value.toString()

                val paintToUse = if (cell.isStartingCell) startingCellTextPaint else textPaint

                paintToUse.getTextBounds(valueString, 0, valueString.length, textBounds)
                val textWidth = paintToUse.measureText(valueString)
                val textHeight = textBounds.height()

                canvas.drawText(
                    valueString, col * cellSizePixels + cellSizePixels / 2 - textWidth / 2,
                                 row * cellSizePixels + cellSizePixels / 2 + textHeight / 2,
                    paintToUse
                )
            }
        }
    }

    private fun drawCages(canvas: Canvas) {
        this.cages?.forEach { cage ->

            val cells = cage.second.sortedWith(compareBy({it.first}, {it.second}))
            //val cells = cage.second
            for((index, cell) in cells.withIndex()){
                val textBounds = Rect()
                if(index == 0){
                    val valueString = cage.first.toString()
                    cageTextPaint.getTextBounds(valueString, 0, valueString.length, textBounds)
                    val textWidth = noteTextPaint.measureText(valueString) / 1.25F
                    val textHeight = textBounds.height()

                    canvas.drawText(
                        valueString, (cell.second * cellSizePixels) + noteSizePixels / 2 - textWidth*1/4f,
                                     (cell.first * cellSizePixels) + noteSizePixels / 2 + textHeight*5/8f,
                        cageTextPaint
                    )
                }

                // Right line
                if(!cells.any { it.first == cell.first && it.second == cell.second+1 }){
                    val startY = when(cells.any { it.first == cell.first-1 && it.second == cell.second }){
                        true -> cell.first * cellSizePixels
                        false -> cell.first * cellSizePixels + cageOffset
                    }
                    val stopY = when(cells.any { it.first == cell.first+1 && it.second == cell.second}){
                        true ->(cell.first+1) * cellSizePixels
                        false -> (cell.first+1) * cellSizePixels - cageOffset
                    }

                    canvas.drawLine(
                        (cell.second+1) * cellSizePixels - cageOffset,
                        startY,
                        (cell.second+1) * cellSizePixels - cageOffset,
                        stopY,
                        thinLinePaint
                    )
                }
                // Bottom line
                if(!cells.any { it.first == cell.first+1 && it.second == cell.second }){
                    val startX = when(cells.any { it.first == cell.first && it.second == cell.second-1 }){
                        true -> cell.second * cellSizePixels
                        false -> cell.second * cellSizePixels + cageOffset
                    }
                    val stopX = when(cells.any { it.first == cell.first && it.second == cell.second+1}){
                        true ->(cell.second+1) * cellSizePixels
                        false -> (cell.second+1) * cellSizePixels - cageOffset
                    }
                    canvas.drawLine(
                        startX,
                        (cell.first+1) * cellSizePixels - cageOffset,
                        stopX,
                        (cell.first+1) * cellSizePixels - cageOffset,
                        thinLinePaint
                    )
                }
                // Top line
                if(!cells.any { it.first == cell.first-1 && it.second == cell.second }){
                    val startX = when (index) {
                        0 -> cell.second * cellSizePixels + noteSizePixels / 2 + textBounds.width()
                        else -> when(cells.any { it.first == cell.first && it.second == cell.second-1}){
                            true -> cell.second * cellSizePixels
                            false -> cell.second * cellSizePixels + cageOffset
                        }
                    }
                    val stopX = when(cells.any { it.first == cell.first && it.second == cell.second+1}){
                        true -> (cell.second+1) * cellSizePixels
                        false -> (cell.second+1) * cellSizePixels - cageOffset
                    }
                    canvas.drawLine(
                        startX,
                        cell.first * cellSizePixels + cageOffset,
                        stopX,
                        cell.first * cellSizePixels + cageOffset,
                        thinLinePaint
                    )
                }
                // Left line
                if(!cells.any { it.first == cell.first && it.second == cell.second-1 }){
                    val startY = when (index) {
                        0 -> cell.first * cellSizePixels + noteSizePixels / 2 + textBounds.height()
                        else -> when(cells.any { it.first == cell.first-1 && it.second == cell.second }){
                            true -> cell.first * cellSizePixels
                            false -> cell.first * cellSizePixels + cageOffset
                        }
                    }
                    val stopY = when(cells.any { it.first == cell.first+1 && it.second == cell.second}){
                        true ->(cell.first+1) * cellSizePixels
                        false -> (cell.first+1) * cellSizePixels - cageOffset
                    }
                    canvas.drawLine(
                        cell.second * cellSizePixels + cageOffset,
                        startY,
                        cell.second * cellSizePixels + cageOffset,
                        stopY,
                        thinLinePaint
                    )
                }

                // CORNERS
                // Top-right corner
                if( cells.any { it.first == cell.first-1 && it.second == cell.second } &&
                    cells.any { it.first == cell.first && it.second == cell.second+1 } &&
                    !cells.any { it.first == cell.first-1 && it.second == cell.second+1 }){
                    // Top line
                    canvas.drawLine(
                        (cell.second+1) * cellSizePixels - cageOffset,
                        cell.first * cellSizePixels + cageOffset,
                        (cell.second+1) * cellSizePixels,
                        cell.first * cellSizePixels + cageOffset,
                        thinLinePaint
                    )
                    // Right line
                    canvas.drawLine(
                        (cell.second+1) * cellSizePixels - cageOffset,
                        cell.first * cellSizePixels,
                        (cell.second+1) * cellSizePixels - cageOffset,
                        cell.first * cellSizePixels + cageOffset,
                        thinLinePaint
                    )
                }

                // Bottom-right corner
                if( cells.any { it.first == cell.first+1 && it.second == cell.second } &&
                    cells.any { it.first == cell.first && it.second == cell.second+1 } &&
                    !cells.any { it.first == cell.first+1 && it.second == cell.second+1 }){
                    // Bottom line
                    canvas.drawLine(
                        (cell.second+1) * cellSizePixels - cageOffset,
                        (cell.first+1) * cellSizePixels - cageOffset,
                        (cell.second+1) * cellSizePixels,
                        (cell.first+1) * cellSizePixels - cageOffset,
                        thinLinePaint
                    )
                    // Right line
                    canvas.drawLine(
                        (cell.second+1) * cellSizePixels - cageOffset,
                        (cell.first+1) * cellSizePixels - cageOffset,
                        (cell.second+1) * cellSizePixels - cageOffset,
                        (cell.first+1) * cellSizePixels,
                        thinLinePaint
                    )
                }

                // Bottom-left corner
                if( cells.any { it.first == cell.first+1 && it.second == cell.second } &&
                    cells.any { it.first == cell.first && it.second == cell.second-1 } &&
                    !cells.any { it.first == cell.first+1 && it.second == cell.second-1 }){
                    // Bottom line
                    canvas.drawLine(
                        cell.second * cellSizePixels,
                        (cell.first+1) * cellSizePixels - cageOffset,
                        cell.second * cellSizePixels + cageOffset,
                        (cell.first+1) * cellSizePixels - cageOffset,
                        thinLinePaint
                    )
                    // Left line
                    canvas.drawLine(
                        cell.second * cellSizePixels + cageOffset,
                        (cell.first+1) * cellSizePixels - cageOffset,
                        cell.second * cellSizePixels + cageOffset,
                        (cell.first+1) * cellSizePixels,
                        thinLinePaint
                    )
                }

                // Top-left corner
                if( cells.any { it.first == cell.first-1 && it.second == cell.second } &&
                    cells.any { it.first == cell.first && it.second == cell.second-1 } &&
                    !cells.any { it.first == cell.first-1 && it.second == cell.second-1 }){
                    // Top line
                    canvas.drawLine(
                        cell.second * cellSizePixels,
                        cell.first * cellSizePixels + cageOffset,
                        cell.second * cellSizePixels + cageOffset,
                        cell.first * cellSizePixels + cageOffset,
                        thinLinePaint
                    )
                    // Left line
                    canvas.drawLine(
                        cell.second * cellSizePixels + cageOffset,
                        cell.first * cellSizePixels,
                        cell.second * cellSizePixels + cageOffset,
                        cell.first * cellSizePixels + cageOffset,
                        thinLinePaint
                    )
                }

            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when(event.action){
            MotionEvent.ACTION_DOWN -> {
                handleTouchEvent(event.x, event.y)
                true
            }
            else -> false
        }
    }

    private fun handleTouchEvent(x: Float, y:Float){
        val possibleSelectedRow = (y / cellSizePixels).toInt()
        val possibleSelectedCol = (x / cellSizePixels).toInt()
        listener?.onCellTouched(possibleSelectedRow, possibleSelectedCol)
    }

    fun updateCells(cells: List<Cell>){
        this.cells = cells
        invalidate()
    }

    fun updateSelectedCellUI(row: Int, col: Int){
        selectedRow = row
        selectedCol = col
        invalidate()
    }

    fun updateSudokuType(sudokuType: ClassicSudokuType.SudokuType){
        this.sudokuType = sudokuType
        println(this.sudokuType)
    }

    fun registerListener(listener: OnTouchListener){
        this.listener = listener
    }

    fun setKillerCages(cages: List<Pair<Int, List<Pair<Int, Int>>>>?) {
        this.cages = cages
    }

    interface OnTouchListener {
        fun onCellTouched(row: Int, col: Int)
    }


}