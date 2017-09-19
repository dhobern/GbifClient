/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.dhobern.gbifclient.utils;

import java.time.LocalDate;

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
    public int getCategory(Mappable bin) {
        int category = -1;
        LocalDate date = bin.getDate();
        if (date != null) {
            category = date.getMonthValue() - 1;
        }
        return category;
    }

    @Override
    public String getCategoryLabel(int index) {
        return categoryLabels[index];
    }
    
    public String getName() {
        return "month";
    }
}