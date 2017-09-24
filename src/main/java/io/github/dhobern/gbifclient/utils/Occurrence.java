/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.dhobern.gbifclient.utils;

import io.github.dhobern.gbifclient.matrix.Item;
import java.time.LocalDate;
import java.util.HashMap;

/**
 *
 * @author Platyptilia
 */
public class Occurrence extends Item {
    
    public static final String SCIENTIFICNAME = "scientificname";
    public static final String TAXONRANK = "taxonrank";
    public static final String COUNTRYCODE = "countrycode";
    public static final String DECIMALLATITUDE = "decimallatitude";
    public static final String DECIMALLONGITUDE = "decimallongitude";
    public static final String COORDINATEUNCERTAINTY = "coordinateuncertaintyinmeters";
    public static final String DAY = "day";
    public static final String MONTH = "month";
    public static final String YEAR = "year";
    public static final String TAXONKEY = "taxonkey";
    public static final String SPECIESKEY = "specieskey";

    private HashMap<String,Integer> columns;
    private String[] values;

    public Occurrence(HashMap<String,Integer> c) {
        columns = c;
    }
    
    public Occurrence(HashMap<String,Integer> c, String[] v) {
        this(c);
        setValues(v);
    }
    
    public Occurrence(HashMap<String,Integer> c, String s) {
        this(c);
        setValuesFromString(s);
    }
    
    public void setValuesFromString(String s) {
        setValues(s.split("\t"));
    }
    
    public void setValues(String[] values) {
        this.values = values;
    }

    public HashMap<String, Integer> getColumns() {
        return columns;
    }

    private String[] getValues() {
        return values;
    }
    
    public String get(String key) {
        String value = "";
        
        Integer i = columns.get(key);
        if (i != null && i >= 0 && i < values.length) {
            value = values[i];
        }
        
        return value;
    }

    public String getScientificName() {
        return get(SCIENTIFICNAME);
    }

    public String getTaxonRank() {
        return get(TAXONRANK);
    }

    public String getCountryCode() {
        return get(COUNTRYCODE);
    }

    public Double getDecimalLatitude() {
        String latitude = get(DECIMALLATITUDE); 
        Double l = null;
        if(latitude != null && latitude.length() > 0) {
            l = new Double(latitude);
        }
        return l;
    }

    public Double getDecimalLongitude() {
        String longitude = get(DECIMALLONGITUDE); 
        Double l = null;
        if(longitude != null && longitude.length() > 0) {
            l = new Double(longitude);
        }
        return l;
    }
    
    public LocalDate getDate() {
        LocalDate date = null;
        String y = getYear();
        String m = getMonth();
        String d = getDay();
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

    public String getCoordinateUncertainty() {
        return get(COORDINATEUNCERTAINTY);
    }

    public String getYear() {
        return get(YEAR);
    }

    public String getMonth() {
        return get(MONTH);
    }
    
    public String getDay() {
        return get(DAY);
    }
    
    public String getTaxonKey() {
        return get(TAXONKEY);
    }
    
    public String getSpeciesKey() {
        return get(SPECIESKEY);
    }
    
    public String getNamedCategory(String category) {
        return get(category);
    }
    
    public int getCount() {
        return 1;
    }
}
