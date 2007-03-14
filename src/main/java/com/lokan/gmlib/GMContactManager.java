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
 */
package com.lokan.gmlib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;


public class GMContactManager {

    
    //
    // Logger
    //
    
    /** Application logger. */
    private static final Logger  APP_LOG =  LogManager.getLogger(GMContactManager.class);
    
    
    //
    // Constants
    //
    
    /** Contact URL. */
    private static final String GMAIL_CONTACT_URL = "https://mail.google.com/mail/?view=up";
    
    /** Referer. */
    private static final String GMAIL_CONTACT_ADDEDIT_REFERER_URL = "https://mail.google.com/mail/?&search=contacts&ct_id=1&cvm=2&view=ct";
    private static final String GMAIL_CONTACT_REMOVE_REFERER_URL = "https://mail.google.com/mail/?view=cl&search=contacts&pnl=a";
    //
    // Member variables
    //
    
    /** GMSession. */
    private GMSession session;
    
    /** Randomizer. */
    private static Random rdm = new Random();
    
    
    //
    // Constructors
    //
    
    
    /**
     * Creates new instance.
     * @param session GMSession to use.
     */
    public GMContactManager(GMSession session) {
        this.session = session;
    }
    
    
    //
    // Methods
    //
    

    
    /**
     * Returns all contacts.
     * @return Returns all contacts.
     * @throws GMException Throws a exception if retrieve fails for some reason.
     */
    public GMContact[] getAllGMContacts() {
        
        try {
            String query = "ik=" + session.getIk()  + "&view=cl&search=contacts&pnl=a&q=";
            query += "&zv=" + rdm.nextInt();
            query = GMSession.LINK_GMAIL + "?" + query;
            if (APP_LOG.isDebugEnabled()) {
                APP_LOG.debug("Retrieving contacts from URL: " + query);
            }            
            GetMethod get = new GetMethod(query);
            if (APP_LOG.isDebugEnabled()) {
                APP_LOG.debug("Retreving: " + query);
            }
            
            get.addRequestHeader("referer", GMSession.LINK_REFER);
            get.addRequestHeader("Content-Type", "text/html");            
            
            int statusCode = session.getClient().executeMethod(get);
            if (statusCode != HttpStatus.SC_OK) {
                APP_LOG.warn("Non normal response code (" + statusCode + "), this might crach...");
            }

            String getResult = get.getResponseBodyAsString();
            if (APP_LOG.isDebugEnabled()) {               
                APP_LOG.debug("Recived contacts:\n" + getResult);
            }            
            
            get.releaseConnection();
            
            return parseContactsFromJSONHtml(getResult);
            
        } catch (GMException e) {
            throw e;
        } catch (Exception e) {
            throw new GMException("Failed retrieve contact list.", e);
        }
    }
    
    /**
     * Adds an contact.
     * @param contact Contact to add.
     * @return Returns new contact with id set
     * @throws IllegalArgumentException is thrown when contact is already added (equals to id is not null).
     * @throws GMException is thrown when add fails.
     */
    public GMContact addGMContact(GMContact contact) {
    	if (contact.getId() != null) {
    		throw new IllegalArgumentException("Contact is already added.");
    	}
    	
        return addEditGMContact(contact);
    }
        
