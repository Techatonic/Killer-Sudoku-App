package com.techatonic.sudokuapp.backend.sudokutypes;

import androidx.core.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class KillerSudokuType extends ClassicSudokuType{
    private ArrayList<Pair<Integer, List<Pair<Integer, Integer>>>> cages = new ArrayList<>();

    public KillerSudokuType(SudokuType type, int[][] grid) {
        super(type, grid);
    }

    public KillerSudokuType() {
        super(SudokuType.Killer);
    }

    public KillerSudokuType(ClassicSudokuType sudoku) {
        super(sudoku);
    }

    public KillerSudokuType(int[][] grid) {
        super(grid);
    }

    public KillerSudokuType(SudokuType type, int[][] grid, ArrayList<Pair<Integer, List<Pair<Integer, Integer>>>> cages) {
        super(type, grid);
        this.cages = cages;
    }

    public KillerSudokuType(SudokuType type, ArrayList<Pair<Integer, List<Pair<Integer, Integer>>>> cages) {
        super(type);
        this.cages = cages;
    }

    public KillerSudokuType(ClassicSudokuType sudoku, ArrayList<Pair<Integer, List<Pair<Integer, Integer>>>> cages) {
        super(sudoku);
        this.cages = cages;
    }

    public KillerSudokuType(int[][] grid, ArrayList<Pair<Integer, List<Pair<Integer, Integer>>>> cages) {
        super(grid);
        this.cages = cages;
    }
    public KillerSudokuType(SudokuType type, ArrayList<Pair<Integer, List<Pair<Integer, Integer>>>> cages, int[][] filledGrid){
        super(type);
        this.setFilledGrid(filledGrid);
        this.cages = cages;
    }


    public KillerSudokuType(SudokuType type, int[][] unfilledGrid, int[][] filledGrid,
                            ArrayList<Pair<Integer, List<Pair<Integer, Integer>>>> cages) {
        super(type, unfilledGrid, filledGrid);
        this.cages = cages;
    }

    public KillerSudokuType(SudokuType sudokuType, int[][] unfilledGrid, int[][] filledGrid) {
        super(sudokuType, unfilledGrid, filledGrid);
    }

    public ArrayList<Pair<Integer, List<Pair<Integer, Integer>>>> getCages(){
        return cages;
    }

    public Pair<Integer, List<Pair<Integer, Integer>>> getCage(Pair<Integer, Integer> pos){
        for(Pair<Integer, List<Pair<Integer, Integer>>> cage : cages){
            if(cage.second.contains(pos)){
                return cage;
            }
        }
        return null;
    }
    public ArrayList<Pair<Integer, Integer>> getBox(Pair<Integer, Integer> pos){
        ArrayList<Pair<Integer, Integer>> result = new ArrayList<>();
        for(int row = pos.first/3*3; row < pos.first/3*3+3; row++){
            for(int col = pos.second/3*3; col < pos.second/3*3+3; col++){
                if(!(row == pos.first && col == pos.second)) {
                    result.add(new Pair<>(row, col));
                }
            }
        }
        return result;
    }
    public ArrayList<Pair<Integer, Integer>> getRow(Pair<Integer, Integer> pos){
        ArrayList<Pair<Integer, Integer>> result = new ArrayList<>();
        for(int col = 0; col < 9; col++){
            if(col != pos.second){
                result.add(new Pair<>(pos.first, col));
            }
        }
        return result;
    }
    public ArrayList<Pair<Integer, Integer>> getCol(Pair<Integer, Integer> pos){
        ArrayList<Pair<Integer, Integer>> result = new ArrayList<>();
        for(int row = 0; row < 9; row++){
            if(row != pos.first){
                result.add(new Pair<>(row, pos.second));
            }
        }
        return result;
    }

    public void setCages(ArrayList<Pair<Integer, List<Pair<Integer, Integer>>>> cages){
        this.cages = cages;
    }
    public void addCage(Pair<Integer, List<Pair<Integer, Integer>>> cage){
        this.cages.add(cage);
    }

    public void PrintSudoku() {
        for(int row=0; row < 9; row++){
            if(row % 3 == 0){
                System.out.println(new String(new char[22]).replace("\0", "-"));
            }
            for(int col=0; col < 9; col++){
                if(col % 3 == 0){
                    System.out.print("|");
                }
                if(this.getGrid()[row][col] == 0){
                    System.out.print("  ");
                } else{
                    System.out.print(" " + this.getGrid()[row][col]);
                }
                if(col == 8){
                    System.out.print("|\n");
                }
            }
        }
        System.out.println(new String(new char[22]).replace("\0", "-"));
    }

    public void PrintSudokuStats(){
        super.PrintSudokuStats(this.getGrid());
        //super.PrintSudokuStats();
        if(this.cages == null){
            System.out.println("Cages: null");
            return;
        }
        System.out.println("Num of cages: " + this.cages.size());
        for (Pair<Integer, List<Pair<Integer, Integer>>> cage : this.cages) {
            System.out.println("\nSum: " + cage.first);
            System.out.print("Points: ");
            for (Pair<Integer, Integer> point : cage.second) {
                System.out.print(point.toString() + "   ");
            }
            System.out.print("\n");
        }

    }
}
