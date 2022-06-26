package com.techatonic.sudokututorial.frontend.custom

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.techatonic.sudokututorial.R
import com.techatonic.sudokututorial.frontend.game.Cell
import kotlin.math.min

class SudokuBoardView (context: Context, attributeSet: AttributeSet) : View(context, attributeSet){

    private var sqrtSize = 3
    private var size = 9

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
    }

    private fun updateMeasurements(width: Int){
        cellSizePixels = (width/size).toFloat()
        noteSizePixels = cellSizePixels / sqrtSize.toFloat()
        noteTextPaint.textSize = cellSizePixels / sqrtSize.toFloat()
        textPaint.textSize = cellSizePixels / 1.5F
        startingCellTextPaint.textSize = cellSizePixels / 1.5F
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
                    val textWidth = noteTextPaint.measureText(valueString)
                    val textHeight = textBounds.height()

                    canvas.drawText(
                        valueString, (cell.col * cellSizePixels) + (colInCell * noteSizePixels) + noteSizePixels / 2 - textWidth / 2f,
                                     (cell.row * cellSizePixels) + (rowInCell * noteSizePixels) + noteSizePixels / 2 + textHeight / 2f,
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
    fun registerListener(listener: OnTouchListener){
        this.listener = listener
    }

    interface OnTouchListener {
        fun onCellTouched(row: Int, col: Int)
    }


}