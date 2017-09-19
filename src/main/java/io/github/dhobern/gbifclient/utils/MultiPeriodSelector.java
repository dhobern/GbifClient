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
public class MultiPeriodSelector implements CategorySelector {
    
    private String[] categoryStrings;
    
    private int[][] boundaryYears;
    
    public MultiPeriodSelector() {
    }

    public MultiPeriodSelector(String s) {
        categoryStrings = s.split(";");
        boundaryYears = new int[categoryStrings.length][2];
        for (int i = 0; i < categoryStrings.length; i++) {
            String[] yearStrings = categoryStrings[i].split("-");
            if (yearStrings.length == 2) {
                boundaryYears[i][0] = new Integer(yearStrings[0]).intValue();
                boundaryYears[i][1] = new Integer(yearStrings[1]).intValue();
            }
        }
    }

    @Override
    public int getCategoryCount() {
        return categoryStrings.length;
    }

    @Override
    public int getCategory(Mappable bin) {
        int category = -1;
        
        LocalDate date = bin.getDate();
        if (date != null) {
            int year = date.getYear();
            for (int i = 0; category < 0 && i < boundaryYears.length; i++) {
                if (year >= boundaryYears[i][0] && year <= boundaryYears[i][1]) {
                    category = i;
                }
            }
        }
    
        return category;
    }

    @Override
    public String getCategoryLabel(int index) {
        return categoryStrings[index];
    }
    
    public String getName() {
        return "timePeriod";
    }
}