    /**
     * Updates an contact.
     * @param contact Contact to update.
     * @throws IllegalArgumentException is thrown when contact is not added (equals to id is null).
     * @throws GMException is thrown when update fails.
     */
    public void updateGMContact(GMContact contact) {
        if (contact.getId() == null) {
            throw new IllegalArgumentException("Contact not added.");
        }
        
        addEditGMContact(contact);
    }

    
    /**
     * Removes an contacts.
     * @param contacts Contacts to remove.
     * @throws IllegalArgumentException is thrown when contacts is not added (equals to id is null).
     * @throws GMException is thrown when removal fails.
     */
    public void removeGMContacts(GMContact[] contacts) {
        if (contacts.length > 0) {
            StringBuffer contactGetData = new StringBuffer("&act=dc&cl_nw=&cl_id=&cl_nm=");            
            for (int i = 0; i < contacts.length; i++) {
                if (contacts[i].getId() == null) {
                    throw new IllegalArgumentException("Contact not added: " + contacts[i]);
                }
                contactGetData.append("&c=" + contacts[i].getId());
            }
            contactGetData.append("&at=" + session.getCookieMap().get("GMAIL_AT").getValue());
            contactGetData.append("&zx=" + GMSession.makeUniqueUrl());
            contactGetData.append("&view=up");
            GetMethod get = new GetMethod(GMAIL_CONTACT_URL + contactGetData);
            get.addRequestHeader("referer", GMAIL_CONTACT_REMOVE_REFERER_URL);
            if (APP_LOG.isDebugEnabled()) {
                APP_LOG.debug("Is about to remove multiple contacts.");
                APP_LOG.debug("Request: " + get);
            }
            
            try {
                int statusCode = session.getClient().executeMethod(get);
                if (statusCode != HttpStatus.SC_OK) {
                    APP_LOG.warn("Non normal response code (" + statusCode + "), this might crach...");
                }
        
                String postResult = get.getResponseBodyAsString();
                if (APP_LOG.isDebugEnabled()) {               
                    APP_LOG.debug("Recived response:\n" + postResult);
                }
                
                get.releaseConnection();
                
                if (!parseResponseForSuccess(postResult)) {
                    throw new GMException("Failed remove contacts.");
                }
            } catch (IOException e) {
                throw new GMException("Failed remove contacts.", e);
            }
                        
        }
    }

    
    /**
     * Removes an contact.
     * @param contact Contact to remove.
     * @throws IllegalArgumentException is thrown when contact is not added (equals to id is null).
     * @throws GMException is thrown when removal fails.
     */
    public void removeGMContact(GMContact contact) {
        String contactGetData = "&ik=" + session.getIk() 
        + "&search=contacts"
        + "&ct_id=" + contact.getId()
        + "&cvm=2"
        + "&act=dc"
        + "&at=" + session.getCookieMap().get("GMAIL_AT").getValue()
        + "&c=" + contact.getId()
        + "&zx=" + GMSession.makeUniqueUrl();
        
        GetMethod get = new GetMethod(GMAIL_CONTACT_URL + contactGetData);
        get.addRequestHeader("referer", GMAIL_CONTACT_REMOVE_REFERER_URL);
        if (APP_LOG.isDebugEnabled()) {
            APP_LOG.debug("Is about to remove contact: " + contact);
            APP_LOG.debug("Request: " + get);
        }
        
        try {
            int statusCode = session.getClient().executeMethod(get);
            if (statusCode != HttpStatus.SC_OK) {
                APP_LOG.warn("Non normal response code (" + statusCode + "), this might crach...");
            }
    
            String postResult = get.getResponseBodyAsString();
            if (APP_LOG.isDebugEnabled()) {               
                APP_LOG.debug("Recived response:\n" + postResult);
            }
            
            get.releaseConnection();
            
            if (!parseResponseForSuccess(postResult)) {
                throw new GMException("Failed remove contact: " + contact);
            }
        } catch (IOException e) {
            throw new GMException("Failed remove contact " + contact, e);
        }
        
    }
    
    //
    // Helpers
    //
    
    
    
    /**
     * Adds/Edits an contact.
     * @param contact Contact to add/edit.
     * @return Returns new contact (with id set).
     * @throws GMException is thrown when add/edit fails.
     */
    private GMContact addEditGMContact(GMContact contact) {
        PostMethod post = new PostMethod(GMAIL_CONTACT_URL);
        NameValuePair[] data = new NameValuePair[] {
            new NameValuePair("act", "ec"),
            new NameValuePair("ct_id", ((contact.getId() != null) ? contact.getId() : "-1")),
            new NameValuePair("ct_nm", contact.getName()),
            new NameValuePair("ct_em", contact.getEMail()),
            new NameValuePair("ctf_n", contact.getNote()),
            new NameValuePair("at", session.getCookieMap().get("GMAIL_AT").getValue()),
        };
        post.addRequestHeader("referer", GMAIL_CONTACT_ADDEDIT_REFERER_URL);
        post.addRequestHeader("Content-Type",
        "application/x-www-form-urlencoded");
        post.setRequestBody(data);
        if (APP_LOG.isDebugEnabled()) {
            APP_LOG.debug("Is about to add/edit contact: " + contact);
            APP_LOG.debug("Request: " + post);
        }
        
        try {
            int statusCode = session.getClient().executeMethod(post);
            if (statusCode != HttpStatus.SC_OK) {
                APP_LOG.warn("Non normal response code (" + statusCode + "), this might crach...");
            }
    
            String postResult = post.getResponseBodyAsString();
            if (APP_LOG.isDebugEnabled()) {               
                APP_LOG.debug("Recived response:\n" + postResult);
            }
            
            post.releaseConnection();
            
            if (!parseResponseForSuccess(postResult)) {
                throw new GMException("Failed add contact: " + contact);
            }
            
            if (contact.getId() == null) {
                String recievedContactId = parseResponseForId(postResult);
                if (APP_LOG.isDebugEnabled()) {
                    APP_LOG.debug("Recieved contact id: " + recievedContactId);
                }
                return new GMContact(recievedContactId, contact.getName(), contact.getEMail(), contact.getNote());
            } else {
                return contact;
            }
        } catch (IOException e) {
            throw new GMException("Failed add/edit contact " + contact, e);
        }
    }    
    
