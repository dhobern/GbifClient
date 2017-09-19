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
public class Species implements Comparable<Species> {
    private String speciesKey;
    private String scientificName;
    private int binCount;
    private int occurrenceCount;
    
    public Species(Mappable m) {
        speciesKey = m.getNamedCategory(Occurrence.SPECIESKEY);
        scientificName = m.getNamedCategory(Occurrence.SCIENTIFICNAME);
        occurrenceCount = m.getCount();
        binCount = 1;
    }
    
    public int increment(Mappable m) {
        occurrenceCount += m.getCount();
        return ++binCount;
    }

    public String getSpeciesKey() {
        return speciesKey;
    }

    public String getScientificName() {
        return scientificName;
    }

    public int getBinCount() {
        return binCount;
    }

    public int getOccurrenceCount() {
        return occurrenceCount;
    }
    
    public String toString() {
        return scientificName;
    }

    @Override
    public int compareTo(Species o) {
        int comparison = o.getBinCount() - binCount;
        if (comparison == 0) {
            comparison = scientificName.compareTo(o.getScientificName());
        }
        return comparison;
    }

}