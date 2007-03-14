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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;


/**
 * Class represents a GM contact.
 */
public class GMContact {

    //
    // Logger
    //
    
    /** Application logger. */
    private static final Logger  APP_LOG =  LogManager.getLogger(GMContact.class);
    
    //
    // Constants
    //
    
    /** Maximum length of name and Email fields. */
    private static final int MAXIMUM_FIELD_LENGTH = 100;
    
    
    //
    // Member variables
    //
    
    /** Contacts name. */
    private String name;
    
    /** E-mail address. */
    private String eMail;
    
    /** Contacts note. */
    private String note;
    
    /** Contacts id. */
    private String id;
    
    //
    // Constructors
    //

    /**
     * Creates a new instance of <code>GMContact</code>.
     * @param array  JSON array.
     * @throws GMException Throws exception whith faulty JSON array as argument.
     */
    GMContact(JSONArray array) {
        try {
            this.id = array.getString(1);
            this.name = array.getString(2);
            this.eMail = array.getString(4);
            this.note = array.getString(5);    
            if (APP_LOG.isDebugEnabled()) {
                APP_LOG.debug("Creating new GMContact(\"" + id + "\", \"" + name + "\", \"" + eMail + "\", \"" + note + "\")");
            }
        } catch (JSONException e) {
            throw new GMException("Failed create contact from JSONArray" + array, e);
        }
        
    }
    
    
    /**
     * Creates a new instance of <code>GMContact</code>.
     * @param id    Contact id.
     * @param name  Contact name. 
     * @param eMail Contact e-mail address.
     * @param note Contact note.
     * @throws IllegalArgumentException is thrown when name or eMail parameter is longer than 100 characters.
     */
    GMContact(String id, String name, String eMail, String note) {
        this.id = id;
        this.name = name;
        this.eMail = eMail;
        this.note = note;    
        if (APP_LOG.isDebugEnabled()) {
            APP_LOG.debug("Creating new GMContact(\"" + id + "\", \"" + name + "\", \"" + eMail + "\", \"" + note + "\")");
        }
    }    
    
    /**
     * Creates a new instance of <code>GMContact</code>.
     * @param name  Contact name. 
     * @param eMail Contact e-mail address.
     * @param note Contact note.
     * @throws IllegalArgumentException is thrown when name or eMail parameter is longer than 100 characters.
     */
    public GMContact(String name, String eMail, String note) {
    	if (name != null  &&  name.length() > MAXIMUM_FIELD_LENGTH) {
    		throw new IllegalArgumentException(
    				"Name length must not be longer than " + MAXIMUM_FIELD_LENGTH + " characters.");
    	}
    	
    	if (eMail != null  &&  eMail.length() > MAXIMUM_FIELD_LENGTH) {
    		throw new IllegalArgumentException(
    				"E-mail length must not be longer than " + MAXIMUM_FIELD_LENGTH + " characters.");
    	}
    	
        this.id = null;
        this.name = name;
        this.eMail = eMail;
        this.note = note;    
        if (APP_LOG.isDebugEnabled()) {
            APP_LOG.debug("Creating new GMContact(\"" + id + "\", \"" + name + "\", \"" + eMail + "\", \"" + note + "\")");
        }
    }

    
    //
    // Accessors
    //
    
    
    /**
     * Returns id.
     * @return Returns id.
     */
    public String getId() {
        return id;
    }
    
    
    /**
     * Returns name.
     * @return Returns name.
     */
    public String getName() {
        return name;
    }
    
    /** 
     * Returns e-mail address.
     * @return Returns e-mail address.
     */
    public String getEMail() {
        return eMail;
    }
    
    
    /**
     * Returns note.
     * @return Returns note.
     */
    public String getNote() {
        return note;
    }
    
    
    //
    // Mutators
    //
    
    /**
     * Updates name.
     * @param name New name.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Updates E-mail.
     * @param mail New Email.
     */
    public void setEMail(String mail) {
        eMail = mail;
    }
    
    /**
     * Updates note.
     * @param note New note.
     */
    public void setNote(String note) {
        this.note = note;
    }
    
    
    //
    // Misc methods
    //
    
    /**
     * Returns a string representation of this instance.
     * @return Returns a string representation of this instance.
     */
    @Override
    public String toString() {
        StringBuffer strbuf = new StringBuffer("GMContact [id=\"" + id + "\", name=\"" + name + "\", eMail=\"" + eMail + "\", note=\"" + note + "\"]");
        return strbuf.toString();
    }
}
