/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.dhobern.gbifclient.utils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * 
 * @author Platyptilia
 */
public class JsonUtils {
    
    /**
     * Compares two JSONObjects.  
     * 
     * If the lenient flag is set to false, the two objects are considered to 
     * match if they have the same number of properties and all properties 
     * match. A property with a null value is considered equivalent to 
     * JSONObject.NULL but considered different from the absence of the same 
     * property.
     * 
     * If the lenient flag is set to true, the objects are considered to match
     * if all of the properties in the second object are also found on the 
     * first object.
     * 
     * Leniency does not propagate beyond the root object's properties. Any
     * properties found must match exactly.
     * 
     * @param o1 first JSONObject to compare
     * @param o2 second JSONObject to compare
     * @param lenient when true, accept missing properties on second object
     * @return true if objects match
     */
    public static boolean objectsMatch(JSONObject o1, JSONObject o2, boolean lenient) {
        boolean match = true;
        
        if ((o1.length() == o2.length()) || (lenient && (o1.length() > o2.length()))) {
            
            // Iterate over second object since extra properties on first 
            // object may be tolerated but all properties on second object
            // must match.
            Iterator<String> iterator = o2.keys();
            while (match && iterator.hasNext()) {
                try {
                    String k = iterator.next();
                    match = valuesMatch(o1.get(k), o2.get(k));
                } catch (JSONException ex) {
                    Logger.getLogger(JsonUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            match = false;
        }
       
        return match;
    }
    
    /**
     * Compares two JSONArrays.  They are considered to match if they have 
     * the same number of values and all values match in the same order. A 
     * null value is considered equivalent to JSONObject.NULL but
     * considered different from the absence of the same value.
     * 
     * @param o1 first JSONObject to compare
     * @param o2 second JSONObject to compare
     * @return true if objects match
     */
    public static boolean arraysMatch(JSONArray a1, JSONArray a2) {
        boolean match = true;
        
        if (a1.length() != a2.length()) {
            match = false;
        } else {
            for (int i = 0; i < a1.length(); i++) {
                try {
                    match = valuesMatch(a1.get(i), a2.get(i));
                } catch (JSONException ex) {
                    Logger.getLogger(JsonUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        return match;
    }
    
    private static boolean valuesMatch(Object obj1, Object obj2) {
        boolean match = false;
        
        if (obj1 == null) {
            obj1 = JSONObject.NULL;
        }
        if (obj2 == null) {
            obj2 = JSONObject.NULL;
        }
        
        if (    obj1 instanceof JSONObject
                && obj2 instanceof JSONObject) {
            match = objectsMatch((JSONObject) obj1, (JSONObject) obj2, false);
        } else if (    obj1 instanceof JSONArray
                && obj2 instanceof JSONArray)  {
            match = arraysMatch((JSONArray) obj1, (JSONArray) obj2);
        } else if (    obj1 instanceof Boolean
                && obj2 instanceof Boolean) {
            match = ((Boolean) obj1 == (Boolean) obj2);
        } else if (    obj1 instanceof Number
                && obj2 instanceof Number) {
            match = ((Number) obj1 == (Number) obj2);
        } else if (    obj1 instanceof String
                && obj2 instanceof String) {
            match = ((String) obj1).equals((String) obj2);
        }
        
        return match;
    }
}
