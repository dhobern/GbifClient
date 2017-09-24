/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.dhobern.gbifclient.utils;

import io.github.dhobern.gbifclient.matrix.Item;
import io.github.dhobern.gbifclient.matrix.MatrixDimensions;
import io.github.dhobern.gbifclient.matrix.MultidimensionMatrix;
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
    private static final String KEY_BINDOMAIN = "bindomain";
    private static final String KEY_BINRANGE = "binrange";
    private static final String KEY_GRIDDOMAIN = "griddomain";
    private static final String KEY_GRIDRANGE = "gridrange";
    private static final String KEY_CACHEDOWNLOADS = "cachedownloads";
    private static final String KEY_REQUIRECOORDINATES = "requirecoordinates";
    private static final String KEY_REQUIRESPECIES = "requirespecies";
    private static final String KEY_REQUIREDATE = "requiredate";
    private static final String KEY_COUNTRYFILTER = "countryfilter";
    private static final String KEY_SCIENTIFICNAME = "scientificname";

    private static final String FORMAT_DWCA = "DWCA";
    private static final String FORMAT_SIMPLE_CSV = "SIMPLE_CSV";
    
    private static final String GLOBAL_PROPERTY_FILE = "gbifclient-default.cfg";
    private static final String LOCAL_PROPERTY_FILE = "gbifclient-local.cfg";
    
    private static Properties defaultProperties;
    private static Properties localProperties;
    private static Properties properties;
    
    static {
        defaultProperties = loadProperties(new Properties(), GLOBAL_PROPERTY_FILE);
        localProperties = loadProperties(new Properties(defaultProperties), LOCAL_PROPERTY_FILE);
        properties = new Properties(localProperties);
    }
    
    public static Properties loadProperties(Properties p, String fileName) {
        InputStream input = null;
        
        try {
            input = GbifConfiguration.class.getClassLoader().getResourceAsStream(fileName);
            if (input != null) {
                p.load(input);
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
        
        return p;
    }
        
    public static void processArgV(String[] argv) {
        for (int i = 0; i < argv.length; i++) {
            String[] elements = argv[i].split("=", 2);

            // Arguments which do not set properties are assumed to be property 
            // filenames and these are loaded into the runtime properties
            if (elements.length == 1) {
                loadProperties(properties, argv[i]);
            } else {
                properties.put(elements[0], elements[i]);
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

    public static String getScientificName() {
        return getProperty(KEY_SCIENTIFICNAME);
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
    public static MultidimensionMatrix getOccurrenceBinMatrix() {
        MatrixDimensions domainDimensions = new MatrixDimensions();
        addDimensions(domainDimensions, getProperty(KEY_BINDOMAIN, "0.01&DAY"));
        MatrixDimensions rangeDimensions = new MatrixDimensions();
        addDimensions(rangeDimensions, getProperty(KEY_BINRANGE, "SPECIES"));
        
        return new MultidimensionMatrix(domainDimensions, rangeDimensions);
    }

    
    public static MultidimensionMatrix getGridMatrix() {
        MatrixDimensions domainDimensions = new MatrixDimensions();
        addDimensions(domainDimensions, getProperty(KEY_GRIDDOMAIN, "1.0"));
        MatrixDimensions rangeDimensions = new MatrixDimensions();
        addDimensions(rangeDimensions, getProperty(KEY_GRIDRANGE, "SPECIES"));
        
        return new MultidimensionMatrix(domainDimensions, rangeDimensions);
    }
    
    public static void addDimensions(MatrixDimensions dimensions, String keyString) {
        String[] keys = keyString.split("&");
        for (int i = 0; i < keys.length; i++) {
            switch (keys[i]) {
                case "COUNTRY":
                    dimensions.addDimension(new CountrySelector());
                    break;
                case "DATE":
                case "DAY":
                    dimensions.addDimension(new DateSelector());
                    break;
                case "MONTH":
                    dimensions.addDimension(new MonthSelector());
                    break;
                case "JULIANWEEK":
                    dimensions.addDimension(new JulianWeekSelector());
                    break;
                case "JULIANDAY":
                    dimensions.addDimension(new JulianDaySelector());
                    break;
                case "ALLTIME":
                    // Do nothing
                    break;
                case "SPECIES":
                case "TAXON":
                    dimensions.addDimension(new SpeciesSelector());
                    break;
                case "LATITUDE":
                    dimensions.addDimension(new LatitudeSelector(1.0));
                    break;
                case "LONGITUDE":
                    dimensions.addDimension(new LongitudeSelector(1.0));
                    break;
                default:
                    if (keys[i].indexOf("-") > 0) {
                        dimensions.addDimension(new MultiPeriodSelector(keys[i]));
                    } else {
                        Double gridScale = new Double(keys[i]);
                        dimensions.addDimension(new LatitudeSelector(gridScale))
                                  .addDimension(new LongitudeSelector(gridScale));
                    }
                    break;
            }
        }
    }
}