/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.dhobern.gbifclient.matrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Platyptilia
 */
public class MultidimensionMatrix {
    
    private MatrixDimensions domainDimensions;
    private MatrixDimensions rangeDimensions;
    private HashMap<String,Row> matrix = new HashMap<>();
    private HashSet<String> columnKeys = new HashSet<>();
    private String[] requiredElements;
    
    private MultidimensionMatrix() {
    }
    
    public MultidimensionMatrix(MatrixDimensions d, MatrixDimensions r) {
        domainDimensions = d;
        rangeDimensions = r;
        
        HashSet<String> required = new HashSet<>();
        
        Iterator<CategorySelector> iterator 
                = domainDimensions.getSelectors().iterator();
        
        while (iterator.hasNext()) {
            String[] selectorRequired = iterator.next().getRequiredElements();
            for (int i = 0; i < selectorRequired.length; i++) {
                required.add(selectorRequired[i]);
            }
        }
        
        iterator = rangeDimensions.getSelectors().iterator();
        
        while (iterator.hasNext()) {
            String[] selectorRequired = iterator.next().getRequiredElements();
            for (int i = 0; i < selectorRequired.length; i++) {
                required.add(selectorRequired[i]);
            }
        }
        
        requiredElements = required.toArray(new String[required.size()]);
    }

    public Item insert(Item item) {
        String rowKey = domainDimensions.lock(domainDimensions.getCellPosition(item)).intern();
        String columnKey = rangeDimensions.lock(rangeDimensions.getCellPosition(item)).intern();
        
        Row row = matrix.get(rowKey);
        
        if(row == null) {
            row = new Row(requiredElements);
            matrix.put(rowKey, row);
        } 
        
        columnKeys.add(columnKey);
        Item outputItem = row.insert(columnKey, item);
                    
        return outputItem;
    }
    
    public MatrixDimensions getDomainDimensions() {
        return domainDimensions; 
    }
    
    public MatrixDimensions getRangeDimensions() {
        return rangeDimensions; 
    }
    
    private void collectRequiredElements(HashSet<String> set, 
                                         MatrixDimensions dimensions) {
        Iterator<CategorySelector> iterator 
                = dimensions.getSelectors().iterator();
        
        while (iterator.hasNext()) {
            String[] selectorRequired = iterator.next().getRequiredElements();
            for (int i = 0; i < selectorRequired.length; i++) {
                set.add(selectorRequired[i]);
            }
        }
    }
    
    public Row getRow(String rowKey) {
        return matrix.get(rowKey);
    }
    
    public Iterator<Row> rowIterator() {
        return matrix.values().iterator();
    }

    public Set<String> getColumnKeys() {
        return columnKeys;
    }

    public Set<String> getRowKeys() {
        return matrix.keySet();
    }

    public static List<String> sortKeys(Set<String> keys) {
        List<String> list = new ArrayList<>(keys);
        Collections.sort(list);
        return list;
    }

    public void getCountsForColumn(String columnKey, int[] counts) {
        Iterator<Row> rows = rowIterator();
        while (rows.hasNext()) {
            Item item = rows.next().get(columnKey);
            if (item != null) {
                counts[0]++;
                counts[1] += item.getCount();
                counts[2] += item.getTotal();
            }
        }
    }
    
    protected String[] getRequiredElements() {
        return requiredElements;
    }

    public void addRequirements(MultidimensionMatrix other) {
        String[] otherRequired = other.getRequiredElements();
        
        for (int i = 0; i < otherRequired.length; i++) {
            boolean found = false;
            for (int j = 0; !found && j < requiredElements.length; j++) {
                if(requiredElements[j].equals(otherRequired[i])) {
                    found = true;
                }
            }
            if(!found) {
                String[] newRequired = new String[requiredElements.length + 1];
                System.arraycopy(requiredElements, 0, newRequired, 0, requiredElements.length);
                newRequired[requiredElements.length] = otherRequired[i];
                requiredElements = newRequired;
            }
        }
    }
}