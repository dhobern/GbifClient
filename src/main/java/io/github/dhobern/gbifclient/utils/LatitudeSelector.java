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
public class LatitudeSelector extends CoordinateSelector {
    
    private static String[] requiredElements = {
        Occurrence.DECIMALLATITUDE
    };

    public LatitudeSelector(int limit, double scale) {
        super(90, scale);
    }
    
    public LatitudeSelector(double scale) {
        this(90, scale);
    }
    
    @Override
    public int getCategory(Item item) {
        return getCategory(new Double(item.get(Occurrence.DECIMALLATITUDE)));
    }
           
    public String getName() {
        return "latitudeRange";
    }

    public String[] getRequiredElements() {
        return requiredElements;
    }
}