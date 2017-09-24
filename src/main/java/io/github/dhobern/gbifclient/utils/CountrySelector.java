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
public class CountrySelector implements CategorySelector {
    
    private static String[] requiredElements = {
        Occurrence.COUNTRYCODE
    };

    private static final String[] codes = GbifApiRequestFactory.getEnumeration("Country");
    
    public CountrySelector() {
    }

    @Override
    public int getCategoryCount() {
        return codes.length;
    }

    @Override
    public int getCategory(Item item) {
        int category = -1;
        String code = item.get(Occurrence.COUNTRYCODE);
        if (code != null) {
            for (int i = 0; category == -1 && i < codes.length; i++) {
                if(code.equals(codes[i])) {
                    category = i;
                }
            }
        }
        return category;
    }

    @Override
    public String getCategoryLabel(int index) {
        return (index == -1) ? "UNKNOWN" : codes[index];
    }
    
    public String getName() {
        return "country";
    }

    public String[] getRequiredElements() {
        return requiredElements;
    }
}