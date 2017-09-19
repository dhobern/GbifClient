/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.dhobern.gbifclient.utils;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;

/**
 *
 * @author Platyptilia
 */
public class DateSelector implements CategorySelector {
    
    private LocalDate arbitraryOrigin = LocalDate.of(1700, Month.JANUARY, 1);
    
    public DateSelector() {
        
    }

    @Override
    public int getCategoryCount() {
        return -1;
    }

    @Override
    public int getCategory(Mappable bin) {
        int category = -1;
        LocalDate date = bin.getDate();
        if (date != null) {
            category = (int) arbitraryOrigin.until(date, ChronoUnit.DAYS);
        }
        return category;
    }

    @Override
    public String getCategoryLabel(int index) {
        LocalDate date = arbitraryOrigin.plus(index, ChronoUnit.DAYS);
        return date.toString();
    }
    
    public String getName() {
        return "date";
    }

}