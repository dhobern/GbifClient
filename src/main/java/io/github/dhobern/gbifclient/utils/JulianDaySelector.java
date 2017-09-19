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
public class JulianDaySelector implements CategorySelector {
    
    public JulianDaySelector() {
    }

    @Override
    public int getCategoryCount() {
        return 366;
    }

    @Override
    public int getCategory(Mappable bin) {
        int category = -1;
        LocalDate date = bin.getDate();
        if (date != null) {
            category = date.getDayOfYear() - 1;
        }
        return category;
    }

    @Override
    public String getCategoryLabel(int index) {
        return new Integer(index + 1).toString();
    }
    
    public String getName() {
        return "dayofyear";
    }
}