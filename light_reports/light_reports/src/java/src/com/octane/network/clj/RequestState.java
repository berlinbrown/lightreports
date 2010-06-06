/********************************************************************
 *
 * Copyright (c) 2006-2007 Berlin Brown and botnode.com  All Rights Reserved
 *
 * http://www.opensource.org/licenses/bsd-license.php

 * All rights reserved.

 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:

 * * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * * Neither the name of the Botnode.com (Berlin Brown) nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written permission.

 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Date: 1/5/2009
 *       7/15/2009 - Added Clojure 1.0, other performance fixes and cleanups.
 *       
 * Main Description: Light Log Viewer is a tool for making it easier to search log files.  
 * Light Log Viewer adds some text highlighting, quick key navigation to text files, simple graphs 
 * and charts for monitoring logs, file database to quickly navigate to files of interest, 
 * and HTML to PDF convert tool.  
 * Light Log was developed with a combination of Clojure 1.0, Java and Scala with use of libs, SWT 3.4, JFreeChart, iText. 
 * 
 * Quickstart : the best way to run the Light Log viewer is to click on the win32 batch script light_logs.bat
 * (you may need to edit the Linux script for Unix/Linux environments).
 * Edit the win32 script to add more heap memory or other parameters.
 * 
 * The clojure source is contained in : HOME/src/octane
 * The java source is contained in :  HOME/src/java/src
 * 
 * To build the java source, see : HOME/src/java/build.xml and build_pdf_gui.xml
 * 
 * Metrics: (as of 7/15/2009) Light Log Viewer consists of 6500 lines of Clojure code, and contains wrapper code
 *  around the Java source.  There are 2000+ lines of Java code in the Java library for Light Log Viewer.
 *  
 * Additional Development Notes: The SWT gui and other libraries are launched from a dynamic classloader.  Clojure is also
 *   started from the same code, and reflection is used to dynamically initiate Clojure. See the 'start' package.  The binary
 *   code is contained in the octane_start.jar library.
 *   
 * Home Page: http://code.google.com/p/lighttexteditor/
 * 
 * Contact: Berlin Brown <berlin dot brown at gmail.com>
 *********************************************************************/
package com.octane.network.clj;

import com.octane.util.StringUtils;

/**
 * @author Berlin
 * @version $Revision: 1.0 $
 */
public final class RequestState {
    
    private String requestType = "";
    private String requestLocation = "/";
    
    private String urlPrefix = "";

    private String urlAction = "";
    private String urlActionArgs = "";
    private String urlActionArgsDecoded = "";
    private int urlActionCode = -1;
   
    private boolean loaded = false;
    
    /////////////////////////////////////////////////////////////////

    /**
     * @return the urlActionArgsDecoded
     */
    public final String getUrlActionArgsDecoded() {
        return urlActionArgsDecoded;
    }

    /**
     * @param urlActionArgsDecoded the urlActionArgsDecoded to set
     */
    public final void setUrlActionArgsDecoded(String urlActionArgsDecoded) {
        this.urlActionArgsDecoded = urlActionArgsDecoded;
    }
   
    /**
     * @return the urlActionCode
     */
    public final int getUrlActionCode() {
        return urlActionCode;
    }

    /**
     * @param urlActionCode the urlActionCode to set
     */
    public final void setUrlActionCode(int urlActionCode) {
        this.urlActionCode = urlActionCode;
    }

    
    /**
     * @return the urlPrefix
     */
    public final String getUrlPrefix() {
        return urlPrefix;
    }

    /**
     * @param urlPrefix the urlPrefix to set
     */
    public final void setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }

    
    /**
     * @return the urlAction
     */
    public final String getUrlAction() {
        return urlAction;
    }

    /**
     * @param urlAction the urlAction to set
     */
    public final void setUrlAction(String urlAction) {
        this.urlAction = urlAction;
    }

    /**
     * @return the urlActionArgs
     */
    public final String getUrlActionArgs() {
        return urlActionArgs;
    }

    /**
     * @param urlActionArgs the urlActionArgs to set
     */
    public final void setUrlActionArgs(String urlActionArgs) {
        this.urlActionArgs = urlActionArgs;
    }
  
    
    /**
     * Implementation Routine toString.
     * @return String
     */
    public String toString() {
        final String additionalParams = StringUtils.concat("(Params: action=", this.urlAction, " args=", this.urlActionArgs + ")");
        return StringUtils.concat(super.toString(), "@RequestState: location=", this.requestLocation, additionalParams);
    }
    
    /**
     * @return the requestLocation
     */
    public final String getRequestLocation() {
        return requestLocation;
    }
    /**
     * @param requestLocation the requestLocation to set
     */
    public final void setRequestLocation(String requestLocation) {
        this.requestLocation = requestLocation;
    }
    /**
     * @return the requestType
     */
    public final String getRequestType() {
        return requestType;
    }
    /**
     * @param requestType the requestType to set
     */
    public final void setRequestType(String requestType) {
        this.requestType = requestType;
    }      
    
    /**
     * @return the loaded
     */
    public final boolean isLoaded() {
        return loaded;
    }
    /**
     * @param loaded the loaded to set
     */
    public final void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }
            
    //////////////////////////////////////////////////////////////////    
    
    public boolean isActionOpenFile() {
        if (this.getUrlActionCode() == ServerConsts.ENUM_ACTION_CODE_OPEN_FILE) {
            return true;
        }
        return false;
    }
        
    public boolean isActionBrowseDir() {
        if (this.getUrlActionCode() == ServerConsts.ENUM_ACTION_CODE_BROWSE_DIR) {
            return true;
        }
        return false;
    }
    
    /**
     * Implementation Routine emptyState.
     * @return RequestState
     */
    public static final RequestState emptyState() {
        final RequestState empty = new RequestState();
        return empty;
    }    
    
} // End of the Class //
