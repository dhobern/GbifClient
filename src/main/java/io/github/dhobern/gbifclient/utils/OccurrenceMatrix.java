/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.dhobern.gbifclient.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Platyptilia
 * @param <S>
 * @param <T>
 */
public class OccurrenceMatrix<S extends CellValue & Comparable,T extends Mappable> {
    
    public static final int FORMAT_DEFAULT = 0;
    public static final int FORMAT_RANKORDER = 1;
    public static final int FORMAT_OCCUPANCY = 2;
    
    private MatrixDimensions dimensions;
    private HashMap<String,S> matrix = new HashMap<String,S>();
    private CellValueFactory<S> factory;
    
    private OccurrenceMatrix() {
    }
    
    public OccurrenceMatrix(MatrixDimensions d, CellValueFactory f) {
        dimensions = d;
        factory = f;
    }
    
    public S insert(T m) {
        int[] position = dimensions.getCellPosition(m);
        String key = dimensions.getCellKey(position);
        
        S cellValue = matrix.get(key);
        
        if (cellValue == null) {
            cellValue = factory.createCellValue(position, m);
            matrix.put(key, cellValue);
        } else {
            cellValue.add(m);
        }
     
        return cellValue;
    }
    
    public Iterator<S> getIterator() {
        return matrix.values().iterator();
    }
    
    public int[] getDimensions() {
        return dimensions.getDimensions(); 
    }
    
    public S getCellValue(int[] offsets) {
        return matrix.get(dimensions.getCellKey(offsets));
    }

    public void exportGrid(String fileName, int format) {
        ArrayList<S> cells = new ArrayList<S>(matrix.values());
        Collections.sort(cells);

        int maxCellSize = 0;

        Iterator<S> iterator = cells.iterator();
        while(iterator.hasNext()) {
            S cell = iterator.next();
            cell.prepareForExport();
            int size = cell.getCounts()[0];
            if (size > maxCellSize) {
                maxCellSize = size;
            }
        }

        if (GbifConfiguration.rotateMatrix()) {
            exportGridRotated(fileName, cells, format, maxCellSize);
        } else {
            exportGridDefault(fileName, cells, format, maxCellSize);
        }
    }

    
    private void exportGridDefault(String fileName, ArrayList<S> cells, int format, int maxCellSize) {
        try {
            PrintWriter writer = new PrintWriter(fileName, "UTF-8");

            ArrayList<CategorySelector> selectors = dimensions.getSelectors();
            for (int i = 0; i < selectors.size(); i++) {
                if (i > 0) {
                    writer.printf("\t");
                }
                writer.printf(selectors.get(i).getName());
            }
            String[] countLabels = cells.get(0).getCountLabels();
            for (int i = 0; i < countLabels.length; i++) {
                writer.printf("\t%s", countLabels[i]);
            }
            String[] itemLabels = cells.get(0).getItemLabels(format, maxCellSize);
            for (int i = 0; i < itemLabels.length; i++) {
                writer.printf("\t%s", itemLabels[i]);
            }
            writer.printf("\n");

           Iterator<S> iterator = cells.iterator();
            while(iterator.hasNext()) {
                S cell = iterator.next();
                int[] position = cell.getCellPosition();
                for (int i = 0; i < position.length && i < selectors.size(); i++) {
                    if (i > 0) {
                        writer.printf("\t");
                    }
                    if (position[i] < 0) {
                        writer.printf("UNSPECIFIED");
                    } else {
                        writer.printf("%s", selectors.get(i).getCategoryLabel(position[i]));
                    }
                }
                int[] counts = cell.getCounts();
                for (int i = 0; i < counts.length; i++) {
                    writer.printf("\t%d", counts[i]);
                }
                String[] items = cell.getItems(format);
                for (int i = 0; i < items.length; i++) {
                    writer.printf("\t%s", items[i]);
                }
                writer.printf("\n");
            }

            writer.close();
        } catch (IOException e) {
            Logger.getLogger(OccurrenceMatrix.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    
    private void exportGridRotated(String fileName, ArrayList<S> cells, int format, int maxCellSize) {
        try {
            PrintWriter writer = new PrintWriter(fileName, "UTF-8");

            ArrayList<CategorySelector> selectors = dimensions.getSelectors();

            for (int i = 0; i < selectors.size(); i++) {
                writer.printf(selectors.get(i).getName());
                Iterator<S> iterator = cells.iterator();
                while(iterator.hasNext()) {
                    S cell = iterator.next();
                    int[] position = cell.getCellPosition();
                    if (position[i] < 0) {
                        writer.printf("\tUNSPECIFIED");
                    } else {
                        writer.printf("\t%s", selectors.get(i).getCategoryLabel(position[i]));
                    }
                }
                writer.printf("\n");
            }
            String[] countLabels = cells.get(0).getCountLabels();
            for (int i = 0; i < countLabels.length; i++) {
                writer.printf("%s", countLabels[i]);
                Iterator<S> iterator = cells.iterator();
                while(iterator.hasNext()) {
                    S cell = iterator.next();
                    writer.printf("\t%d", cell.getCounts()[i]);
                }
                writer.printf("\n");
            }
            String[] itemLabels = cells.get(0).getItemLabels(format, maxCellSize);
            for (int i = 0; i < itemLabels.length; i++) {
                String itemLabel = itemLabels[i];
                writer.printf("%s", itemLabel);
                Iterator<S> iterator = cells.iterator();
                while(iterator.hasNext()) {
                    S cell = iterator.next();
                    writer.printf("\t%s", cell.getItem(format, itemLabel, i));
                }
                writer.printf("\n");
            }

            writer.close();
        } catch (IOException e) {
            Logger.getLogger(OccurrenceMatrix.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}