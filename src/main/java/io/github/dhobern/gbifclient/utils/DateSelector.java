/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.dhobern.gbifclient.utils;

import io.github.dhobern.gbifclient.matrix.CategorySelector;
import io.github.dhobern.gbifclient.matrix.Item;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;

/**
 *
 * @author Platyptilia
 */
public class DateSelector implements CategorySelector {

    private static String[] requiredElements = {
        Occurrence.YEAR, Occurrence.MONTH, Occurrence.DAY
    };
    
    private LocalDate arbitraryOrigin = LocalDate.of(1700, Month.JANUARY, 1);
    
    public DateSelector() {
        
    }

    @Override
    public int getCategoryCount() {
        return -1;
    }

    @Override
    public int getCategory(Item item) {
        int category = -1;
        LocalDate date = getDate(item.get(Occurrence.YEAR), 
                                 item.get(Occurrence.MONTH), 
                                 item.get(Occurrence.DAY));
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

    public String[] getRequiredElements() {
        return requiredElements;
    }
    
    protected LocalDate getDate(String y, String m, String d){
        LocalDate date = null;
        if (y != null && y.length() > 0 && m != null && m.length() > 0 && d != null && d.length() > 0) {
            Integer year = new Integer(y);
            Integer month = new Integer(m);
            Integer day = new Integer(d);
            if(year > 0 && month > 0 && day > 0) {
                date = LocalDate.of(year, month, day);
            }
        }
        return date; 
    }
}