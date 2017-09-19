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
public class JulianWeekSelector implements CategorySelector {
    
    public JulianWeekSelector() {
    }

    @Override
    public int getCategoryCount() {
        return 52;
    }

    @Override
    public int getCategory(Mappable bin) {
        int category = -1;
        LocalDate date = bin.getDate();
        if (date != null) {
            category = (date.getDayOfYear() - 1) / 7;
            if (category == 52) {
                category = 51;
            }
        }
        return category;
    }

    @Override
    public String getCategoryLabel(int index) {
        return new Integer(index + 1).toString();
    }
    
    public String getName() {
        return "weekofyear";
    }
}