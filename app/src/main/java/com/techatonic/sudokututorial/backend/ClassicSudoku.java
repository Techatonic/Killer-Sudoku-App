package com.techatonic.sudokututorial.backend;

import android.os.Build;
import android.util.Pair;


import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ClassicSudoku {

    static int attemptsToDo = 25;

    public static ClassicSudokuType GenerateSudoku(ClassicSudokuType sudoku){
        Pair<Boolean, List<List<Integer>>> result = Generate(sudoku.getGrid(), new int[]{0, 0});
        int[][] grid = new int[9][9];
        for(int row = 0; row < 9; row++){
            grid[row] = result.second.get(row).stream().mapToInt(i->i).toArray();
        }
        ClassicSudokuType generatedGrid = new ClassicSudokuType(sudoku.getType(), grid);

        return new ClassicSudokuType(sudoku.getType(), RemoveCells(generatedGrid), grid);
    }

    public static int[][] RemoveCells(ClassicSudokuType sudoku) {
        List<List<List<Integer>>> possibilities = new ArrayList<>();
        for(int x = 0; x < 9; x++){
            possibilities.add(new ArrayList<>());
            for(int y = 0; y < 9; y++){
                possibilities.get(x).add(new ArrayList<>(Arrays.asList(x, y)));
            }
        }
        int[][] grid = Remove(sudoku, possibilities);
        //System.out.println("\n\nSolutions Found: " + SolveSudoku(new classic.ClassicSudokuType(classic.ClassicSudokuType.SudokuType.Classic, grid), 0));
        return grid;
    }
    private static int[][] Remove(ClassicSudokuType sudoku, List<List<List<Integer>>> possibilities){
        int[][] grid;

        for (int attempt = 0; attempt < attemptsToDo; attempt++){
            grid = Arrays.stream(sudoku.getGrid()).map(int[]::clone).toArray(int[][]::new);
            //sudoku.PrintSudoku();

            int choiceRowIndex = new Random(System.currentTimeMillis()).nextInt(possibilities.size());
            List<List<Integer>> choiceRow = possibilities.get(choiceRowIndex);
            List<Integer> choice = choiceRow.get(new Random(System.currentTimeMillis()).nextInt(choiceRow.size()));
            grid[choice.get(0)][choice.get(1)] = 0;
            //System.out.println("[" + choice.get(0) + ", " + choice.get(1) + "]\n");

            int solutionsFound = SolveSudoku(new ClassicSudokuType(sudoku.getType(), Arrays.stream(sudoku.getGrid()).map(int[]::clone).toArray(int[][]::new)), 0);
            if (solutionsFound == 1) {
                possibilities.get(choiceRowIndex).remove(choiceRow.indexOf(choice));
                if (possibilities.get(choiceRowIndex).size() == 0) {
                    possibilities.remove(choiceRowIndex);
                }
                int[][] result = Remove(new ClassicSudokuType(sudoku.getType(), grid), possibilities);

                if (result == null) {
                    return sudoku.getGrid();
                } else{
                    return result;
                }
            }
        }
        return null;
    }

    public static Pair<Boolean, List<List<Integer>>> Generate(int[][] grid, int[] currPosition){
        grid = Arrays.stream(grid).map(int[]::clone).toArray(int[][]::new);
        List<Integer> possibilities = Arrays.stream(IntStream.range(1, 10).toArray()).boxed().collect(Collectors.toCollection(LinkedList::new));

        while(possibilities.size() > 0){
            int choice = possibilities.get(new Random(System.currentTimeMillis()).nextInt(possibilities.size()));
            grid[currPosition[0]][currPosition[1]] = choice;
            //new classic.ClassicSudokuType(classic.ClassicSudokuType.SudokuType.Classic, grid).PrintSudoku(); // Printing sudoku
            if(!ValidGrid(grid)){
                possibilities.remove(Integer.valueOf(choice));
                continue;
            }

            int[] nextPos;
            if(currPosition[1] < 8){
                nextPos = new int[]{currPosition[0], currPosition[1]+1};
            } else if(currPosition[0] < 8){
                nextPos = new int[]{currPosition[0]+1, 0};
            } else{
                return new Pair<>(true, Arrays.stream(grid).map(row -> IntStream.of(row).boxed().collect(Collectors.toList())).collect(Collectors.toList()));
            }

            Pair<Boolean, List<List<Integer>>> result = Generate(grid, nextPos);
            if(result.first){
                return result;
            } else{
                possibilities.remove(Integer.valueOf(choice));
            }
        }

        return new Pair<>(false, null);
    }



    static int SolveSudoku(ClassicSudokuType sudoku, int solutionsFound){
        int[][] grid = Arrays.stream(sudoku.getGrid()).map(int[]::clone).toArray(int[][]::new);

        int[] nextEmpty = null;
        for(int row=0; row < grid.length; row++){
            for(int col=0; col < grid[0].length; col++){
                if(grid[row][col] == 0){
                    nextEmpty = new int[]{row, col};
                    break;
                }
            }
            if(nextEmpty != null){
                break;
            }
        }

        if(nextEmpty == null){
            solutionsFound++;
            return solutionsFound;
        }

        for(int attempt=1; attempt < 10; attempt++){
            grid[nextEmpty[0]][nextEmpty[1]] = attempt;
            if(!ValidGrid(grid)){
                continue;
            }
            solutionsFound = SolveSudoku(new ClassicSudokuType(sudoku.getType(), grid), solutionsFound);
        }

        return solutionsFound;
    }

    protected static boolean ValidGrid(int[][] grid){
        // Checks valid rows
        for(int[] row : grid){
            int[] newRow = Arrays.stream(row).filter(x -> x != 0).toArray();
            Set<Integer> setRow = Arrays.stream(newRow).boxed().collect(Collectors.toSet());
            if(newRow.length != setRow.size()){
                return false;
            }
        }
        // Checks valid columns
        for(int col = 0; col < grid.length; col++){
            int[] column = new int[grid.length];
            for(int i = 0; i<grid.length; i++){
                column[i] = grid[i][col];
            }
            int[] newCol = Arrays.stream(column).filter(x -> x != 0).toArray();
            Set<Integer> setCol = Arrays.stream(newCol).boxed().collect(Collectors.toSet());
            if(newCol.length != setCol.size()){
                return false;
            }
        }
        // Checks valid boxes
        for(int boxX = 0; boxX < 3; boxX++){
            for(int boxY = 0; boxY < 3; boxY++){
                int[] values = new int[grid.length];
                for(int x=3*boxX; x < 3*boxX+3; x++){
                    for(int y=3*boxY; y < 3*boxY+3; y++){
                        values[3*(x%3)+(y%3)] = grid[x][y];
                    }
                }
                int[] newVals = Arrays.stream(values).filter(x -> x != 0).toArray();
                Set<Integer> setVals = Arrays.stream(values).boxed().filter(x -> x != 0).collect(Collectors.toSet());
                if(newVals.length != setVals.size()){
                    return false;
                }
            }
        }

        return true;
    }

}