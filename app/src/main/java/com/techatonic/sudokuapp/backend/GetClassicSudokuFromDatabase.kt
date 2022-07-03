package com.techatonic.sudokuapp.backend

import com.techatonic.sudokuapp.backend.sudokutypes.KillerSudokuType
import kotlin.Throws
import com.google.firebase.firestore.FirebaseFirestore
import android.content.ContentValues
import android.util.Log
import androidx.core.util.Pair
import com.google.android.gms.tasks.OnCompleteListener
import com.techatonic.sudokuapp.backend.sudokutypes.ClassicSudokuType
import com.google.firebase.firestore.CollectionReference
import com.techatonic.sudokuapp.frontend.game.SudokuGame
import java.util.*

class GetClassicSudokuFromDatabase {
    enum class SudokuType {
        Classic, Arrow, Thermo, Killer
    }

    private var collectionNameBySudokuType: Map<SudokuType, String> = mapOf(
        kotlin.Pair(SudokuType.Classic, "classicsudokus"),
        kotlin.Pair(SudokuType.Arrow, "arrowsudokus"),
        kotlin.Pair(SudokuType.Thermo, "thermosudokus"),
        kotlin.Pair(SudokuType.Killer, "killersudokus")
    )
    private var documentCount = 0
    private var sudoku: KillerSudokuType
    private var finishedSections = 0

    init {
        sudoku = KillerSudokuType()
    }

    @Throws(InterruptedException::class)
    fun retrieveClassicSudoku(sudokuGame: SudokuGame) {
        println("\n\nStart Function\n\n")
        val db = FirebaseFirestore.getInstance()

        //CountDownLatch countDownLatch = new CountDownLatch(1);
        println("Created countdown latch")
        // Get current id
        val documentReference =
            db.collection(collectionNameBySudokuType[SudokuType.Classic]!!).document("data")
        println("Created document reference")
        documentReference.get().addOnSuccessListener { document ->
            println("COMPLETED DOCUMENT GET")
            if (document.exists()) {
                val data = document.data
                Log.d(ContentValues.TAG, "DocumentSnapshot data: $data")
                val `val`: Int?
                if (data != null) {
                    `val` = (Objects.requireNonNull(data["documentCount"]) as Long).toInt()
                    documentCount = `val`
                    if (finishedSections == 0) {
                        finishedSections++
                        getSudokuFromDatabase(db, sudokuGame)
                    }
                } else {
                    Log.d(ContentValues.TAG, "Sudoku retrieval failed")
                }
            } else {
                Log.d(ContentValues.TAG, "No document found")
            }
        }
            .addOnFailureListener { println("FAILURE - UNABLE TO RETRIEVE DOCUMENT") }
            .addOnCanceledListener { println("FAILURE - UNABLE TO RETRIEVE DOCUMENT #1") }
        println("Ending function 1")
    }

    private fun getSudokuFromDatabase(db: FirebaseFirestore, sudokuGame: SudokuGame) {
        // Get random sudoku from database
        val random = Random()
        val choice = random.nextInt(documentCount)+1
        println("Document choice: $choice")
        val sudokuDocumentReference = db.collection(
            collectionNameBySudokuType[SudokuType.Classic]!!
        ).document("classicsudoku-$choice")
        sudokuDocumentReference.get().addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document.exists()) {
                    val data = document.data
                    Log.d(ContentValues.TAG, "DocumentSnapshot data: $data")
                    // Recreate sudoku
                    val grid = Array(9) { IntArray(9) }
                    val filledGrid = Array(9) { IntArray(9) }

                    // Grid
                    for (rowNum in 0..8) {
                        if (data == null || !data.containsKey("grid-row$rowNum")) {
                            println("Sudoku retrieval failed #1")
                            return@OnCompleteListener
                        }
                        val rowList = data["grid-row$rowNum"] as ArrayList<Long>?
                        val row: IntArray = if (rowList != null) {
                            rowList.stream().mapToInt { obj: Long -> obj.toInt() }.toArray()
                        } else {
                            println("Sudoku retrieval failed #2")
                            return@OnCompleteListener
                        }
                        grid[rowNum] = row
                    }
                    // Filled grid
                    for (rowNum in 0..8) {
                        if (!data!!.containsKey("filledGrid-row$rowNum")) {
                            println("Sudoku retrieval failed #3")
                            return@OnCompleteListener
                        }
                        val rowList = data["filledGrid-row$rowNum"] as ArrayList<Long>?
                        val row: IntArray = if (rowList != null) {
                            rowList.stream().mapToInt { obj: Long -> obj.toInt() }.toArray()
                        } else {
                            println("Sudoku retrieval failed #4")
                            return@OnCompleteListener
                        }
                        filledGrid[rowNum] = row
                    }
                    sudoku = KillerSudokuType(ClassicSudokuType.SudokuType.Killer, grid, filledGrid)
                    if (finishedSections == 1) {
                        finishedSections++

                        returnSudokuToFrontEnd(sudokuGame)
                    }
                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            } else {
                Log.d(ContentValues.TAG, "get failed with ", task.exception)
            }
        })
        println("Ending function 2")
    }


    private fun returnSudokuToFrontEnd(sudokuGame: SudokuGame) {
        println("\n\n\nReached the end")
        sudokuGame.classicSudokuGenerated(sudoku)
    }
}