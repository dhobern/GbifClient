/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.dhobern.gbifclient.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Platyptilia
 */
public class GbifConfiguration {
    
    private static final String KEY_GBIFUSER = "gbifuser";
    private static final String KEY_GBIFPASSWORD = "gbifpassword";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_FORMAT = "format";
    private static final String KEY_BINSCALE = "binscale";
    private static final String KEY_BINPERIOD = "binperiod";
    private static final String KEY_GRIDSCALE = "gridscale";
    private static final String KEY_GRIDPERIOD = "gridperiod";
    private static final String KEY_CACHEDOWNLOADS = "cachedownloads";
    private static final String KEY_REQUIRECOORDINATES = "requirecoordinates";
    private static final String KEY_REQUIRESPECIES = "requirespecies";
    private static final String KEY_REQUIREDATE = "requiredate";
    private static final String KEY_COUNTRYFILTER = "countryfilter";

    private static final String FORMAT_DWCA = "DWCA";
    private static final String FORMAT_SIMPLE_CSV = "SIMPLE_CSV";
    
    private static final String PROPERTY_FILE = "gbifclient.properties";
    
    private static Properties properties;
    
    static {
        properties = new Properties();
        InputStream input = GbifConfiguration.class.getClassLoader().getResourceAsStream(PROPERTY_FILE);
        try {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException ex) {
            Logger.getLogger(GbifConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (input != null) {
                try {
                        input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public static String getProperty(String key) {
        String value = properties.getProperty(key);
        
        if (value == null) {
            System.out.printf("Please provide value for %s: \n", key);
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                value = reader.readLine();
            } catch (IOException ex) {
                Logger.getLogger(GbifConfiguration.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return value;
    }

    public static String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        
        if (value == null) {
            value = defaultValue;
        }
        
        return value;
    }

    public static String getGbifUser() {
        return getProperty(KEY_GBIFUSER);
    }

    public static String getGbifPassword() {
        return getProperty(KEY_GBIFPASSWORD);
    }

    public static String getEmail() {
        return getProperty(KEY_EMAIL);
    }
    
    public static String getFormat() {
        return getProperty(KEY_FORMAT, FORMAT_DWCA);
    }

    public static Integer getCacheDownloads() {
        return new Integer(getProperty(KEY_CACHEDOWNLOADS, "30"));
    }
    
    public static Boolean requireCoordinates() {
        return new Boolean(getProperty(KEY_REQUIRECOORDINATES, "true"));
    }

    public static Boolean requireSpecies() {
        return new Boolean(getProperty(KEY_REQUIRESPECIES, "true"));
    }

    public static Boolean requireDate() {
        return new Boolean(getProperty(KEY_REQUIREDATE, "true"));
    }

    public static Set<String> getCountryFilter() {
        Set<String> filter = null;
        String s = getProperty(KEY_COUNTRYFILTER);
        if (s != null && s.length() > 0) {
            filter = new HashSet<String>();
            String[] codes = s.split(",");
            for (int i = 0; i < codes.length; i++) {
                filter.add(codes[i]);
            }
        }
        return filter;
    }

    /**
     *
     * @return
     */
    public static OccurrenceMatrix<OccurrenceBin,Occurrence> getOccurrenceBinMatrix() {
        Double binScale = new Double(getProperty(KEY_BINSCALE, "0.01"));
        MatrixDimensions dimensions = new MatrixDimensions()
                .addDimension(new LatitudeSelector(binScale))
                .addDimension(new LongitudeSelector(binScale))
                .addDimension(new DateSelector());
        
        return new OccurrenceMatrix<OccurrenceBin,Occurrence>(dimensions, new OccurrenceBinFactory());
    }

    
    public static OccurrenceMatrix<GridCell,OccurrenceBin> getGridMatrix() {
        Double gridScale = new Double(getProperty(KEY_GRIDSCALE, "10"));
        String gridPeriod = getProperty(KEY_GRIDPERIOD, "ALLTIME");
        
        MatrixDimensions dimensions = new MatrixDimensions()
                .addDimension(new LatitudeSelector(gridScale))
                .addDimension(new LongitudeSelector(gridScale));
        
        switch (gridPeriod) {
            case "MONTH":
                dimensions.addDimension(new MonthSelector());
                break;
            case "ALLTIME":
                // Do nothing
                break;
            default:
                dimensions.addDimension(new MultiPeriodSelector(gridPeriod));
                break;
        }
        
        return new OccurrenceMatrix<GridCell,OccurrenceBin>(dimensions, new GridCellFactory());
    }
}