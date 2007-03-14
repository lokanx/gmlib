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

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * This class represents a GM session.
 * All operations within this library requires a <code>GMSession</code> instance.
 * <b>Note:</b> The class has no "keep alive" functionality so inactive sessions could be terminated
 * on the google side (cookie expires).
 */
public class GMSession {
    
    //
    // Logger
    //
    
    /** Application logger. */
    private static final Logger  APP_LOG =  LogManager.getLogger(GMSession.class);
    
    
    //
    // Constants
    //

    /** User agent. */
    final static String USER_AGENT =
        "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.7.7) Gecko/20050414 Firefox/1.0.3";
    
    
    /** Login. */
    final static String LINK_LOGIN =
        "https://www.google.com/accounts/ServiceLoginAuth";
    
    /** Login2. */
    final static String LINK_LOGIN2 =
        "https://www.google.com/accounts/";
    
    
    /** Link referer. */
    final static String LINK_REFER =
        "https://www.google.com/accounts/ServiceLoginBox?service=mail&continue=https%3A%2F%2Fgmail.google.com%2Fgmail";
        //"https://www.google.com/accounts/ServiceLogin?service=mail&passive=true&rm=false&continue=https%3A%2F%2Fmail.google.com%2Fmail%3Fui%3Dhtml%26zy%3Dl";
    

    /** Logout. */
    final static String LINK_LOGOUT =
        "https://gmail.google.com/gmail?logout";
    
    /** Login site. */
    final static String LOGON_SITE = "gmail.google.com";
        
    /** Gmail link. */ 
    final static String LINK_GMAIL = "https://mail.google.com/mail";
    
    //
    // Member variables
    //
   
    
    /** Username. */
    private String username;
    
    /** Password. */
    private String password;
 
    /** HTTP client. */
    private HttpClient client;
    
    /** Proxy. */
    private Proxy proxy;
    
    /** IK. */
    private String ik;
    
    /** Cookie map. */
    private Map<String, Cookie> cookieMap;
    
    /** Randommizer. */
    private static Random random = new Random(System.currentTimeMillis());
    
    //
    // Constructors
    //
    
    /**
     * Creates a new instance of <code>GMSession</code> and performs a login.
     * @param username Username to authenticate with.
     * @param password Password to autenticate with.
     * @throws GMException Throws exception if authentication fails for some reason.
     */
    public GMSession(String username, String password) {
        this(username, password, null);
    }
    
    /**
     * Creates a new instance of <code>GMSession</code> and performs a login.
     * @param username Username to authenticate with.
     * @param password Password to autenticate with.
     * @param proxy Proxy to use.
     * @throws GMException Throws exception if authentication fails for some reason.
     */
    public GMSession(String username, String password, Proxy proxy)  {
        if (APP_LOG.isDebugEnabled()) {
            APP_LOG.debug("Creating new GMSession(\"" + username + "\", \"***********\")");
        }        
        this.username = username;
        this.password = password;
        this.proxy = proxy;
        this.ik = null;
        login();
    }
    
        
    
