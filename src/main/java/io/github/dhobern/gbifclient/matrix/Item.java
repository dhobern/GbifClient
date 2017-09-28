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
public class Item {
    
    private final static String[] EMPTY = {};
    
    private final String[] elements;
    private final String[] values;
    private int count = 1;
    private int total = 1;
    
    public Item() {
        elements = values = EMPTY;
    }
    
    public Item(String[] e, String[] v) {
        elements = e;
        values = v;
    }
    
    public Item(String[] e, Item other) {
        elements = e;
        values = new String[elements.length];
        
        for (int i = 0; i < elements.length; i++) {
            values[i] = other.get(elements[i]).intern();
        }
        
        count = other.getCount();
        total = other.getTotal();
    }
    
    public String get(String element) {
        String value = null;
        
        for (int i = 0; value == null && i < elements.length; i++) {
            if (elements[i].equals(element)) {
                value = values[i];
            }
        }
        return value;
    }
    
    public void put(String element, String value) {
        for (int i = 0; value != null && i < elements.length; i++) {
            if (elements[i].equals(element)) {
                values[i] = value;
                value = null;
            }
        }
    }
    
    public void add(Item other) {
        this.count++;
        this.total += other.getTotal();
    }

    public int getCount() {
        return count;
    }

    public int getTotal() {
        return total;
    }
}