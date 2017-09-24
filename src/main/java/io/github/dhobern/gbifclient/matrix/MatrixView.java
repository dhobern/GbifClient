/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.dhobern.gbifclient.matrix;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Platyptilia
 */
public class MatrixView {
    
    private static final int SECTION_LABEL = 0;
    private static final int SECTION_COUNT = 1;
    private static final int SECTION_VALUE = 2;
    
    private static final String[] COUNT_LABELS = { "rows", "bins", "occurrences" };

    private MultidimensionMatrix matrix;
    
    public MatrixView(MultidimensionMatrix m) {
        matrix = m;
    }
    
    public void outputMatrix(Writer writer) {
        MatrixDimensions domainDimensions = matrix.getDomainDimensions();
        MatrixDimensions rangeDimensions = matrix.getRangeDimensions();
        List<String> columnKeys = MultidimensionMatrix.sortKeys(matrix.getColumnKeys());
        List<String> rowKeys = MultidimensionMatrix.sortKeys(matrix.getRowKeys());
        
        int[] columnCounts = new int[3];
        columnCounts[SECTION_LABEL] = domainDimensions.getSelectors().size();
        columnCounts[SECTION_COUNT] = 3;
        columnCounts[SECTION_VALUE] = columnKeys.size();

        try {        

            // Write rows for column headings - one row for each element of range dimension
            for (int i = 0; i < rangeDimensions.getSelectors().size(); i++) {
                CategorySelector selector = rangeDimensions.getSelectors().get(i);
                writeEmptyCells(writer, columnCounts[SECTION_LABEL] + columnCounts[SECTION_COUNT]);
                writer.write(selector.getName() + "\t");
                for (int j = 0; j < columnCounts[SECTION_VALUE]; j++) {
                    writer.write(selector.getCategoryLabel(rangeDimensions.unlock(columnKeys.get(j))[i]) + "\t");
                }
                writer.write("\n");
            }
            
            // Write rows for column counts
            int[][] counts = new int[columnCounts[SECTION_VALUE]][3];
            for (int i = 0; i < columnCounts[SECTION_VALUE]; i++) {
                matrix.getCountsForColumn(columnKeys.get(i), counts[i]);
            }
            for (int i = 0; i < 3; i++) {
                writeEmptyCells(writer, columnCounts[SECTION_LABEL] + columnCounts[SECTION_COUNT]);
                writer.write(COUNT_LABELS[i] + "\t");
                for (int j = 0; j < columnCounts[SECTION_VALUE]; j++) {
                    writer.write(counts[j][i] + "\t");
                }
                writer.write("\n");
            }
            
            // Write row for domain dimension and row count labels
            for (int i = 0; i < columnCounts[SECTION_LABEL]; i++) {
                CategorySelector selector = domainDimensions.getSelectors().get(i);
                writer.write(selector.getName() + "\t");
            }
            writer.write("columns\tbins\toccurrences\t");
            writeEmptyCells(writer, columnCounts[SECTION_VALUE] + 1);
            writer.write("\n");
            
            // Write rows with values
            ArrayList<CategorySelector> selectors = domainDimensions.getSelectors();
            for (int r = 0; r < rowKeys.size(); r++) {
                String rowKey = rowKeys.get(r);
                Row row = matrix.getRow(rowKey);
                for (int i = 0; i < columnCounts[SECTION_LABEL]; i++) {
                    CategorySelector selector = selectors.get(i);
                    writer.write(selector.getCategoryLabel(domainDimensions.unlock(rowKey)[i]) + "\t");
                }
                writer.write(row.getColumns().size() + "\t");
                writer.write(row.getCumulativeCount() + "\t");
                writer.write(row.getCumulativeTotal() + "\t" + "\t");
                
                for (int i = 0; i < columnCounts[SECTION_VALUE]; i++) {
                    Item item = row.get(columnKeys.get(i));
                    if (item == null) {
                        writer.write("\t");
                    } else {
                        writer.write(item.getCount() + "\t");
                    }
                }
                writer.write("\n");
            }
            
        } catch (IOException ex) {
            Logger.getLogger(MatrixView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void writeEmptyCells(Writer writer, int count) {
        try {
            for (int i = 0; i < count; i++) {
                writer.write("\t");
            }
        } catch (IOException ex) {
            Logger.getLogger(MatrixView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