    //
    // Mutators
    //
    
    
    static {
        // Initiliza user agent system property.
        System.getProperties().setProperty("httpclient.useragent", USER_AGENT);
    }
    
    
    /**
     * Performs a login.
     * @throws GMException Throws exception if authentication fails for some reason.
     */
    @SuppressWarnings("deprecation")
    void login() {    
        
        MultiThreadedHttpConnectionManager connManager = new MultiThreadedHttpConnectionManager();
        client = new HttpClient(connManager);
        client.getState().setCookiePolicy(CookiePolicy.COMPATIBILITY);
        client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        client.getParams().setSoTimeout(30000);
        if (proxy != null) {
            client.getHostConfiguration().setProxy(proxy.getProxy(), proxy.getProxyPort());
            if (proxy.isAuthenticationNeeded()) {
                Credentials defaultcreds = new UsernamePasswordCredentials(
                        proxy.getProxyUsername(), proxy.getProxyPassword());
                        client.getState().setProxyCredentials(AuthScope.ANY, defaultcreds);
            }
        }
        

        /* create first post request to login */
        PostMethod post = new PostMethod(LINK_LOGIN);

        // parameters
        NameValuePair[] data = {
        		new NameValuePair("service", "mail"),
                new NameValuePair("Email", username),
                new NameValuePair("Passwd", password),
                new NameValuePair("null", "Sign in"),
                new NameValuePair(
                		"continue",
                        "https://gmail.google.com/gmail")
        };
        post.addRequestHeader("referer", LINK_REFER);
        post.addRequestHeader("Content-Type",
                "application/x-www-form-urlencoded");
        post.setRequestBody(data);
        if (APP_LOG.isDebugEnabled()) {
            APP_LOG.debug("Request: " + post);
        }
        int statusCode = -1;
        String result = null;
        try {

            // execute the method.
            statusCode = client.executeMethod(post);
            // Check we actually get status code correctly
            if (statusCode == -1) {
                throw new GMException("Exception reading HTTP, status code = -1");
            }
            // Get response String & Release the connection.
            
            result = post.getResponseBodyAsString();
        } catch (IOException e) {
            throw new GMException("Failed to open url.", e);
        } finally {
            post.releaseConnection();
        }


        if (result == null) {
            throw new IllegalStateException("Could not happen, who wrote this.");
        }

        // check if connect failed
        if (result.indexOf("errormsg") > 0) {

            // check if connect failed
            if (result.indexOf(
                        "Enter the letters as they are shown in the image above.") > 0) {
                APP_LOG.warn("Login too much, google request image login: "
                        + result);
                throw new GMException("Failed to login due to image login required.");
            } else {
                APP_LOG.warn("Connect failed: " + result);
                throw new GMException("Failed to login.");
            }
            
        }

        // get cookies from gmail
        Cookie[] logoncookies = client.getState().getCookies();
        if (logoncookies.length == 0) {
            if (APP_LOG.isDebugEnabled()) {
                APP_LOG.debug("No cookies");
            }
        } else {
            String cookies = "";
            for (int i = 0; i < logoncookies.length; i++) {
                cookies += logoncookies[i].toString() + "; ";
            }
            if (APP_LOG.isDebugEnabled()) {
                APP_LOG.debug("cookies: " + cookies);
            }
        }

        // get top.location variable from login response
        String location = "";
        try {
            int pointer = result.indexOf("top.location = \"");
            if (pointer > -1) {
                int varDeclStart = pointer + 16;
                int varDeclEnd = result.indexOf("\"", varDeclStart);
                location = result.substring(varDeclStart, varDeclEnd);
                if (APP_LOG.isDebugEnabled()) {
                    APP_LOG.debug("CheckCookie [" + location + "]");
                }
            } else {
                APP_LOG.warn("Unable to find CheckCookie GET variable.");
            }
        } catch (IndexOutOfBoundsException iooe) {
            String msg = "IndexOutOfBoundsException: " + iooe.getMessage();
            APP_LOG.warn(msg);
            throw new GMException(msg, iooe); 
        }

        /* create a get request to perform "cookie-handshaking"... */
        GetMethod get = new GetMethod(LINK_LOGIN2 + location);
        get.addRequestHeader("referer", LINK_REFER);
        get.addRequestHeader("Content-Type", "text/html");
        statusCode = -1;
        try {
            // execute the method.
            statusCode = client.executeMethod(get);
        } catch (IOException e) {
            String msg = "Failed to open URL: " + e.getMessage() + ", "
            + e.getCause();
            APP_LOG.warn(msg);
            throw new GMException(msg, e);
        }

        // redirect?
        if ((statusCode == HttpStatus.SC_MOVED_PERMANENTLY)
                    || (statusCode == HttpStatus.SC_MOVED_TEMPORARILY)
                    || (statusCode == HttpStatus.SC_SEE_OTHER)
                    || (statusCode == HttpStatus.SC_TEMPORARY_REDIRECT)) {
            String redirectLocation;
            Header locationHeader = get.getResponseHeader("location");
            if (locationHeader != null) {
                redirectLocation = locationHeader.getValue();
                APP_LOG.warn("Start redirection to: " + redirectLocation);
                get = new GetMethod(redirectLocation);
                try {
                    statusCode = client.executeMethod(get);
                } catch (IOException e) {
                    String msg = "Failed to redirect URL: " + e.getMessage()
                    + ", " + e.getCause();
                    APP_LOG.warn(msg);
                    throw new GMException(msg, e);
                }
            } else {

                // The response is invalid and did not provide the new location for
                // the resource.
                String msg = "Failed to redirect URL. ";
                APP_LOG.warn(msg);
                throw new GMException(msg);
            }
        }

        // 2nd redirect?
        if ((statusCode == HttpStatus.SC_MOVED_PERMANENTLY)
                    || (statusCode == HttpStatus.SC_MOVED_TEMPORARILY)
                    || (statusCode == HttpStatus.SC_SEE_OTHER)
                    || (statusCode == HttpStatus.SC_TEMPORARY_REDIRECT)) {
            String redirectLocation;
            Header locationHeader = get.getResponseHeader("location");
            if (locationHeader != null) {
                redirectLocation = locationHeader.getValue();
                APP_LOG.warn("Start redirection to: " + redirectLocation);
                get = new GetMethod(redirectLocation);
                try {
                    statusCode = client.executeMethod(get);
                } catch (IOException e) {
                    String msg = "Failed to redirect URL: " + e.getMessage()
                    + ", " + e.getCause();
                    APP_LOG.warn(msg);
                    throw new GMException(msg, e);
                }
            } else {
                // The response is invalid and did not provide the new location for
                // the resource.
                String msg = "Failed to redirect URL. ";
                APP_LOG.warn(msg);
                throw new GMException(msg);
            }
        }
        String resp = null;
        try {
            if (statusCode == HttpStatus.SC_OK) {
                resp = get.getResponseBodyAsString();
                if (APP_LOG.isDebugEnabled()) {
                    APP_LOG.debug("cookie-handshaking response: " + resp);
                }
            }
            get.releaseConnection();
        } catch (IOException ioe) {             
            APP_LOG.warn("IOException reading HTTP: " + ioe.getMessage());
        }
        if (resp == null) {
            APP_LOG.warn("Loggin failure");
            throw new GMException("Login failure.");
        }

        // get cookies from gmail
        Cookie[] handshakecookies = client.getState().getCookies();
        if (handshakecookies.length == 0) {
            if (APP_LOG.isDebugEnabled()) {
                APP_LOG.debug("No 2nd phase cookies");
            }
        } else {
            String cookies = "";
            for (int i = 0; i < handshakecookies.length; i++) {
                cookies += handshakecookies[i].toString() + "; ";
            }
            if (APP_LOG.isDebugEnabled()) {
                APP_LOG.debug("2nd phase cookie obtained: " + cookies);
            }
        }
        
        get = new GetMethod(LINK_GMAIL);
        get.setFollowRedirects(true);
        
        try {
            statusCode = client.executeMethod(get);
            result = get.getResponseBodyAsString();
            debug(get);
        } catch (Exception e) {
            throw new GMException("Failed...", e);
        } finally {
            get.releaseConnection();
            get = null;
        }
        
        
        get = new GetMethod(LINK_GMAIL + "?search=inbox&view=tl&start=0&init=1&zx=");
        get.setFollowRedirects(true);
        
        try {
            statusCode = client.executeMethod(get);
            result = get.getResponseBodyAsString();
            debug(get);
        } catch (Exception e) {
            throw new GMException("Failed<2>...", e);
        } finally {
            get.releaseConnection();
            get = null;
        }
        
        
        String[] udStrings = JSONUtil.getJSONString("ud", result);
        if (udStrings.length == 0) {
            throw new GMException("Could not find ik string");
        }
        
        try {
            JSONArray jsonArray = new JSONArray(udStrings[0]);
            ik = jsonArray.getString(3);
            if (APP_LOG.isDebugEnabled()) {
                APP_LOG.debug("\n\n******\n\nFound ik: " + ik + "\n\n******\n\n");
                StringBuffer strbuf = new StringBuffer("JSON array:\n");
                for (int i = 0; i < jsonArray.length(); i++) {
                    strbuf.append("  " + jsonArray.get(i) + "\n");
                }
                APP_LOG.debug(strbuf);
                strbuf = null;
            }
        } catch (JSONException e) {
            throw new GMException("Failed parse json expresssion.", e);
        }
        
        Cookie[] cookies = client.getState().getCookies();
        cookieMap = new HashMap<String, Cookie>(Math.max(10, cookies.length * 2));
        for (int i = 0; i < cookies.length; i++) {
        	cookieMap.put(cookies[i].getName(), cookies[i]);
        	if (APP_LOG.isDebugEnabled()) {
        		APP_LOG.debug(cookies[i]);
        	}        	
        }
    }
    

    
    
 
    /**
     * Performs a logout.
     * @throws GMException Throws exception if logout fails for some reason.
     */
    public void logout() {
        if (client != null) {
            if (APP_LOG.isDebugEnabled()) {
                APP_LOG.debug("Terminates session for user '" + username + "' using URL: " + LINK_LOGOUT);
            }            

            GetMethod get = new GetMethod(LINK_LOGOUT);
            get.addRequestHeader("referer", LINK_REFER);
            get.addRequestHeader("Content-Type", "text/html");
            client.getState().addCookie(
                    new Cookie(
                            LOGON_SITE, "GMAIL_LOGIN",
                            "T" + Calendar.getInstance().getTimeInMillis() + "/"
                    + Calendar.getInstance().getTimeInMillis() + "/"
                    + Calendar.getInstance().getTimeInMillis(), "/", 999,
                            true));
            try {
                // execute the method.
                client.executeMethod(get);
                get.releaseConnection();
            } catch (IOException e) {
                throw new GMException("Failed to open url.", e);
            } finally {
                client = null;
                ik = null;
                cookieMap.clear();
                cookieMap = null;
            }
        }
    }
    
    
    
    
    
