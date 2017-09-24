/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.dhobern.gbifclient.utils;

import io.github.dhobern.gbifclient.matrix.CategorySelector;
import io.github.dhobern.gbifclient.matrix.Item;
import java.util.HashMap;

/**
 *
 * @author Platyptilia
 */
public class SpeciesSelector implements CategorySelector {
    
    private final HashMap<String,String> taxa = new HashMap<>();
    
    private static String[] requiredElements = {
        Occurrence.SCIENTIFICNAME, Occurrence.SPECIESKEY, Occurrence.TAXONKEY
    };

    public SpeciesSelector() {
    }

    @Override
    public int getCategoryCount() {
        return -1;
    }

    @Override
    public int getCategory(Item item) {
        String species = item.get(Occurrence.SPECIESKEY);
        String taxon = item.get(Occurrence.TAXONKEY);
        String scientificname = item.get(Occurrence.SCIENTIFICNAME);
        
        if (species.length() != 0 && !species.equals(taxon)) {
            scientificname = GbifApiRequestFactory.getScientificName(species);
            item.put(Occurrence.SCIENTIFICNAME, scientificname); 
            item.put(Occurrence.TAXONKEY, species);
            taxon = species;
        }
        
        taxa.put(taxon, scientificname);
        
        return new Integer(taxon).intValue();
    }

    @Override
    public String getCategoryLabel(int index) {
        String label = taxa.get(new Integer(index).toString());
        return (label == null) ? "" : label;
    }
    
    public String getName() {
        return "scientificname";
    }

    public String[] getRequiredElements() {
        return requiredElements;
    }
}