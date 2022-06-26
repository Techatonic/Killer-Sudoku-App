package com.techatonic.sudokututorial.backend;

import com.techatonic.sudokututorial.frontend.game.Cell;
import com.techatonic.sudokututorial.frontend.game.SudokuGame;
import com.techatonic.sudokututorial.frontend.viewmodel.PlaySudokuViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class CreateSudoku {

    public static ClassicSudokuType createSudoku(){
        return ClassicSudoku.GenerateSudoku(new ClassicSudokuType());
    }


}
