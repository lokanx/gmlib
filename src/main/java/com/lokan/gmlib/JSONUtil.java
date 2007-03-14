/*
 *  ----------- Identification -----------------------------------
 *  Library: gmlib
 *  File:    $RCSfile$
 *  Author:  $Author$
 *  Date:    $Date$
 *  Version: $Revision$
 *  ----------- Copyright ----------------------------------------
 *  Copyright (C) 2007 Björn Sjögren
 *  
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *  
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.

 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA  
 *  --------------------------------------------------------------
 */package com.lokan.gmlib;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;


/**
 * 
 * Simple util class for JSON related stuff.
 * This class is not ment to be instansiated.
 */
class JSONUtil {
    
    //
    // Constructors 
    //
    
    
    /**
     * Creates a new instance of <code>JSONUtil</code>.
     */
    private JSONUtil() {
        // Procied to prevent instansiations due its a util class with only statics visible 
    }
    
    
    //
    // Methods
    //
    
    
    /**
     * Returns JSON strings from an HTML document string. 
     * If no mattching JSON strings are found en empty array is returned.  
     * Example 1:
     * <pre>
     * Input:
     *    D(["name", "Some JSON Stuff",  "More JSON stuff"]);
     * Output:
     *    "[\"name\", \"Some JSON Stuff\",  \"More JSON stuff\"]"
     * </pre>
     * Example 2:
     * <pre>
     * Input:
     *    D(["name", "JSON A1",  "JSON A2"]); 
     *    D(["name", "JSON B1",  "JSON B2"]);
     * Output:
     *    "[\"name\", "\"JSON A1\", \"JSON A2\"]",
     *    "[\"name\", "\"JSON B1\", \"JSON B2\"]", 
     * </pre>
     * @param name JSON Name to search for.
     * @param html HTML to search within.
     * @return Returns found JSON strings as an array.
     * @throws GMException is thrown if input parameters are null or empty.
     */
    static String[] getJSONString(String name, String html) {
        if (name == null  ||  name.length() == 0) {
            throw new GMException("Name parameter must not be null or empty. Recived: >>" + name + "<<");
       }
       
       if (html == null  ||  html.length() == 0) {
           throw new GMException("html parameter must not be null or empty. Recieved: >>" + html + "<<");
       }
        
        List<String> result = new ArrayList<String>();        
        int pos = 0;        
        
        String pattern1 = "D([\"" + name + "\"";
        int pos1;
        do {
            pos1 = html.indexOf(pattern1, pos);
            if (pos1 > -1) {
                String pattern2 = ");";
                int pos2 = html.indexOf(pattern2, pos1);
                if (pos2 > pos1) {
                    result.add(html.substring(pos1 + 2, pos2));
                }
                pos = pos2 + pattern2.length();
            }
        } while (pos1 > -1);
        
        return result.toArray(new String[result.size()]);
    }

    /**
     * Returns an array of <code>JSONArray</code> objects from an HTML document string.
     * If no mattching JSON expressions are found en empty array is returned.
     * @param name JSON Name to search for.
     * @param html HTML to search within.
     * @return Returns found JSON expressions as an <code>JSONArray</code> array.
     * @throws GMException is thrown if found JSON expressions could not 
     *                     be parsed (syntax errors) or input parameters are null  or empty.
     */
    static JSONArray[] getJSONArray(String name, String html) {               
        if (name == null  ||  name.length() == 0) {
             throw new GMException("Name parameter must not be null or empty. Recived: >>" + name + "<<");
        }
        
        if (html == null  ||  html.length() == 0) {
            throw new GMException("html parameter must not be null or empty. Recieved: >>" + html + "<<");
        }
        
        try {
            String[] jsonStrings = getJSONString(name, html);
            JSONArray[] result = new JSONArray[jsonStrings.length];        
            for (int i = 0; i < jsonStrings.length; i++) {
                result[i] = new JSONArray(jsonStrings[i]);
            }
            return result;
        } catch (JSONException e) {
            throw new GMException("Failed parse json expresssion.", e);
        }        
    }    
}
