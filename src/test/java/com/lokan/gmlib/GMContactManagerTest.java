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

import java.util.Arrays;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.lokan.gmlib.GMContact;
import com.lokan.gmlib.GMContactManager;
import com.lokan.gmlib.GMSession;

import junit.framework.TestCase;

/**
 * Testing class <code>GMContactManager</code>.
 */
public class GMContactManagerTest extends TestCase {

    //
    // Logger
    //
    
    /** Application logger. */
    private static final Logger  APP_LOG =  LogManager.getLogger(GMContactManagerTest.class);

    
    //
    // Statics
    //
    
    /** Session instance. */
    private static final GMSession SESSION = TUtils.createSession(GMContactManagerTest.class, "/Test.properties");
    
    /**
     * Tests <code>getAllGMContacts</code>.
     * @throws Exception Throws exception if anything goes wrong.
     */
    public void testGetAllGMContacts() throws Exception {
        
        GMContactManager manager = new GMContactManager(SESSION); 
        GMContact[] contacts = manager.getAllGMContacts();
        APP_LOG.info("Recieved contacts: " + Arrays.asList(contacts));
        
    }
    
    /**
     * Tests <code>addGMContact</code>.
     * @throws Exception Throws exception if anything goes wrong.
     */
    public void testAddEditDeleteGMCOntact() throws Exception {
    	GMContactManager manager = new GMContactManager(SESSION);
    	GMContact contact = new GMContact(
    			"TEST TESTINGSSON", 
    			"test.testingsson@testnet.com", 
    			"Test comment");
        System.out.println("Is about to add contact: " + contact);
        contact = manager.addGMContact(contact);
        System.out.println("Recived new added contact: " + contact);
        contact = new GMContact(contact.getId(), contact.getName(),contact.getEMail(), "Some new interesting note.");
        System.out.println("Is about to edit contact: " + contact);
        manager.updateGMContact(contact);
        System.out.println("Is about to remove contact: " + contact);
        manager.removeGMContact(contact);
    }
    
    public void testRemoveGMContacts() throws Exception {
        GMContactManager manager = new GMContactManager(SESSION);
        GMContact contact1 = new GMContact(
                "TEST 1", 
                "test1@testnet.com", 
                "Test 1 comment");
        GMContact contact2 = new GMContact(
                "TEST 2", 
                "fest2@testnet.com", 
                "Fest 2 comment");        
        contact1 = manager.addGMContact(contact1);
        contact2 = manager.addGMContact(contact2);
        manager.removeGMContacts(new GMContact[] {contact1, contact2});
    }
}
