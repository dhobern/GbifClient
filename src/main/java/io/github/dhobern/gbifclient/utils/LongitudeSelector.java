/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.dhobern.gbifclient.utils;

import io.github.dhobern.gbifclient.matrix.Item;

/**
 *
 * @author Platyptilia
 */
public class LongitudeSelector extends CoordinateSelector {
    
    private static String[] requiredElements = {
        Occurrence.DECIMALLONGITUDE
    };

    public LongitudeSelector(int limit, double scale) {
        super(180, scale);
    }
    
    public LongitudeSelector(double scale) {
        this(180, scale);
    }
    
    @Override
    public int getCategory(Item item) {
        return getCategory(new Double(item.get(Occurrence.DECIMALLONGITUDE)));
    }
    
    public String getName() {
        return "longitudeRange";
    }

    public String[] getRequiredElements() {
        return requiredElements;
    }
}