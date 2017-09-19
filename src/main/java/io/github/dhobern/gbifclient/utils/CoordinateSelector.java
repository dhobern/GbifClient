/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.dhobern.gbifclient.utils;

import java.text.DecimalFormat;

/**
 *
 * @author Platyptilia
 */
public abstract class CoordinateSelector implements CategorySelector {
    
    private double coordinateLimit;
    private double gridScale;
    private int count;
    private DecimalFormat decimalFormat;
    
    private CoordinateSelector() {
    }

    public CoordinateSelector(int limit, double scale) {
        coordinateLimit = limit;
        gridScale = scale;
        
        count = new Double(Math.ceil((2 * coordinateLimit) / gridScale)).intValue();
        
        initializeDecimalFormat();
    }
    
    @Override
    public int getCategoryCount() {
        return count;
    }

    @Override
    public abstract int getCategory(Mappable bin);

    public int getCategory(double value) {
        return new Double(Math.floor((value + coordinateLimit) / gridScale)).intValue();
    }

    @Override
    public String getCategoryLabel(int index) {
        Double lower = (gridScale * index) - coordinateLimit;
        Double upper = lower + gridScale; 
        return decimalFormat.format(lower) + "," + decimalFormat.format(upper);
    }
    
    /**
     * Constructs a DecimalFormat which always shows sign and includes
     * sufficient decimal places to correspond with the grid scale.
     */
    private void initializeDecimalFormat() {
        String base = (coordinateLimit > 100) ? "000" : "00";
        if (gridScale < 2) {
            base += ".0";
            for(double g = gridScale * 5; g < 1; g *= 10) {
                base += "0";
            }
        }
        decimalFormat = new DecimalFormat("+" + base + ";-" + base);
    }

}