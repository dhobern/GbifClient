/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.dhobern.gbifclient;

import io.github.dhobern.gbifclient.utils.GbifApiRequestFactory;
import io.github.dhobern.gbifclient.utils.GbifConfiguration;
import io.github.dhobern.gbifclient.utils.GridCell;
import io.github.dhobern.gbifclient.utils.Occurrence;
import io.github.dhobern.gbifclient.utils.OccurrenceBin;
import io.github.dhobern.gbifclient.utils.OccurrenceMatrix;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
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
                    OccurrenceMatrix<OccurrenceBin,Occurrence> binMatrix = extractOccurrenceBins(entity.getContent());
                    
                    OccurrenceMatrix<GridCell,OccurrenceBin> gridMatrix = gridOccurrenceBins(binMatrix);

                    gridMatrix.exportGrid("GridExport-Ranked-" + scientificName.replace(" ", "_") + ".txt", OccurrenceMatrix.FORMAT_RANKORDER);
                    gridMatrix.exportGrid("GridExport-Occupancy-" + scientificName.replace(" ", "_") + ".txt", OccurrenceMatrix.FORMAT_OCCUPANCY);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(GbifClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static OccurrenceMatrix<OccurrenceBin,Occurrence> extractOccurrenceBins(InputStream inputStream) {
        OccurrenceMatrix<OccurrenceBin,Occurrence> binMatrix = GbifConfiguration.getOccurrenceBinMatrix();

        try {
            ZipInputStream zipStream = new ZipInputStream(inputStream);
            ZipEntry  zipEntry = zipStream.getNextEntry();
            if (zipEntry != null) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(zipStream));
                
                String line = reader.readLine();
                HashMap<String,Integer> columnIndexes = new HashMap<String,Integer>();
                String[] columnHeadings = line.split("\t");
                for (int i = 0; i < columnHeadings.length; i++) {
                    columnIndexes.put(columnHeadings[i], i);
                }
                
                Boolean requiresSpecies = GbifConfiguration.requireSpecies();
                Boolean requiresCoordinates = GbifConfiguration.requireCoordinates();
                Boolean requiresDate = GbifConfiguration.requireDate();
                Set<String> countryFilter = GbifConfiguration.getCountryFilter();
                
                Occurrence occurrence = new Occurrence(columnIndexes);
                while ((line = reader.readLine()) != null) {
                    occurrence.setValuesFromString(line);

                    if (    (!requiresSpecies || (occurrence.getSpeciesKey().length() > 0))
                         && (!requiresCoordinates || ((occurrence.getDecimalLatitude() != null) && (occurrence.getDecimalLongitude() != null)))
                         && ((countryFilter == null) || countryFilter.contains(occurrence.getCountryCode()))
                         && (!requiresDate || (occurrence.getDate() != null))) {
                        binMatrix.insert(occurrence);
                    }
                }
            }

            zipStream.closeEntry();
            zipStream.close();
        } catch (IOException ex) {
            Logger.getLogger(GbifClient.class.getName()).log(Level.SEVERE, null, ex);
        }

        /*
        exportBins(binMatrix);
        */
        
        return binMatrix;
    }
    
    private static OccurrenceMatrix<GridCell,OccurrenceBin> gridOccurrenceBins(OccurrenceMatrix<OccurrenceBin,Occurrence> binMatrix) {
        OccurrenceMatrix<GridCell,OccurrenceBin> gridMatrix = GbifConfiguration.getGridMatrix();

        Iterator<OccurrenceBin> iterator = binMatrix.getIterator();
        while (iterator.hasNext()) {
            OccurrenceBin bin = iterator.next();
            gridMatrix.insert(bin);
        }
        
        return gridMatrix;
    }
    
    /*
    private static void exportBins(OccurrenceBinManager binMatrix) {
        try {
            PrintWriter writer = new PrintWriter("BinExport-" + scientificName.replaceAll(" ", "_") + ".txt", "UTF-8");
            writer.printf("decimallatitude\tdecimallongitude\teventdate\ttaxonkey\tscientifiname\tcount\n");

            Iterator<OccurrenceBin> iterator = binMatrix.getOccurrenceBins();
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

    private static void exportGrid(GridManager gridMatrix, int maxSpecies) {
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
            
            Species[][][][] grid = gridMatrix.getGrid();
            int xRange = gridMatrix.getXRange();
            int yRange = gridMatrix.getYRange();
            int zRange = gridMatrix.getZRange();
            
            String[] xLabels = new String[xRange];
            String[] yLabels = new String[yRange];
            String[] zLabels = new String[zRange];
            
            for (int i = 0; i < xRange; i++) {
                xLabels[i] = gridMatrix.getXLabel(i);
            }
            for (int i = 0; i < yRange; i++) {
                yLabels[i] = gridMatrix.getYLabel(i);
            }
            for (int i = 0; i < zRange; i++) {
                zLabels[i] = gridMatrix.getZLabel(i);
            }
            
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
    
    }*/
}