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
public class LatitudeSelector extends CoordinateSelector {
    
    public LatitudeSelector(int limit, double scale) {
        super(90, scale);
    }
    
    public LatitudeSelector(double scale) {
        this(90, scale);
    }
    
    @Override
    public int getCategory(Mappable bin) {
        return getCategory(bin.getDecimalLatitude());
    }
           
    public String getName() {
        return "latitudeRange";
    }
}