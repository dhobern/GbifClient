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
public class CountrySelector implements CategorySelector {
    
    private static final String[] codes = GbifApiRequestFactory.getEnumeration("Country");
    
    public CountrySelector() {
    }

    @Override
    public int getCategoryCount() {
        return codes.length;
    }

    @Override
    public int getCategory(Mappable bin) {
        String code = bin.getCountryCode();
        int category = -1;
        for (int i = 0; category == -1 && i < codes.length; i++) {
            if(code.equals(codes[i])) {
                category = i;
            }
        }
        return category;
    }

    @Override
    public String getCategoryLabel(int index) {
        return codes[index];
    }
    
    public String getName() {
        return "country";
    }
}