/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.dhobern.gbifclient.matrix;

import io.github.dhobern.gbifclient.utils.OccurrenceInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Platyptilia
 */
public class MatrixDimensions {
    
    private ArrayList<CategorySelector> selectors;
    private ArrayList<String> indexFormats;
    int selectorCount = 0;
    boolean unboundedMatrix = false;
    
    public MatrixDimensions() {
        selectors = new ArrayList<CategorySelector>();
        indexFormats = new ArrayList<String>();
    }
    
    public MatrixDimensions addDimension(CategorySelector s) {
        selectors.add(s);
        
        String indexFormat = ((selectorCount > 0) ? "_%" : "%")
                + ((s.getCategoryCount() < 10) 
                        ? "" : ("0" + new Double(Math.ceil(Math.log10(s.getCategoryCount()))).intValue()))
                + "d";
        
        indexFormats.add(indexFormat);

        selectorCount++;
        
        if (s.getCategoryCount() < 0) {
            unboundedMatrix = true;
        }

        return this;
    }
    
    public int[] getCellPosition(Item item) {
        int[] position = new int[selectorCount];
        for (int i = 0; i < selectorCount; i++) {
            position[i] = selectors.get(i).getCategory(item);
        }
        return position;
    }
    
    /*
    public int[] getDimensions() {
        int[] dimensions = new int[selectorCount];
        
        for (int i = 0; i < selectorCount; i++) {
            dimensions[i] = selectors.get(i).getCategoryCount();
        }
        
        return dimensions;
    }
*/
    
    public boolean isUnboundedMatrix() {
        return unboundedMatrix;
    }
    
    public ArrayList<CategorySelector> getSelectors() {
        return selectors;
    }

    public String lock(int[] position) {
        String key = "";
        
        for (int i = 0; i < selectorCount && i < position.length; i++) {
            key += String.format(indexFormats.get(i), position[i]);
        }
        
        return key;
    }

    public int[] unlock(String key) {
        String[] parts = key.split("_");
        int[] unlocked = new int[parts.length];
        
        for (int i = 0; i < unlocked.length; i++) {
            unlocked[i] = new Integer(parts[i]).intValue();
        }
        return unlocked;
    }
}
