package com.techatonic.sudokututorial.backend.killer;

import androidx.core.util.Pair;

import com.techatonic.sudokututorial.backend.classic.ClassicSudoku;

import java.util.*;
import java.util.stream.Collectors;


public class KillerSudoku extends ClassicSudoku {

    static int maxCageSize = 5;

    public static KillerSudokuType GenerateSudoku(KillerSudokuType sudoku){
        Pair<Boolean, List<List<Integer>>> result = Generate(sudoku.getGrid(), new int[]{0, 0});
        int[][] grid = new int[9][9];
        for(int row = 0; row < 9; row++){
            grid[row] = result.second.get(row).stream().mapToInt(i->i).toArray();
        }

        int attempt = 1;
        System.out.println();
        while(true) {
            System.out.println("\nAttempt #" + attempt++);
            final long startTime = System.currentTimeMillis();
            ArrayList<Pair<Integer, List<Pair<Integer, Integer>>>> cages;
            try {
                cages = AddCages(new KillerSudokuType(sudoku.getType(), grid, new ArrayList<>()), new ArrayList<>());
            } catch (CageDuplicateValueException e){
                System.out.println(e.getMessage());
                final long endTime = System.currentTimeMillis();
                final double totalTime = (double) (endTime - startTime) / 1000;
                System.out.println("Attempt execution time: " + totalTime + " seconds");
                continue;
            }

            KillerSudokuType killerGrid = new KillerSudokuType(sudoku.getType(), cages);
            int solutions = SolveSudoku(killerGrid, 0, GeneratePossibilitiesPerCell(), false);
            System.out.println("Solutions Found: " + solutions);
            final long endTime = System.currentTimeMillis();
            final double totalTime = (double) (endTime - startTime) / 1000;
            System.out.println("Attempt execution time: " + totalTime + " seconds");
            if(solutions == 1) {
                return new KillerSudokuType(sudoku.getType(), new int[9][9], grid, cages);
            }
        }

    }
    @SuppressWarnings("unchecked")
    public static Pair<Boolean, Pair<Integer, Integer>> CheckValidFromCutOff(ArrayList<Pair<Integer, Integer>> cellsUsed, ArrayList<Pair<Integer, Integer>> points, Pair<Integer, Integer> newPoint){
        ArrayList<Pair<Integer, Integer>> used = (ArrayList<Pair<Integer, Integer>>) cellsUsed.clone();
        used.addAll(points);
        used.add(newPoint);


        int maxRow = Math.min(used.stream().mapToInt(x -> x.first).max().orElse(0) + 2, 9);

        //System.out.println("\n\n" + cellsUsed);
        //System.out.println(maxY);

        for(int row = 0; row < maxRow; row++){
            for(int column = 0; column < 9; column++){
                Pair<Integer, Integer> pos = new Pair<>(row, column);
                if(used.contains(pos)){
                    continue;
                }
                // Check if all surrounded cells have been used
                boolean allUsed = true;
                for(int drow = -1; drow <= 1; drow++){
                    for(int dcolumn = -1; dcolumn <=1; dcolumn++){
                        if(row+drow < 0 || row+drow > 8 || column+dcolumn < 0 || column+dcolumn > 8 || (drow != 0 && dcolumn != 0) || (drow == 0 && dcolumn == 0)){
                            continue;
                        }
                        if(!used.contains(new Pair<>(row+drow, column+dcolumn))){
                            //System.out.println("For " + pos + " , " + new Pair<>(row+drow, column+dcolumn) + " is available");
                            allUsed = false;
                            break;
                        }
                    }
                    if(!allUsed){
                        break;
                    }
                }

                if(allUsed){
                    return new Pair<>(false, pos);
                }
            }
        }
        return new Pair<>(true, null);
    }

