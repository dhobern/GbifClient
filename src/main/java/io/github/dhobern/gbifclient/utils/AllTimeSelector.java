/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.dhobern.gbifclient.utils;

import io.github.dhobern.gbifclient.matrix.CategorySelector;
import io.github.dhobern.gbifclient.matrix.Item;

/**
 *
 * @author Platyptilia
 */
public class AllTimeSelector implements CategorySelector {

    private static String[] requiredElements = {};
    
    public AllTimeSelector() {
    }

    public int getCategoryCount() {
        return 1;
    }

    public String getCategoryLabel(int index) {
        return "ALLTIME";
    }
    
    public String getName() {
        return "timePeriod";
    }

    @Override
    public int getCategory(Item item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String[] getRequiredElements() {
        return requiredElements;
    }
}