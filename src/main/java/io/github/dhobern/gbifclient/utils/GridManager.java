/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.dhobern.gbifclient.utils;

import java.text.DecimalFormat;
import java.util.Arrays;

/**
 *
 * @author Platyptilia
 */
public class GridManager {
    
    private static final int PERIOD_TYPE_NONE = 0;
    private static final int PERIOD_TYPE_YEARRANGE = 1;
    private static final int PERIOD_TYPE_MONTHS = 2;
    
    private Float scale;
    private Species[][][][] grid;    
    private DecimalFormat latitudeFormat;
    private DecimalFormat longitudeFormat;
    private int xRange;
    private int yRange;
    private int zRange;
    private CategorySelector periodSelector;

    public int getXRange() {
        return xRange;
    }

    public int getYRange() {
        return yRange;
    }

    public int getZRange() {
        return zRange;
    }
    
    public GridManager(Float s, String p) {
        scale = s;
        
        switch (p) {
            case "MONTH":
                periodSelector = new MonthSelector();
                break;
            case "ALLTIME":
                periodSelector = new AllTimeSelector();
                break;
            default:
                periodSelector = new MultiPeriodSelector(p);
                break;
        }
                
        xRange = new Double(Math.ceil(360 / scale)).intValue();
        yRange = new Double(Math.ceil(180 / scale)).intValue();
        zRange = periodSelector.getCategoryCount();
        
        grid = new Species[xRange][yRange][zRange][0];
        
        if (scale < 0.02) {
            latitudeFormat = new DecimalFormat("+00.000;-00.000");
            longitudeFormat = new DecimalFormat("+000.000;-000.000");
        } else if (scale < 0.2) {
            latitudeFormat = new DecimalFormat("+00.00;-00.00");
            longitudeFormat = new DecimalFormat("+000.00;-000.00");
        } else if (scale < 2) {
            latitudeFormat = new DecimalFormat("+00.0;-00.0");
            longitudeFormat = new DecimalFormat("+000.0;-000.0");
        } else {
            latitudeFormat = new DecimalFormat("+00;-00");
            longitudeFormat = new DecimalFormat("+000;-000");
        }
    }
    
    public Species add(OccurrenceBin bin) {
        Species species = null;

        int z = periodSelector.getCategory(bin);

        if (z >= 0) {
            int x = new Double(Math.floor((bin.getDecimalLongitudeCentroid() + 180) / scale)).intValue();
            if (x == xRange) x--;
            int y = new Double(Math.floor((bin.getDecimalLatitudeCentroid() + 90) / scale)).intValue();
            if (y == yRange) y--;

            Species[] cell = grid[x][y][z];
            for (int i = 0; i < cell.length; i++) {
                if (cell[i].getCanonicalName().equals(bin.getCanonicalName())) {
                    species = cell[i];
                    species.increment(bin);
                }
            }

            if (species == null) {
                species = new Species(bin);
                grid[x][y][z] = addSpeciesToCell(cell, species);
            }
        }
        
        return species;
    }
    
    public Species[][][][] getGrid() {
        return grid;
    }
    
    public String getXLabel(int x) {
        Float leftSide = (x * scale) - 180;
        Float rightSide = leftSide + scale;
        return longitudeFormat.format(leftSide) + "," + longitudeFormat.format(rightSide);
    }
    
    public String getYLabel(int y) {
        Float lowerSide = (y * scale) - 90;
        Float upperSide = lowerSide + scale;
        return latitudeFormat.format(lowerSide) + "," + latitudeFormat.format(upperSide);
    }
    
    public String getZLabel(int z) {
        return periodSelector.getCategoryLabel(z);
    }
    

    private Species[] addSpeciesToCell(Species[] cell, Species species) {
        Species[] newCell = new Species[cell.length + 1];
        System.arraycopy(cell, 0, newCell, 0, cell.length);
        newCell[cell.length] = species;
        return newCell;
    }

    public int sortBins() {
        int largest = 0;
        for (int i = 0; i < xRange; i++) {
            for (int j = 0; j < yRange; j++) {
                for (int k = 0; k < zRange; k++) {
                    if (grid[i][j][k].length > 1) {
                        Arrays.sort(grid[i][j][k], null);
                        if(grid[i][j][k].length > largest) {
                            largest = grid[i][j][k].length;
                        }
                    }
                }
            }
        }
        return largest;
    }
}