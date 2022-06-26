package com.techatonic.sudokututorial.backend;

import com.techatonic.sudokututorial.backend.classic.ClassicSudoku;
import com.techatonic.sudokututorial.backend.classic.ClassicSudokuType;
import com.techatonic.sudokututorial.backend.killer.KillerSudoku;
import com.techatonic.sudokututorial.backend.killer.KillerSudokuType;

public class CreateSudoku {

    public static ClassicSudokuType createClassicSudoku(){
        return ClassicSudoku.GenerateSudoku(new ClassicSudokuType());
    }
    public static KillerSudokuType createKillerSudoku(){
        return KillerSudoku.GenerateSudoku(new KillerSudokuType());
    }

}