    @SuppressWarnings("unchecked")
    private static ArrayList<Pair<Integer, List<Pair<Integer, Integer>>>> AddCages(KillerSudokuType sudoku, ArrayList<Pair<Integer, Integer>> cellsUsed) throws CageDuplicateValueException {
        ArrayList<Pair<Integer, List<Pair<Integer, Integer>>>> cages = (ArrayList<Pair<Integer, List<Pair<Integer, Integer>>>>) sudoku.getCages().clone();

        // Find next free cell for start position
        Pair<Integer, Integer> startPosition = null;
        for(int x = 0; x < 9; x++){
            boolean found = false;
            for(int y = 0; y < 9; y++){
                if(!cellsUsed.contains(new Pair<>(x, y))) {
                    found = true;
                    startPosition = new Pair<>(x, y);
                    break;
                }
            }
            if(found){
                break;
            }
        }

        if(startPosition == null){
            return null;
        }
        //System.out.println("Start Position: " + startPosition);

        int attemptsToDo = 100;
        while(attemptsToDo-- > 0) {
            //System.out.println("\nAttempts Left: " + attemptsToDo);

            ArrayList<Pair<Integer, Integer>> points = new ArrayList<>();
            ArrayList<Integer> values = new ArrayList<>();
            points.add(startPosition);
            values.add(sudoku.getGrid()[startPosition.first][startPosition.second]);
            Pair<Integer, Integer> mostRecentPoint = new Pair<>(startPosition.first, startPosition.second);

            ArrayList<Pair<Integer, Integer>> possibilities = new ArrayList<>();

            // To be used when we have a trapped point that needs to be picked next
            Pair<Integer, Integer> forcedPoint = null;

            int counter = 1000;
            while (--counter > 0) {
                Pair<Integer, Integer> point;
                if(forcedPoint != null){
                    point = new Pair<>(forcedPoint.first, forcedPoint.second);
                    if(values.contains(sudoku.getGrid()[point.first][point.second])){
                        throw new CageDuplicateValueException("Duplicate value in cage");
                    }
                } else {
                    possibilities.clear();
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dy = -1; dy <= 1; dy++) {
                            int x = mostRecentPoint.first + dx;
                            int y = mostRecentPoint.second + dy;
                            if (x >= 0 && x <= 8 && y >= 0 && y <= 8 && (dx == 0 || dy == 0) && !(dx == 0 && dy == 0) && (!cellsUsed.contains(new Pair<>(x, y))) && (!points.contains(new Pair<>(x, y))) && (!values.contains(sudoku.getGrid()[x][y]))) {
                                possibilities.add(new Pair<>(x, y));
                            }
                        }
                    }
                    if (possibilities.size() == 0) {
                        break;
                    }
                    int randomIndex = new Random().nextInt(possibilities.size());
                    point = possibilities.get(randomIndex);
                }

                Pair<Boolean, Pair<Integer, Integer>> checkCutOff = CheckValidFromCutOff(cellsUsed, points, point);
                if(!checkCutOff.first){
                    forcedPoint = checkCutOff.second;
                } else{
                    forcedPoint = null;
                }

                points.add(point);
                values.add(sudoku.getGrid()[point.first][point.second]);
                mostRecentPoint = new Pair<>(point.first, point.second);

                if(forcedPoint != null){
                    continue;
                }

                int randomNum = new Random().nextInt(10);
                if(randomNum <= 3 && points.size() == 2){
                    break;
                } else if(randomNum > 3 && randomNum <= 7 && points.size() == 3){
                    break;
                }else if(randomNum > 7 && points.size() > 1){
                    break;
                }else if(points.size() >= maxCageSize){
                    break;
                }

            }
            if(counter == 0){
                int value = sudoku.getGrid()[startPosition.first][startPosition.second];
                Pair<Integer, Integer> cells = new Pair<>(startPosition.first, startPosition.second);
                ArrayList<Pair<Integer, Integer>> cellsList = (ArrayList<Pair<Integer, Integer>>) List.of(cells);
                cages.add(new Pair<>(value, cellsList));
                cellsUsed.addAll(cellsList);
                break;
            }

