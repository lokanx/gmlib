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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Proxy information holder.
 */
public class Proxy {
    
    //
    // Logger
    //
    
    /** Application logger. */
    private static final Logger  APP_LOG =  LogManager.getLogger(Proxy.class);
    
        
    //
    // Member variables
    //

    /** Proxy name. */
    private String proxy;
    
    /** Proxy port. */
    private int proxyPort;
    
    /** Proxy username. */
    private String proxyUsername;
    
    /** Proxy password. */
    private String proxyPassword;
    
    
    //
    // Constructors
    //

    /**
     * Creates a new instance of <code>Proxy</code>.
     * @param proxy         Proxy.
     * @param proxyPort     Port.
     */
    public Proxy(String proxy, int proxyPort) {
        this(proxy, proxyPort, null, null);
    }
    
    
    /**
     * Creates a new instance of <code>Proxy</code>.
     * @param proxy         Proxy.
     * @param proxyPort     Port.
     * @param proxyUsername Username.
     * @param proxyPassword Password.
     */
    public Proxy(String proxy, int proxyPort, String proxyUsername, String proxyPassword) {
        if (APP_LOG.isDebugEnabled()) {
            APP_LOG.debug("Creating new Proxy(\"" + proxy + "\", \"" + proxyPort + "\", \"" + proxyUsername + "\", \"***********\")");
        }        
        
        this.proxy = proxy;
        this.proxyPort = proxyPort;
        this.proxyUsername = proxyUsername;
        this.proxyPassword = proxyPassword;
        
    }
    
    
    //
    // Accessors
    //
    
    /**
     *  Returns proxy.
     * @return Returns proxy.
     */public String getProxy() {
        return proxy;
    }
    
     /**
      * Returns port.
      * @return Returns port.
      */
    public int getProxyPort() {
        return proxyPort;
    }
    
    /**
     * Returns username.
     * @return Returns username.
     */
    public String getProxyUsername() {
        return proxyUsername;
    }
    
    /**
     * Returns password.
     * @return Returns password.
     */
    public String getProxyPassword() {
        return proxyPassword;
    }
    
    /**
     * Returns true if authentication is needed else false.
     * @return Returns true if authentication is needed else false.
     */
    public boolean isAuthenticationNeeded() {
        return (proxyUsername != null);
    }
    
    /**
     * Returns a string representation of this instance.
     * @return Returns a string representation of this instance.
     */
    @Override
    public String toString() {
        StringBuffer strbuf = new StringBuffer("Proxy [proxy=\"" + proxy + "\", port=\"" + proxyPort + "\", username=\"" + proxyUsername + "\", password=\"" + proxyPassword + "\"]");
        return strbuf.toString();
    }
    
}
