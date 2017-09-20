/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.dhobern.gbifclient.utils;

import java.time.LocalDate;

/**
 *
 * @author Platyptilia
 */
public class OccurrenceBin extends CellValue implements Mappable, Comparable {

    private static final String[] countLabels = new String[] { "occurrencecount" };
    private static final String[] itemLabels = new String[] { Occurrence.SPECIESKEY, Occurrence.SCIENTIFICNAME };
    
    private String speciesKey;
    private String scientificName;
    private String countryCode;

    private double decimalLatitude;
    private double decimalLongitude;
    private LocalDate date;
    private int count;
    
    public OccurrenceBin(int[] position, Mappable m) {
        super(position, m);
        String species = m.getNamedCategory(Occurrence.SPECIESKEY);
        String taxon = m.getNamedCategory(Occurrence.TAXONKEY);
        
        if (species.length() == 0) {
            scientificName = m.getNamedCategory(Occurrence.SCIENTIFICNAME);
            speciesKey = taxon;
        } else {
            speciesKey = species;
            if (species.equals(taxon)) {
                scientificName = m.getNamedCategory(Occurrence.SCIENTIFICNAME);
            } else {
                scientificName = GbifApiRequestFactory.getScientificName(speciesKey);
            }
        }
        
        decimalLatitude = m.getDecimalLatitude();
        decimalLongitude = m.getDecimalLongitude();
        countryCode = m.getCountryCode();
        date = m.getDate();

        count = 1;
    }
   
    public CellValue add(Mappable m) {
        ++count;
        return this;
    }
    
    public String getSpeciesKey() {
        return speciesKey;
    }

    public String getScientificName() {
        return scientificName;
    }

    public String getCountryCode() {
        return countryCode;
    }
    
    public int getCount() {
        return count;
    }

    @Override
    public Double getDecimalLatitude() {
        return decimalLatitude;
    }

    @Override
    public Double getDecimalLongitude() {
        return decimalLongitude;
    }

    @Override
    public LocalDate getDate() {
        return date;
    }

    @Override
    public String getNamedCategory(String category) {
        String value = "";
        
        switch (category) {
            case Occurrence.SCIENTIFICNAME:
                value = scientificName;
                break;
            case Occurrence.SPECIESKEY:
                value = speciesKey;
                break;
        }
        
        return value;
    }

    public String[] getCountLabels() {
        return countLabels;
    }
    
    public int[] getCounts() {
        int[] counts = new int[1];
        counts[0] = count;
        
        return counts;
    }
    
    public String[] getItems(int format) {
        String[] items = new String[2];
        items[0] = speciesKey;
        items[1] = scientificName;
        return items;
    }

    public String getItem(int format, String itemLabel, int index) {
        String item = null;
        
        switch(itemLabel) {
            case Occurrence.SCIENTIFICNAME:
                item = scientificName;
                break;
            case Occurrence.SPECIESKEY:
                item = speciesKey;
                break;
        }
        
        return item;
    }

    public int compareTo(Object o) {
        int comparison = 0;
        
        if (o instanceof OccurrenceBin) {
            for (int i = 0; comparison == 0 && i < offsets.length; i++) {
                comparison = this.getCellPosition()[i] - ((OccurrenceBin) o).getCellPosition()[i];
            }
        }
        
        return comparison;
    }

    @Override
    public String[] getItemLabels(int format, int maxCellSize) {
        return itemLabels;
    }
}