            if(points.size() == 1){
                continue;
            }
            int totalSum = points.stream().mapToInt(x -> sudoku.getGrid()[x.first][x.second]).sum();
            cellsUsed.addAll(points);
            cages.add(new Pair<>(totalSum, points));
            break;


        }
        if(attemptsToDo == 0){
            return null;
        }

        KillerSudokuType updatedSudoku = new KillerSudokuType(sudoku.getType(), sudoku.getGrid(), cages);
        int solutionsFound = SolveSudoku(updatedSudoku, 0, GeneratePossibilitiesPerCell(), true);
        if (solutionsFound == 1) {
            ArrayList<Pair<Integer, List<Pair<Integer, Integer>>>> result = AddCages(updatedSudoku, cellsUsed);
            if (result == null) {
                return updatedSudoku.getCages();
            } else {
                return result;
            }
        }
        return null;
    }


    static ArrayList<ArrayList<ArrayList<Integer>>> GeneratePossibilitiesPerCell(){
        ArrayList<ArrayList<ArrayList<Integer>>> possibilities = new ArrayList<>();
        for(int row = 0; row < 9; row++){
            possibilities.add(new ArrayList<>());
            for(int col = 0; col < 9; col++){
                possibilities.get(row).add(new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)));
            }
        }
        return possibilities;
    }


    static int SolveSudoku(KillerSudokuType sudoku, int depth, ArrayList<ArrayList<ArrayList<Integer>>> possibilities, boolean addCages) {
        // Possibilities count
        /*BigInteger total = BigInteger.ONE;
        for(int row = 0; row < 9; row++){
            for(int col = 0; col < 9; col++){
                if(possibilities.get(row).get(col).size() == 0){
                    continue;
                }
                total = total.multiply(BigInteger.valueOf(possibilities.get(row).get(col).size()));
            }
        }*/

        //System.out.println("Total possibilities remaining: " + total);

        // Check if we're finished
        int[][] grid = Arrays.stream(sudoku.getGrid()).map(int[]::clone).toArray(int[][]::new);

        Pair<Integer, Integer> nextEmpty = null;
        for(int row=0; row < grid.length; row++){
            for(int col=0; col < grid[0].length; col++){
                if(grid[row][col] == 0){
                    nextEmpty = new Pair<>(row, col);
                    break;
                }
            }
            if(nextEmpty != null){
                break;
            }
        }
        if(nextEmpty == null){
            return 1; // Found one solution
        }
        // End Check if we're finished


        ArrayList<ArrayList<ArrayList<Integer>>> possibilitiesClone = clonePossibilities(possibilities);

        // Pick next element
        ArrayList<Pair<Pair<Integer, Integer>, ArrayList<Integer>>> options = new ArrayList<>();
        for(int row = 0; row < 9; row++){
            for(int col = 0; col < 9; col++) {
                options.add(new Pair<>(new Pair<>(row, col), possibilitiesClone.get(row).get(col)));
            }
        }
        //System.out.println();
        //sudoku.PrintSudoku();

        options = (ArrayList<Pair<Pair<Integer, Integer>, ArrayList<Integer>>>) options.stream().filter(a -> sudoku.getGrid()[a.first.first][a.first.second] == 0).collect(Collectors.toList());
        Pair<Pair<Integer, Integer>, ArrayList<Integer>> min = options.stream().min(Comparator.comparingInt(a -> a.second.size())).orElse(null);
        assert min != null;
        if(min.second.size() == 0){
            return 0;
        }

        nextEmpty = min.first;
        //System.out.println(nextEmpty);

        // END Pick next element

        int solutionsFound = 0;
        for(int attempt=0; attempt < possibilitiesClone.get(nextEmpty.first).get(nextEmpty.second).size(); attempt++){
            int newVal = possibilitiesClone.get(nextEmpty.first).get(nextEmpty.second).get(attempt);
            grid[nextEmpty.first][nextEmpty.second] = newVal;

            if(!ValidGrid(new KillerSudokuType(sudoku.getType(), grid, sudoku.getCages()))){
                possibilitiesClone.get(nextEmpty.first).get(nextEmpty.second).remove(Integer.valueOf(newVal));
                attempt--;
                //System.out.println("Fails Valid Grid");
                continue;
            }

            ArrayList<ArrayList<ArrayList<Integer>>> newPossibilities = RemovePossibilities(sudoku, possibilitiesClone, nextEmpty, newVal);
            //System.out.println("Setting " + nextEmpty + " to " + newVal);
            //System.out.println(newPossibilities);
            solutionsFound += SolveSudoku(new KillerSudokuType(sudoku.getType(), grid, sudoku.getCages()), depth+1, newPossibilities, addCages);
            //if(solutionsFound != 0) {
            //System.out.println("Depth: " + depth + " - solutions: " + solutionsFound);
            //}
            if(solutionsFound > 1){ // No point carrying on if we know we don't have a unique solution
                return solutionsFound;
            }
            //System.out.println("Solutions Found: " + solutionsFound);
        }

        return solutionsFound;
    }

    static boolean ValidGrid(KillerSudokuType sudoku){
        if(!ClassicSudoku.ValidGrid(sudoku.getGrid())){
            return false;
        }
        for(Pair<Integer, List<Pair<Integer, Integer>>> cage : sudoku.getCages()){
            int sumVal = cage.second.stream().mapToInt(x -> sudoku.getGrid()[x.first][x.second]).sum();
            List<Pair<Integer, Integer>> newSum = cage.second.stream().filter(x -> sudoku.getGrid()[x.first][x.second] != 0).collect(Collectors.toList());

            if(newSum.size() == cage.second.size()){
                if(sumVal != cage.first){
                    return false;
                }
            } else{
                if(sumVal >= cage.first){
                    return false;
                }
                if(sumVal + 9*(cage.second.size() - newSum.size()) < cage.first){
                    return false;
                }
            }

        }
        return true;
    }

    static ArrayList<ArrayList<ArrayList<Integer>>> clonePossibilities(ArrayList<ArrayList<ArrayList<Integer>>> possibilities){
        ArrayList<ArrayList<ArrayList<Integer>>> newPossibilities = new ArrayList<>();
        for(int row = 0; row < 9; row++){
            newPossibilities.add(new ArrayList<>());
            for(int col = 0; col < 9; col++){
                newPossibilities.get(row).add(new ArrayList<>());
                for(int val : possibilities.get(row).get(col)){
                    newPossibilities.get(row).get(col).add(val);
                }
            }
        }
        return newPossibilities;
    }

    private static ArrayList<ArrayList<ArrayList<Integer>>> RemovePossibilities(KillerSudokuType sudoku, ArrayList<ArrayList<ArrayList<Integer>>> possibilities,
                                                                                Pair<Integer, Integer> updatedPos, int newVal) {

        ArrayList<ArrayList<ArrayList<Integer>>> clone = clonePossibilities(possibilities);

        // Remove newVal from box
        for(Pair<Integer, Integer> pos : sudoku.getBox(updatedPos)){
            clone.get(pos.first).get(pos.second).remove(Integer.valueOf(newVal));
        }
        // Remove newVal from row
        for(Pair<Integer, Integer> pos : sudoku.getRow(updatedPos)){
            clone.get(pos.first).get(pos.second).remove(Integer.valueOf(newVal));
        }
        // Remove newVal from col
        for(Pair<Integer, Integer> pos : sudoku.getCol(updatedPos)){
            clone.get(pos.first).get(pos.second).remove(Integer.valueOf(newVal));
        }
        // Remove newVal from cage
        for(Pair<Integer, Integer> pos : sudoku.getCage(updatedPos).second){
            clone.get(pos.first).get(pos.second).remove(Integer.valueOf(newVal));
        }

        // Remove vals too large in cage
        Pair<Integer, List<Pair<Integer, Integer>>> cage = sudoku.getCage(updatedPos);
        int sumVal = cage.second.stream().mapToInt(x -> sudoku.getGrid()[x.first][x.second]).sum() + newVal;
        List<Pair<Integer, Integer>> nonZeroCells = cage.second.stream().filter(x -> sudoku.getGrid()[x.first][x.second] != 0).collect(Collectors.toList());

        if(cage.first - sumVal < 9 && nonZeroCells.size() < cage.second.size()){
            for(Pair<Integer, Integer> pos : cage.second){
                if(!nonZeroCells.contains(pos)){
                    for(int i = cage.first-sumVal + 1; i <= 9; i++){
                        clone.get(pos.first).get(pos.second).remove(Integer.valueOf(newVal));
                    }
                }
            }
        }

        return clone;

    }

}