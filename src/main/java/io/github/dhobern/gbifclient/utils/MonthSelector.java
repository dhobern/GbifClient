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
public class MonthSelector implements CategorySelector {
    
    private static String[] requiredElements = { Occurrence.MONTH };
    
    private final static String[] categoryLabels = { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };

    public MonthSelector() {
    }

    @Override
    public int getCategoryCount() {
        return 12;
    }

    @Override
    public int getCategory(Item item) {
        int category = new Integer(item.get(Occurrence.MONTH)).intValue() - 1;
        return category;
    }

    @Override
    public String getCategoryLabel(int index) {
        return categoryLabels[index];
    }
    
    public String getName() {
        return "month";
    }

    public String[] getRequiredElements() {
        return requiredElements;
    }
}