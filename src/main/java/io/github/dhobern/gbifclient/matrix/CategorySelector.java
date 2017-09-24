/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.dhobern.gbifclient.matrix;

/**
 *
 * @author Platyptilia
 */
public interface CategorySelector {
    
    /**
     * Returns the number of categories used by the selector, or -1 if the
     * selector has undefined cardinality.
     * 
     * If the cardinality of a selector is undefined, it cannot be used to
     * index into an OccurrenceMatrix. It can still be used to organise Mappable objects
     * but the contents of the OccurrenceMatrix can then only be retrieved 
     * using the Iterator.
     * 
     * @return
     */
    public int getCategoryCount();
    
    public int getCategory(Item item);
    
    public String getCategoryLabel(int index);
    
    public String getName();

    public String[] getRequiredElements();
}