    //
    // Accessors
    //
    
    /**
     * Returns client. If not authenticated a login is performed.
     * @return Returns client.
     */
    HttpClient getClient() {
        if (client == null) {
            login();
        }
        return client;
    }
    
    /**
     * Returns ik or null.
     * @return Returns ik or null.
     */
    String getIk() {
        return ik;
    }

    /**
     * Returns http client cookie map.
     * @return Returns http client cookie map.
     */
    Map<String, Cookie> getCookieMap() {
    	return cookieMap;
    }
    
    
    /**
     * Genereates a random value to add to url to make them unique.
     * @return Returns generated value.
     */
    static String makeUniqueUrl() {
        // The significance of 2147483648 is that it's equal to 2^32, or 2GB.
        int rndVal = Math.max(1, random.nextInt(999));
        return String.valueOf((Math.round(rndVal * 2147483.648)));
    }
    
    //
    // Helpers
    //
    
    private void debug(HttpMethodBase method) throws IOException {
        if (APP_LOG.isDebugEnabled()) {
            APP_LOG.debug("Retrieveing: " + method);
            Header[] header = method.getResponseFooters();
            StringBuffer strbuf = new StringBuffer("Recieved headers:\n");
            for (int i = 0; i < header.length; i++) {
                strbuf.append(header[i].getName() + "=" + header[i].getValue() + "\n");
            }
            APP_LOG.debug(strbuf.toString());
            strbuf = null;
            APP_LOG.debug("Recieved:\n" + method.getResponseBodyAsString());
        }        
    }
    
}
