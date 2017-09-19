/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.dhobern.gbifclient.utils;

import java.text.DecimalFormat;

/**
 *
 * @author Platyptilia
 */
public class LongitudeSelector extends CoordinateSelector {
    
    public LongitudeSelector(int limit, double scale) {
        super(180, scale);
    }
    
    public LongitudeSelector(double scale) {
        this(180, scale);
    }
    
    @Override
    public int getCategory(Mappable bin) {
        return getCategory(bin.getDecimalLongitude());
    }
    
    public String getName() {
        return "longitudeRange";
    }
}