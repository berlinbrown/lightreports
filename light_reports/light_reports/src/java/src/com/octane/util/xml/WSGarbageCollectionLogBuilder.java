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
package com.octane.util.xml;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.octane.util.StringUtils;

/**
 * Utility to repair the garbage collection logs.
 * Garbage collection statistics are written to
 * 'native_stderr.log'.
 * 
 * The input document can include multiple xml header tags
 * No end tag and invalid data between tags.
 *  
 * @author Berlin
 *
 */
public final class WSGarbageCollectionLogBuilder {
    
    public static final String NL = "\n";
    
    public static final String EMPTY = "";
    
    public static final String XML_HEADER = "<\\?xml version(.*?)\\?>";
    
    public static final String XML_LINE = "^<(.*?)>$";
    
    public final Pattern HEADER_PATTERN = Pattern.compile(XML_HEADER, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
    
    public final Pattern XML_PATTERN = Pattern.compile(XML_LINE, Pattern.CASE_INSENSITIVE);
    
    //////////////////////////////////////////////////////////////////
    
    private final String inputDocument;
    
    private String repairDocument = EMPTY;
    
    private final WSGarbageCollectionLogReport report = new WSGarbageCollectionLogReport();
    
    public WSGarbageCollectionLogBuilder(final String indoc) {
        // If the document is null, convert to an empty document.
        this.inputDocument = indoc == null ? EMPTY : indoc;        
        this.report.setDocumentSize(this.inputDocument.length());
        this.repairDocument = this.inputDocument;
    }
    
    public final void repairInit() {
        this.repairDocument = this.repairDocument.trim();
    }
    
    public final Matcher matchHeader() {
        final Matcher match = HEADER_PATTERN.matcher(this.repairDocument);
        return match;
    }
    
    public final Matcher matchXMLTag() {
        final Matcher match = HEADER_PATTERN.matcher(this.repairDocument);
        return match;
    }
    
    public final void repairRemoveHeader() {         
        final Matcher match = this.matchHeader();
        if (match != null) {
            // Remove the header
            this.repairDocument = match.replaceAll(EMPTY);
        } // End of the if //        
    }
    
    public final void repairOnlyXMLTags() {
        final StringBuffer repairBuffer = new StringBuffer(128);
        try {
            final ByteArrayInputStream stream = new ByteArrayInputStream(this.repairDocument.getBytes());
            final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String data = "";
            do {
                data = reader.readLine();
                if (StringUtils.isNotEmpty(data)) {
                    final String trim = data.trim();
                    final Matcher match = XML_PATTERN.matcher(trim);
                    while(match.find()) {                        
                        // Add this line
                        repairBuffer.append(match.group()).append(NL);
                    } // End of the While //
                }
            } while(data != null);
        } catch(Exception e) {
            e.printStackTrace();
        } // End of the try catch //
        this.repairDocument = repairBuffer.toString();
    }
        
   
    public final void repairAddOuterTags() {
        // also remove the end tags
        this.repairDocument = this.repairDocument.replaceAll("<verbosegc(.*)>", EMPTY);
        this.repairDocument = this.repairDocument.replaceAll("</verbosegc>", EMPTY);
        
        final StringBuffer addBuf = new StringBuffer(this.repairDocument.length()+128);
        
        addBuf.append("<?xml version=\"1.0\" ?>").append(NL);        
        addBuf.append("<verbosegc version=\"200803_08\">").append(NL);
        addBuf.append(this.repairDocument).append(NL);
        addBuf.append("</verbosegc>").append(NL);
        this.repairDocument = addBuf.toString();
    }
    
    public final void repair() {
        this.repairInit();
        this.repairRemoveHeader();
        this.repairOnlyXMLTags();
        this.repairAddOuterTags();
    }                               
    
    public final String getRepairDocument() {        
        return this.repairDocument;        
    }
        
} // End of the class //