    /**
     * Parses response for id of added contact. Returns id or null if no id is found.
     * @param html HTML to parse.
     * @return Returns id or null of no id is found.
     * @throws GMException is thrown if parse fails.
     */
    private String parseResponseForId(String html) {
        String result = null;
        
        JSONArray[] jsonArrays = JSONUtil.getJSONArray("cov", html);
        if (jsonArrays.length == 1) {
            try {
                String tmpStr = jsonArrays[0].getString(1);
                if (APP_LOG.isDebugEnabled()) {
                    APP_LOG.debug("Found JSON expression: " + tmpStr);                    
                }
                JSONArray ja = new JSONArray(tmpStr);
                result = ja.getString(1);
            } catch (JSONException e) {
                throw new GMException("Faild parse JSON expression.", e);
            }
        }
        
        return result;
    }
    
    /**
     * Parses response for success flag "ar". Returns true of "ar" flag equals to "1" else false.
     * @param html HTML to parse.
     * @return Returns true if command was successfull else false.
     * @throws GMException is thrown if parse fails.
     */
    private boolean parseResponseForSuccess(String html) {
    	boolean result = false;
    	JSONArray[] jsonArrays = JSONUtil.getJSONArray("ar", html);
    	if (jsonArrays.length == 1) {
    		try {
    			result = jsonArrays[0].getString(1).equals("1");
    		} catch (JSONException e) {
    			throw new GMException("Faild parse JSON expression.", e);
    		}
    	}

    	return result;
    }
    
    /**
     * Parses and constructs <code>GMContact</code> instances from relevent JSON parts of the provied html string.
     * @param html HTML string to parse.
     * @return Returns a GMContact array of all found JSON contacts. If no contacts are found an empty array is returned.
     * @throws GMException Throws Exception of parsing fails for some reason.
     */
    private GMContact[] parseContactsFromJSONHtml(String html)  {
        try {
            List<GMContact> result = new ArrayList<GMContact>(100);;
            JSONArray[] jsonArrays = JSONUtil.getJSONArray("cl", html);
            JSONArray tmpJsonArray;
            if (APP_LOG.isDebugEnabled()) {
                StringBuffer strbuf = new StringBuffer("Contacts JSON arrays:\n");
                for (int i = 0; i <jsonArrays.length; i++) {
                    strbuf.append("\tJSONArray " + i + "\n");
                    for (int j = 1; j < jsonArrays[i].length(); j++) {
                        strbuf.append("\t\t" + jsonArrays[i].get(j) + "\n");
                        tmpJsonArray = new JSONArray(jsonArrays[i].getString(j));
                        result.add(new GMContact(tmpJsonArray));
                        
                    }
                }
                APP_LOG.debug(strbuf);
                strbuf = null;
            } else {
                for (int i = 0; i <jsonArrays.length; i++) {                    
                    for (int j = 1; j < jsonArrays[i].length(); j++) {
                        tmpJsonArray = new JSONArray(jsonArrays[i].getString(j));
                        result.add(new GMContact(tmpJsonArray));
                    }
                }
                
            }
            return result.toArray(new GMContact[result.size()]);
        } catch (GMException e) {
            throw e;
        } catch (Exception e) {
            throw new GMException("Failed parse JSON contacts.", e);
        }
        
    }
}
