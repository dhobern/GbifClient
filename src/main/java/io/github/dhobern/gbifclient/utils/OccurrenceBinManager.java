/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.dhobern.gbifclient.utils;

import static io.github.dhobern.gbifclient.utils.TabDataUtils.INDEX_DAY;
import static io.github.dhobern.gbifclient.utils.TabDataUtils.INDEX_COUNTRY;
import static io.github.dhobern.gbifclient.utils.TabDataUtils.INDEX_DECIMALLATITUDE;
import static io.github.dhobern.gbifclient.utils.TabDataUtils.INDEX_DECIMALLONGITUDE;
import static io.github.dhobern.gbifclient.utils.TabDataUtils.INDEX_MONTH;
import static io.github.dhobern.gbifclient.utils.TabDataUtils.INDEX_SCIENTIFICNAME;
import static io.github.dhobern.gbifclient.utils.TabDataUtils.INDEX_SPECIESKEY;
import static io.github.dhobern.gbifclient.utils.TabDataUtils.INDEX_TAXONKEY;
import static io.github.dhobern.gbifclient.utils.TabDataUtils.INDEX_YEAR;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

/**
 *
 * @author Platyptilia
 */
public class OccurrenceBinManager {
    
    // To check date matches, use a small bitfield
    private static final int DAY_ELEMENT = 1;
    private static final int MONTH_ELEMENT = 2;
    private static final int YEAR_ELEMENT = 4;
    
    // Date matches at day level require all three parts to match
    private static final int PERIOD_DAY = 7;

    // Date matches at month level require month and year to match
    private static final int PERIOD_MONTH = 6;

    // Date matches at year level require just year to match
    private static final int PERIOD_YEAR = 4;

    // Date matches at julian day level require day and month to match
    private static final int PERIOD_JULIANDAY = 3;

    // Date matches at julian month level require just month to match
    private static final int PERIOD_JULIANMONTH = 2;
    
    private Float scale;
    private DecimalFormat latitudeFormat;
    private DecimalFormat longitudeFormat;
    private int period;
    private HashMap<String,OccurrenceBin> bins;    
    private HashMap<String,String> cachedNames;
    
    public OccurrenceBinManager(Float s, String p) {
        scale = s;
        
        if (scale < 0.00002) {
            latitudeFormat = new DecimalFormat("+00.000000;-00.000000");
            longitudeFormat = new DecimalFormat("+000.000000;-000.000000");
        } else if (scale < 0.0002) {
            latitudeFormat = new DecimalFormat("+00.00000;-00.00000");
            longitudeFormat = new DecimalFormat("+000.00000;-000.00000");
        } else if (scale < 0.002) {
            latitudeFormat = new DecimalFormat("+00.0000;-00.0000");
            longitudeFormat = new DecimalFormat("+000.0000;-000.0000");
        } else if (scale < 0.02) {
            latitudeFormat = new DecimalFormat("+00.000;-00.000");
            longitudeFormat = new DecimalFormat("+000.000;-000.000");
        } else if (scale < 0.2) {
            latitudeFormat = new DecimalFormat("+00.00;-00.00");
            longitudeFormat = new DecimalFormat("+000.00;-000.00");
        } else if (scale < 2) {
            latitudeFormat = new DecimalFormat("+00.0;-00.0");
            longitudeFormat = new DecimalFormat("+000.0;-000.0");
        } else {
            latitudeFormat = new DecimalFormat("+00;-00");
            longitudeFormat = new DecimalFormat("+000;-000");
        }
        
        switch(p) {
            case "MONTH":       period = PERIOD_MONTH; break;
            case "YEAR":        period = PERIOD_YEAR; break;
            case "JULIANDAY":   period = PERIOD_JULIANDAY; break;
            case "JULIANMONTH": period = PERIOD_JULIANMONTH; break;
            default:            period = PERIOD_DAY; break;
        }
        
        bins = new HashMap<String,OccurrenceBin>();
        cachedNames = new HashMap<String,String>();
    }
    
