/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.dhobern.gbifclient.utils;

import io.github.dhobern.gbifclient.matrix.CategorySelector;
import io.github.dhobern.gbifclient.matrix.Item;
import java.time.LocalDate;

/**
 *
 * @author Platyptilia
 */
public class YearSelector implements CategorySelector {
    
    private static String[] requiredElements = { Occurrence.YEAR };
    
    public YearSelector() {
    }

    @Override
    public int getCategoryCount() {
        return -1;
    }

    @Override
    public int getCategory(Item item) {
        int category = new Integer(item.get(Occurrence.YEAR)).intValue();
        return category;
    }

    @Override
    public String getCategoryLabel(int index) {
        return new Integer(index).toString();
    }
    
    public String getName() {
        return "year";
    }

    public String[] getRequiredElements() {
        return requiredElements;
    }
}