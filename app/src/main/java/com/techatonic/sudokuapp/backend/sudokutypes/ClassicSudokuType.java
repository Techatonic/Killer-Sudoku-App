package com.techatonic.sudokuapp.backend.sudokutypes;

public class ClassicSudokuType {


    public enum SudokuType{
        Classic,
        Arrow,
        Thermo,
        Killer
    }


    private SudokuType type;
    private int[][] grid;
    private int[][] filledGrid;

    public ClassicSudokuType(){
        this.type = SudokuType.Classic;
        this.grid = new int[9][9];
    }
    public ClassicSudokuType(SudokuType type, int[][] grid){
        this.type = type;
        this.grid = grid;
    }
    public ClassicSudokuType(int[][] grid) {
        this.type = SudokuType.Classic;
        this.grid = grid;
    }
    public ClassicSudokuType(SudokuType type){
        this.type = type;
        this.grid = new int[9][9];
    }
    public ClassicSudokuType(ClassicSudokuType sudoku){
        this.type = sudoku.type;
        this.grid = sudoku.grid;
        this.filledGrid = sudoku.filledGrid;
    }
    public ClassicSudokuType(ClassicSudokuType.SudokuType type, int[][] unfilledGrid, int[][] filledGrid) {
        this.type = type;
        this.grid = unfilledGrid;
        this.filledGrid = filledGrid;
    }


    public SudokuType getType() {
        return type;
    }

    public int[][] getGrid() {
        return grid;
    }
    public int[][] getFilledGrid(){
        return filledGrid;
    }

    public void setPosition(int row, int col, int val){
        grid[row][col] = val;
    }

    public void setFilledGrid(int[][] filledGrid) { this.filledGrid = filledGrid; }

    public void PrintSudoku() {
        PrintSudoku(this.grid);
    }

    public void PrintSudoku(int[][] grid){
        for(int row=0; row < 9; row++){
            if(row % 3 == 0){
                System.out.println(new String(new char[22]).replace("\0", "-"));
            }
            for(int col=0; col < 9; col++){
                if(col % 3 == 0){
                    System.out.print("|");
                }
                if(grid[row][col] == 0){
                    System.out.print("  ");
                } else{
                    System.out.print(" " + grid[row][col]);
                }
                if(col == 8){
                    System.out.print("|\n");
                }
            }
        }
        System.out.println(new String(new char[22]).replace("\0", "-"));
        //System.out.println("\n");
    }

    public void PrintSudokuStats(){
        PrintSudokuStats(this.grid);
    }
    public void PrintSudokuStats(int[][] grid){
        System.out.println("Number of cells given: " + GetCellsGiven());
    }
    public int GetCellsGiven(){
        int cellsGiven = 0;

        for(int[] row : grid){
            for(int cell : row){
                if(cell != 0){
                    cellsGiven++;
                }
            }
        }
        return cellsGiven;
    }
}