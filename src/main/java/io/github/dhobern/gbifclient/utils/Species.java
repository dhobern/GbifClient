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
    private String canonicalName;
    private int binCount;
    private int occurrenceCount;
    
    public Species(OccurrenceBin bin) {
        speciesKey = bin.getSpeciesKey();
        canonicalName = bin.getCanonicalName();
        occurrenceCount = bin.getCount();
        binCount = 1;
    }
    
    public int increment(OccurrenceBin bin) {
        occurrenceCount += bin.getCount();
        return ++binCount;
    }

    public String getSpeciesKey() {
        return speciesKey;
    }

    public String getCanonicalName() {
        return canonicalName;
    }

    public int getBinCount() {
        return binCount;
    }

    public int getOccurrenceCount() {
        return occurrenceCount;
    }
    
    public String toString() {
        return canonicalName;
    }

    @Override
    public int compareTo(Species o) {
        int comparison = o.getBinCount() - binCount;
        if (comparison == 0) {
            comparison = canonicalName.compareTo(o.getCanonicalName());
        }
        return comparison;
    }

}