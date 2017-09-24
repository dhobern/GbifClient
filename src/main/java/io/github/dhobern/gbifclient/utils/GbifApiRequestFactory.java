/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.dhobern.gbifclient.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author Platyptilia
 */
public class GbifApiRequestFactory {
    
    private static final String DOWNLOAD_REQUEST_URL = "https://api.gbif.org/v1/occurrence/download/request";
    private static final String DOWNLOAD_GET_URL_BASE = "https://api.gbif.org/v1/occurrence/download/request/";
    private static final String SPECIES_GET_URL_BASE = "https://api.gbif.org/v1/species/";
    private static final String SPECIES_MATCH_URL_BASE = "https://api.gbif.org/v1/species/match/?name=";
    private static final String LIST_DOWNLOADS_URL_BASE = "https://api.gbif.org/v1/occurrence/download/user/";
    private static final String ENUMERATION_GET_URL_BASE = "https://api.gbif.org/v1/enumeration/basic/";
    
    private static CredentialsProvider provider;
    
    private static HashMap<String,String> cachedNames;
    
    static {
        provider = new BasicCredentialsProvider();
        
        UsernamePasswordCredentials credentials
            = new UsernamePasswordCredentials(
                    GbifConfiguration.getGbifUser(), 
                    GbifConfiguration.getGbifPassword());
        provider.setCredentials(AuthScope.ANY, credentials);
    }
    
    private static HttpClient getHttpClient() {
        return HttpClientBuilder.create()
            .setDefaultCredentialsProvider(provider)
            .build();
    }
    
