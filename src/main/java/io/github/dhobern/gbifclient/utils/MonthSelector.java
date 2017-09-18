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
public class MonthSelector implements CategorySelector {
    
    private final static String[] categoryLabels = { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };

    public MonthSelector() {
    }

    @Override
    public int getCategoryCount() {
        return 12;
    }

    @Override
    public int getCategory(OccurrenceBin bin) {
        int category = -1;
        String dateString = bin.getDateString();
        if (!dateString.equals("NULL")) {
            category = new Integer(dateString.substring(5,7)).intValue() - 1;
            if (category > 11) {
                category = -1;
            }
        }
        return category;
    }

    @Override
    public String getCategoryLabel(int index) {
        return categoryLabels[index];
    }
}