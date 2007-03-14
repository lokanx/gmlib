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

/**
 * Main exceptions class for package.
 */
public class GMException extends RuntimeException {

    //
    // SUID
    //
    
    /**
     * Serial Version UID string.
     */
    private static final String SUID_STR = 
      "$RCSfile$" + "$Revision$";

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = ((long) SUID_STR.hashCode());
    
    
    //
    // Constructors
    //
    
    
    /**
     * Creates a new instance of <code>GMException</code>.
     */
    public GMException() {
        super();
    }

    /**
     * Creates a new instance of <code>GMException</code>.
     * @param message   Message.
     */
    public GMException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of <code>GMException</code>.
     * @param cause     Cause.
     */
    public GMException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance of <code>GMException</code>.
     * @param message   Message.
     * @param cause     Cause.
     */
    public GMException(String message, Throwable cause) {
        super(message, cause);
    }

}