    public OccurrenceBin add(String[] values, Boolean requiresSpecies, Boolean requiresCoordinates, Set<String> countryFilter) {
        OccurrenceBin bin = null;
        
        Double latitude = getBinCentroid(values[INDEX_DECIMALLATITUDE], 90, scale);
        Double longitude = getBinCentroid(values[INDEX_DECIMALLONGITUDE], 180, scale);
        String latitudeString = (latitude == null) ? "NULL" : latitudeFormat.format(latitude);
        String longitudeString = (longitude == null) ? "NULL" : longitudeFormat.format(longitude);
        String dateString = getDateString(values[INDEX_YEAR], values[INDEX_MONTH], values[INDEX_DAY]);
        String scientificName = null;
        String speciesKey = values[INDEX_SPECIESKEY];
        String taxonKey = values[INDEX_TAXONKEY];
        String indexKey;
        if (    (!requiresSpecies || speciesKey.length() > 0)
             && (!requiresCoordinates || (latitude != null && longitude != null))
             && ((countryFilter == null) || countryFilter.contains(values[INDEX_COUNTRY]))) {
            if (speciesKey.length() == 0) {
                scientificName = values[INDEX_SCIENTIFICNAME];
                indexKey = taxonKey;
            } else {
                indexKey = speciesKey;
                if (speciesKey.equals(taxonKey)) {
                    scientificName = values[INDEX_SCIENTIFICNAME];
                } else {
                    scientificName = getScientificName(speciesKey);
                }
            } 

            String key = latitudeString + "_" + longitudeString + "_" + dateString + "_" + indexKey;

            bin = bins.get(key);

            if (bin == null) {
                bin = new OccurrenceBin(latitude, longitude, dateString, indexKey, scientificName);
                bins.put(key, bin);
            } else {
                bin.increment();
                if (bin.getCanonicalName() == null && scientificName != null) {
                    bin.setCanonicalName(scientificName);
                }
            }
        }
        
        return bin;
    }
    
    public Iterator<OccurrenceBin> getOccurrenceBins() {
        return bins.values().iterator();
    }
    
    private Double getBinCentroid(String v, int range, Float scale) {
        Double value = null;
        try {
            value = ((Math.floor((new Float(v) + (range / 2)) / scale) + 0.5) * scale) - (range / 2);
        } catch (Exception ex) {
            // Ignore
        }
        
        return value;
    }

    private String getDateString(String y, String m, String d) {
        String dateString = "NULL";
        try {
            StringBuffer sb = new StringBuffer();
            if ((period & YEAR_ELEMENT) == YEAR_ELEMENT) {
                Integer year = new Integer(y);
                if (year < 1000 || year > 9999) {
                    throw new IOException(String.format("Bad year: %s", y));
                }
                sb.append(y);
            } else {
                sb.append("XXXX");
            }
            sb.append("-");
            if ((period & MONTH_ELEMENT) == MONTH_ELEMENT) {
                Integer month = new Integer(m);
                if (month < 1 || month > 12) {
                    throw new IOException(String.format("Bad month: %s", m));
                }
                if (month < 10) {
                    sb.append("0");
                }
                sb.append(m);
            } else {
                sb.append("XX");
            }
            sb.append("-");
            if ((period & DAY_ELEMENT) == DAY_ELEMENT) {
                Integer day = new Integer(d);
                if (day < 1 || day > 31) {
                    throw new IOException(String.format("Bad day: %s", d));
                }
                if (day < 10) {
                    sb.append("0");
                }
                sb.append(d);
            } else {
                sb.append("XX");
            }
            dateString = sb.toString();
        } catch (Exception ex) {
            // Ignore
        }
        return dateString;
    }
    
    private String getScientificName(String speciesKey) {
        String name = cachedNames.get(speciesKey);
        
        if (name == null) {
            name = GbifApiRequestFactory.getScientificName(speciesKey);
            cachedNames.put(speciesKey, name);
        }
        
        return name;
    }
}