    public static HttpUriRequest createDownloadRequest(String jsonRequest) {
        HttpPost post = null;
        
        try {
            post = new HttpPost(DOWNLOAD_REQUEST_URL);
            post.addHeader("content-type", "application/json");
            post.setEntity(new StringEntity(jsonRequest));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(GbifApiRequestFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
 
        return post;
    }

    public static String executeDownloadRequest(String k, String v) throws IOException {
        String jsonRequest = constructJsonRequest(k, v);
        
        String key = findExistingDownloadKey(jsonRequest);
        
        if (key == null) {
            HttpResponse response = getHttpClient().execute(createDownloadRequest(jsonRequest));;
            if (response.getStatusLine().getStatusCode() == 201) {
                HttpEntity entity;
                BufferedReader reader;
                entity = response.getEntity();
                if ( entity != null ) {
                    reader = new BufferedReader(new InputStreamReader(entity.getContent()));
                    key = reader.readLine();
                    String message = new StringBuffer("Download Key: ").append(key).toString();
                    Logger.getLogger(GbifApiRequestFactory.class.getName()).log(Level.INFO, message, key);
                }
            }
        }
        
        return  key;
    }
    
    public static HttpUriRequest createDownloadGet(String k) {
        return new HttpGet(
                new StringBuilder(DOWNLOAD_GET_URL_BASE).append(k).toString());
    }

    public static HttpResponse executeDownloadGet(String k) throws IOException {
        return getHttpClient().execute(createDownloadGet(k));
    }
    
    public static HttpResponse executeDownloadGetWait(String k) throws IOException {
        int attempts = 0;
        HttpResponse response = null;

        while (response == null && attempts++ < 100) {
            HttpResponse r = executeDownloadGet(k);
            int status = r.getStatusLine().getStatusCode();
            String message = new StringBuffer("Wait status: ").append(status).toString();
            Logger.getLogger(GbifApiRequestFactory.class.getName()).log(Level.INFO, message);
            if(status == 200) {
                response = r;
            } else {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(GbifApiRequestFactory.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        return response;
    }
    
    public static HttpResponse executeDownloadRequestWait(String k, String v) throws IOException {
        String key = executeDownloadRequest(k, v);        
        return executeDownloadGetWait(key);
    }
    
    private static String constructJsonRequest(String k, String v) {
        return new StringBuilder("{ \"creator\":\"")
        .append(GbifConfiguration.getGbifUser())
        .append("\", \"notificationAddresses\": [\"")
        .append(GbifConfiguration.getEmail())
        .append("\"], \"format\":\"")
        .append(GbifConfiguration.getFormat())
        .append("\", \"predicate\": { \"type\":\"equals\", \"key\":\"")
        .append(k)
        .append("\", \"value\":\"")
        .append(v)
        .append("\" } }")
        .toString();
    }
    
    public static HttpUriRequest createSpeciesGet(String k) {
        return new HttpGet(
                new StringBuilder(SPECIES_GET_URL_BASE).append(k.replace(" ", "+")).toString());
    }

    public static HttpResponse executeSpeciesGet(String k) throws IOException {
        return getHttpClient().execute(createSpeciesGet(k));
    }
    
    public static HttpUriRequest createSpeciesMatch(String n) {
        return new HttpGet(
                new StringBuilder(SPECIES_MATCH_URL_BASE).append(n.replace(" ", "+")).toString());
    }

    public static HttpResponse executeSpeciesMatch(String n) throws IOException {
        return getHttpClient().execute(createSpeciesMatch(n));
    }
    
    public static HttpUriRequest createListDownloads() {
        return new HttpGet(
                new StringBuilder(LIST_DOWNLOADS_URL_BASE).append(GbifConfiguration.getGbifUser()).toString());
    }

    public static HttpResponse executeListDownloads() throws IOException {
        return getHttpClient().execute(createListDownloads());
    }
    
    public static JSONObject readJsonEntity(HttpResponse response) throws JSONException {
        JSONObject object = null;
        BufferedReader reader = null;
        try {
            HttpEntity entity = response.getEntity();
            StringBuffer sb = new StringBuffer();
            reader = new BufferedReader(new InputStreamReader(entity.getContent()));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            object = new JSONObject(sb.toString());
        } catch (IOException ex) {
            Logger.getLogger(GbifApiRequestFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalStateException ex) {
            Logger.getLogger(GbifApiRequestFactory.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(GbifApiRequestFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return object;
    }
    
    public static JSONArray readJsonEntityAsArray(HttpResponse response) throws JSONException {
        JSONArray array = null;
        BufferedReader reader = null;
        try {
            HttpEntity entity = response.getEntity();
            StringBuffer sb = new StringBuffer();
            reader = new BufferedReader(new InputStreamReader(entity.getContent()));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            array = new JSONArray(sb.toString());
        } catch (IOException ex) {
            Logger.getLogger(GbifApiRequestFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalStateException ex) {
            Logger.getLogger(GbifApiRequestFactory.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(GbifApiRequestFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return array;
    }
    
    public static String getScientificName(String taxonKey) {
        if (cachedNames == null) {
            cachedNames = new HashMap<String,String>();
        }
        
        String scientificName = cachedNames.get(taxonKey);
        
        if (scientificName == null || scientificName.length() == 0) {
            try {
                JSONObject object = readJsonEntity(executeSpeciesGet(taxonKey));
                scientificName = object.getString("scientificName");

                cachedNames.put(taxonKey, scientificName);
            } catch (Exception ex) {
                Logger.getLogger(GbifApiRequestFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return scientificName;
    }
    
    public static String getTaxonKey(String scientificName) {
        String taxonKey = null;
        
        try {
            JSONObject object = readJsonEntity(executeSpeciesMatch(scientificName));
            String matchType = object.getString("matchType");
            if (matchType != null && matchType.equals("EXACT")) {
                taxonKey = object.getString("usageKey");
            }
        } catch (Exception ex) {
            Logger.getLogger(GbifApiRequestFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return taxonKey;
    }

    private static String findExistingDownloadKey(String jsonRequest) {
        String downloadKey = null;
        Integer cacheDownloads = GbifConfiguration.getCacheDownloads();
        
        if (cacheDownloads > 0) {
            ZonedDateTime threshold = ZonedDateTime.now().minusDays(cacheDownloads);
            
            try {
                JSONObject object = readJsonEntity(executeListDownloads());
                JSONArray array = object.getJSONArray("results");
                JSONObject thisRequest = new JSONObject(jsonRequest);
                if (array != null) {
                    for (int i = 0; downloadKey == null && i < array.length(); i++) {
                        JSONObject d = array.getJSONObject(i);
                        JSONObject request = d.getJSONObject("request");
                        String status = d.getString("status");
                        ZonedDateTime timestamp = ZonedDateTime.parse(d.getString("created"),DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ").withZone(ZoneId.of("Europe/Berlin")));
                        if (    JsonUtils.objectsMatch(request, thisRequest, true)
                             && timestamp.isAfter(threshold)
                             && (    status.equals("SUCCEEDED")
                                  || status.equals("PREPARING")
                                  || status.equals("RUNNING"))) {
                            downloadKey = d.getString("key");
                        }
                    }                   
                }
            } catch (Exception ex) {
                Logger.getLogger(GbifApiRequestFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return downloadKey;
    }

    public static HttpUriRequest createGetEnumeration(String n) {
        return new HttpGet(
                new StringBuilder(ENUMERATION_GET_URL_BASE).append(n).toString());
    }

    public static HttpResponse executeGetEnumeration(String n) throws IOException {
        return getHttpClient().execute(createGetEnumeration(n));
    }
    
    public static String[] getEnumeration(String n) {
        String[] enumeration = null;
        
        try {
            JSONArray array = readJsonEntityAsArray(executeGetEnumeration(n));
            enumeration = new String[array.length()];
            for (int i = 0; i < enumeration.length; i++) {
                enumeration[i] = array.getString(i);
            }
        } catch (Exception ex) {
            Logger.getLogger(GbifApiRequestFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return enumeration;
    }

}