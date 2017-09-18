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
public class OccurrenceBin {
    private Double decimalLatitudeCentroid;
    private Double decimalLongitudeCentroid;
    private String dateString;
    private String speciesKey;
    private String canonicalName;
    private int count;
    
    public OccurrenceBin(Double lat, Double lon, String d, String s, String n) {
        decimalLatitudeCentroid = lat;
        decimalLongitudeCentroid = lon;
        dateString = d;
        speciesKey = s;
        canonicalName = n;
        count = 1;
    }
    
    public int increment() {
        return ++count;
    }

    public void setCanonicalName(String canonicalName) {
        this.canonicalName = canonicalName;
    }
    
    public Double getDecimalLatitudeCentroid() {
        return decimalLatitudeCentroid;
    }

    public Double getDecimalLongitudeCentroid() {
        return decimalLongitudeCentroid;
    }

    public String getDateString() {
        return dateString;
    }

    public String getSpeciesKey() {
        return speciesKey;
    }

    public String getCanonicalName() {
        return canonicalName;
    }

    public int getCount() {
        return count;
    }

}