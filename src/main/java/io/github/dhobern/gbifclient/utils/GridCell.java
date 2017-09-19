/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.dhobern.gbifclient.utils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

/**
 *
 * @author Platyptilia
 */
public class GridCell extends CellValue implements Comparable {
    
    public static final String[] countLabels = new String[] { 
        "speciescount", "bincount", "occurrenceount"
    };
    
    private static HashSet<String> allSpecies = new HashSet<String>();

    private Species[] species;
    private double decimalLatitude;
    private double decimalLongitude;

    public GridCell(int[] position, Mappable m) {
        super(position, m);
        species = new Species[1];
        species[0] = new Species(m); 
        decimalLatitude = m.getDecimalLatitude();
        decimalLongitude = m.getDecimalLongitude();
        allSpecies.add(species[0].getScientificName());
    }
    
    public GridCell add(Mappable m) {
        String scientificName = m.getNamedCategory(Occurrence.SCIENTIFICNAME);
        boolean found = false;
        
        for (int i = 0; !found && i < species.length; i++) {
            if (scientificName.equals(species[i].getScientificName())) {
                species[i].increment(m);
                found = true;
            }
        }
        
        if (!found) {
            Species[] newSpecies = new Species[species.length + 1];
            System.arraycopy(species, 0, newSpecies, 0, species.length);
            newSpecies[species.length] = new Species(m);
            allSpecies.add(newSpecies[species.length].getScientificName());
            species = newSpecies;
        }
        
        return this; 
    }
    
    public void prepareForExport() {
        Arrays.sort(species);
    }

    @Override
    public String[] getCountLabels() {
        return countLabels;
    }

    @Override
    public int[] getCounts() {
        int[] counts = new int[3];
        counts[0] = species.length;
        for (int i = 0; i < species.length; i++) {
            counts[1] += species[i].getBinCount();
            counts[2] += species[i].getOccurrenceCount();
        }
        return counts;
    }
    
    public String[] getItems(int format) {
        String[] items;
        
        switch(format) {
            case OccurrenceMatrix.FORMAT_OCCUPANCY:
                items = new String[allSpecies.size()];
                Iterator<String> iterator = allSpecies.iterator();
                for (int i = 0; i < items.length && iterator.hasNext(); i++) {
                    Species s = null;
                    String nextSpecies = iterator.next();
                    for (int j = 0; s == null && j < species.length; j++) {
                        if (species[j].getScientificName().equals(nextSpecies)) {
                            s = species[j];
                        }
                    }
                    if (s == null) {
                        items[i] = "";
                    } else {
                        items[i] = new Integer(s.getBinCount()).toString();
                    }
                }
                break;
                
            default:
                items = new String[species.length];
                for (int i = 0; i < species.length; i++) {
                    items[i] = species[i].getScientificName() + " [ " + species[i].getBinCount() + " / " + species[i].getOccurrenceCount() + " ]";
                }
                break;
                
        }
        return items;
    }


    @Override
    public int compareTo(Object o) {
        int comparison = 0;
        
        if (o instanceof GridCell) {
            for (int i = 0; comparison == 0 && i < offsets.length; i++) {
                comparison = this.getCellPosition()[i] - ((GridCell) o).getCellPosition()[i];
            }
        }
        
        return comparison;
    }

    @Override
    public String[] getItemLabels(int format, int maxCellSize) {
        String[] labels;
        
        switch(format) {

            case OccurrenceMatrix.FORMAT_OCCUPANCY:
                labels = new String[allSpecies.size()];
                Iterator<String> iterator = allSpecies.iterator();
                for (int i = 0; i < labels.length && iterator.hasNext(); i++) {
                    labels[i] = iterator.next();
                }
                break;

             default:
                labels = new String[maxCellSize];
                int digits = 0;
                for (int n = maxCellSize; n > 0; n /= 10) {
                    digits++;
                }
                String formatString = "rank_%0" + digits + "d";
                for (int i = 0; i < maxCellSize; i++) {
                    labels[i] = String.format(formatString, i + 1);
                }
                break;
        }
        return labels;
    }

}
