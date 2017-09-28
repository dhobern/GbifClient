/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.dhobern.gbifclient;

import io.github.dhobern.gbifclient.matrix.Item;
import io.github.dhobern.gbifclient.matrix.MatrixView;
import io.github.dhobern.gbifclient.matrix.MultidimensionMatrix;
import io.github.dhobern.gbifclient.matrix.Row;
import io.github.dhobern.gbifclient.utils.GbifApiRequestFactory;
import io.github.dhobern.gbifclient.utils.GbifConfiguration;
import io.github.dhobern.gbifclient.utils.Occurrence;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
            if (argv.length > 0) {
                GbifConfiguration.processArgV(argv);
            }
            scientificName = GbifConfiguration.getScientificName();

            if (scientificName == null || scientificName.length() == 0) {
                System.err.println("Please supply scientific name in configuration file or as command line parameter assignment for scientificname");
                System.err.println("   Example: java io.github.dhobern.gbifclient.GbifClient \"scientificname=Cactaceae\"");
            } else {
                String taxonKey = GbifApiRequestFactory.getTaxonKey(scientificName);

                if (taxonKey == null) {
                    System.err.println("Unrecognised scientific name: " + scientificName);
                } else {
                    HttpResponse response 
                            = GbifApiRequestFactory.executeDownloadRequestWait("TAXON_KEY", taxonKey);

                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        MultidimensionMatrix gridMatrix = GbifConfiguration.getGridMatrix();
                        MultidimensionMatrix binMatrix = GbifConfiguration.getOccurrenceBinMatrix();
                        
                        binMatrix.addRequirements(gridMatrix);

                        extractOccurrenceBins(binMatrix, entity.getContent());

                        /*
                        MatrixView view = new MatrixView(binMatrix);
                        PrintWriter writer = new PrintWriter("BinExport-" + scientificName.replace(" ", "_") + ".txt", "UTF-8");
                        view.outputMatrix(writer);
                        writer.close();
                        */
                        
                        gridOccurrenceBins(gridMatrix, binMatrix);

                        MatrixView view = new MatrixView(gridMatrix);
                        PrintWriter writer = new PrintWriter("GridExport-" + scientificName.replace(" ", "_") + ".txt", "UTF-8");
                        view.outputMatrix(writer);
                        writer.close();
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(GbifClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void extractOccurrenceBins(MultidimensionMatrix binMatrix, InputStream inputStream) {
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
    }
    
    private static void gridOccurrenceBins(MultidimensionMatrix gridMatrix, MultidimensionMatrix binMatrix) {
        Iterator<Row> rows = binMatrix.rowIterator();
        while (rows.hasNext()) {
            Iterator<Item> columns = rows.next().columnIterator();
            while (columns.hasNext()) {
                Item item = columns.next();
                gridMatrix.insert(item);
            }
        }
    }
}