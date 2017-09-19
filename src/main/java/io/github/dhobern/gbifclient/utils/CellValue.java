/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.dhobern.gbifclient.utils;

/**
 *
 * @author Platyptilia
 */
public abstract class CellValue {
    
    int[] offsets;
    
    private CellValue() {
    }
    
    public CellValue(int[] o, Mappable m) {
        offsets = o;
    }
    
    public CellValue add(Mappable m) {
        return this;
    }
    
    public int[] getCellPosition() {
        return offsets;
    }
    
    public void prepareForExport() {
    }
    
    public abstract String[] getCountLabels();
    
    public abstract int[] getCounts();
    
    public abstract String[] getItems();
}
