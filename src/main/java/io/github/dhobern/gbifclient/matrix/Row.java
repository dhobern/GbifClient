 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.dhobern.gbifclient.matrix;

import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author Platyptilia
 */
public class Row {
    
    private final HashMap<String, Item> items = new HashMap<>();
    private String[] itemElements;
    
    private Row() {
        itemElements = null;
    }
    
    public Row(String[] e) {
        itemElements = e;
    }
    
    public Item insert(String columnKey, Item other) {
        Item item = items.get(columnKey);
        
        if(item == null) {
            item = new Item(itemElements, other);
            items.put(columnKey, item);
        } else {
            item.add(other);
        }

        return item;
    }
    
    public Item get(String columnKey) {
        return items.get(columnKey);
    }
    
    public Iterator<Item> columnIterator() {
        return items.values().iterator();
    }

    public HashMap<String, Item> getColumns() {
        return items;
    }

    public int getCumulativeCount() {
        int count = 0;
        Iterator<Item> iterator = columnIterator();
        while (iterator.hasNext()) {
            count += iterator.next().getCount();
        }
        return count;
    }

    public int getCumulativeTotal() {
        int total = 0;
        Iterator<Item> iterator = columnIterator();
        while (iterator.hasNext()) {
            total += iterator.next().getTotal();
        }
        return total;
    }
}