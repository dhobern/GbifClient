/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.dhobern.gbifclient;

import io.github.dhobern.gbifclient.utils.GbifApiRequestFactory;
import io.github.dhobern.gbifclient.utils.GbifConfiguration;
import io.github.dhobern.gbifclient.utils.GridManager;
import io.github.dhobern.gbifclient.utils.OccurrenceBin;
import io.github.dhobern.gbifclient.utils.OccurrenceBinManager;
import io.github.dhobern.gbifclient.utils.Species;
import io.github.dhobern.gbifclient.utils.TabDataUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

/**
 *
 * @author Platyptilia
 */
public class GbifClient {

    private static String scientificName = null;
    
    public static void main(String[] argv) throws InterruptedException {
        try {
            if (argv.length == 0) {
                System.err.println("Supply scientific name as first parameter");
            } else {
                scientificName = argv[0];
                String taxonKey = GbifApiRequestFactory.getTaxonKey(scientificName);
                
                HttpResponse response 
                    = GbifApiRequestFactory.executeDownloadRequestWait("TAXON_KEY", taxonKey);

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    OccurrenceBinManager binManager = extractOccurrenceBins(entity.getContent());
                    
                    GridManager gridManager = gridOccurrenceBins(binManager);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(GbifClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static OccurrenceBinManager extractOccurrenceBins(InputStream inputStream) {
        OccurrenceBinManager binManager = GbifConfiguration.getOccurrenceBinManager();

        try {
            ZipInputStream zipStream = new ZipInputStream(inputStream);
            ZipEntry  zipEntry = zipStream.getNextEntry();
            if (zipEntry != null) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(zipStream));
                
                String line = reader.readLine();
                int[] columnIndexes = TabDataUtils.getColumnIndexes(line);
                Boolean requiresSpecies = GbifConfiguration.requireSpecies();
                Boolean requiresCoordinates = GbifConfiguration.requireCoordinates();
                Set<String> countryFilter = GbifConfiguration.getCountryFilter();
                while ((line = reader.readLine()) != null) {
                    String[] values = TabDataUtils.getColumnValues(line, columnIndexes);
                    binManager.add(values, requiresSpecies, requiresCoordinates, countryFilter);
                }
            }

            zipStream.closeEntry();
            zipStream.close();
        } catch (IOException ex) {
            Logger.getLogger(GbifClient.class.getName()).log(Level.SEVERE, null, ex);
        }

        exportBins(binManager);
        
        return binManager;
    }
    
    private static GridManager gridOccurrenceBins(OccurrenceBinManager binManager) {
        GridManager gridManager = GbifConfiguration.getGridManager();

        Iterator<OccurrenceBin> iterator = binManager.getOccurrenceBins();
        while (iterator.hasNext()) {
            OccurrenceBin bin = iterator.next();
            gridManager.add(bin);
        }
        
        int largest = gridManager.sortBins();
        
        exportGrid(gridManager, largest);
        
        return gridManager;
    }
    
    private static void exportBins(OccurrenceBinManager binManager) {
        try {
            PrintWriter writer = new PrintWriter("BinExport-" + scientificName.replaceAll(" ", "_") + ".txt", "UTF-8");
            writer.printf("decimallatitude\tdecimallongitude\teventdate\ttaxonkey\tscientifiname\tcount\n");

            Iterator<OccurrenceBin> iterator = binManager.getOccurrenceBins();
            while (iterator.hasNext()) {
                OccurrenceBin bin = iterator.next();
                writer.printf("%f\t%f\t%s\t%s\t%s\t%d\n",
                              bin.getDecimalLatitudeCentroid(),
                              bin.getDecimalLongitudeCentroid(),
                              bin.getDateString(),
                              bin.getSpeciesKey(),
                              bin.getCanonicalName(),
                              bin.getCount());
            }
            writer.close();
        } catch (IOException e) {
            Logger.getLogger(GbifClient.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private static void exportGrid(GridManager gridManager, int maxSpecies) {
        try {
            PrintWriter writer = new PrintWriter("GridExport-" + scientificName.replaceAll(" ", "_") + ".txt", "UTF-8");
            writer.printf("decimallatitude\tdecimallongitude\ttimeperiod\ttotalspecies\ttotalbins\ttotaloccurrences");
            
            int digits = 0;
            for (int n = maxSpecies; n > 0; n /= 10) {
                digits++;
            }
            String formatString = "\trank_%0" + digits + "d";
            for (int i = 0; i < maxSpecies; i++) {
               writer.printf(formatString, i);
            }
            writer.printf("\n");
            
            Species[][][][] grid = gridManager.getGrid();
            int xRange = gridManager.getXRange();
            int yRange = gridManager.getYRange();
            int zRange = gridManager.getZRange();
            
            String[] xLabels = new String[xRange];
            String[] yLabels = new String[yRange];
            String[] zLabels = new String[zRange];
            
            for (int i = 0; i < xRange; i++) {
                xLabels[i] = gridManager.getXLabel(i);
            }
            for (int i = 0; i < yRange; i++) {
                yLabels[i] = gridManager.getYLabel(i);
            }
            for (int i = 0; i < zRange; i++) {
                zLabels[i] = gridManager.getZLabel(i);
            }
            
            /* Outer loop iterates over latitude since this is better order */ 
            for (int j = 0; j < yRange; j++) {
                for (int i = 0; i < xRange; i++) {
                    for (int k = 0; k < zRange; k++) {
                        Species[] cell = grid[i][j][k];
                        if (cell.length > 0) {
                            int occurrenceCount = 0;
                            int binCount = 0;
                            for(int s = 0; s < cell.length; s++) {
                                occurrenceCount += cell[s].getOccurrenceCount();
                                binCount += cell[s].getBinCount();
                            }

                            writer.printf("%s\t%s\t%s\t%d\t%d\t%d",
                                    yLabels[j], xLabels[i], zLabels[k],
                                    cell.length, binCount, occurrenceCount);

                            for (int s = 0; s < maxSpecies; s++) {
                                writer.printf("\t");
                                if (s < cell.length) {
                                    writer.printf("%s [%d / %d]", cell[s].getCanonicalName(), cell[s].getBinCount(), cell[s].getOccurrenceCount());
                                }
                            }
                            
                            writer.printf("\n");
                        }
                    }
                }
            }

            writer.close();
        } catch (IOException e) {
            Logger.getLogger(GbifClient.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}