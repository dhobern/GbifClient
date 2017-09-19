/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.dhobern.gbifclient.utils;

import java.util.ArrayList;

/**
 *
 * @author Platyptilia
 */
public class MatrixDimensions {
    
    private ArrayList<CategorySelector> selectors;
    private ArrayList<String> indexFormats;
    int selectorCount = 0;
    boolean abstractMatrix = false;
    
    public MatrixDimensions() {
        selectors = new ArrayList<CategorySelector>();
        indexFormats = new ArrayList<String>();
    }
    
    public MatrixDimensions addDimension(CategorySelector s) {
        selectors.add(s);

        String indexFormat = ((selectorCount > 0) ? "-%" : "%")
                + ((s.getCategoryCount() < 0) 
                        ? "" : new Double(Math.ceil(Math.log10(s.getCategoryCount()))).intValue())
                + "d";
        
        indexFormats.add(indexFormat);
        
        selectorCount++;
        
        if (s.getCategoryCount() < 0) {
            abstractMatrix = true;
        }

        return this;
    }
    
    public int[] getCellPosition(Mappable m) {
        int[] position = new int[selectorCount];
        for (int i = 0; i < selectorCount; i++) {
            position[i] = selectors.get(i).getCategory(m);
        }
        return position;
    }
    
    public int[] getDimensions() {
        int[] dimensions = new int[selectorCount];
        
        for (int i = 0; i < selectorCount; i++) {
            dimensions[i] = selectors.get(i).getCategoryCount();
        }
        
        return dimensions;
    }
    
    public String getCellKey(int[] position) {
        String key = "";
        
        for (int i = 0; i < selectorCount && i < position.length; i++) {
            key += String.format(indexFormats.get(i), position[i]);
        }
        
        return key;
    }
    
    public boolean isAbstractMatrix() {
        return abstractMatrix;
    }
    
    public ArrayList<CategorySelector> getSelectors() {
        return selectors;
    }
}
