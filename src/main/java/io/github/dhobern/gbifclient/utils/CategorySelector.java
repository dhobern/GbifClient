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
public interface CategorySelector {
    
    public int getCategoryCount();
    
    public int getCategory(OccurrenceBin bin);
    
    public String getCategoryLabel(int index);
    
}
