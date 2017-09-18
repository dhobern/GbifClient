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
public class TabDataUtils {

    public static final String[] extractColumns = {
        "scientificname",
        "taxonrank",
        "countrycode",
        "decimallatitude",
        "decimallongitude",
        "coordinateuncertaintyinmeters",
        "day",
        "month",
        "year",
        "taxonkey",
        "specieskey"
    };
    
    public static final int INDEX_SCIENTIFICNAME = 0;
    public static final int INDEX_TAXONRANK = 1;
    public static final int INDEX_COUNTRY = 2;
    public static final int INDEX_DECIMALLATITUDE = 3;
    public static final int INDEX_DECIMALLONGITUDE = 4;
    public static final int INDEX_COORDINATEUNCERTAINTY = 5;
    public static final int INDEX_DAY = 6;
    public static final int INDEX_MONTH = 7;
    public static final int INDEX_YEAR = 8;
    public static final int INDEX_TAXONKEY = 9;
    public static final int INDEX_SPECIESKEY = 10;
    

    public static int[] getColumnIndexes(String headingRow) {
        String[] columnHeadings = headingRow.split("\t");
        int[] indexes = new int[extractColumns.length];
        
        for (int i = 0; i < extractColumns.length; i++) {
            boolean found = false;
            for (int j = 0; !found && j < columnHeadings.length; j++) {
                if(columnHeadings[j].equals(extractColumns[i])) {
                    indexes[i] = j;
                    found = true;
                }
            }
        }
        return indexes;
    }

    public static String[] getColumnValues(String line, int[] columnIndexes) {
        String[] values = new String[columnIndexes.length];
        String[] columns = line.split("\t");
        
        for (int i = 0; i < columnIndexes.length; i++) {
            values[i] = columns[columnIndexes[i]];
        }
        
        return values;
    }
}