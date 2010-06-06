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
package com.octane.util;

/**
 */
public final class CommandState {
    
    public static final String EMPTY = "";
    
    private String fileName = EMPTY;
    private String projectHome = EMPTY;
    private String filePath = EMPTY;
    private String compileCommand = EMPTY;
    private String runCommand = EMPTY;
    private String singleCommand = EMPTY;
    private String singleClass = EMPTY;
    
    private boolean active = false;
    
    /**
     * Constructor for CommandState.
     */
    private CommandState() {
        // Disable the constructor
    }
    
    /////////////////////////////////////////////////////////////////
    
    /**
     * Implementation Routine createEmptyState.
     * @return CommandState
     */
    public static final CommandState createEmptyState() {
        final CommandState state = new CommandState(EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY);
        return state;
    }
    
    /**
     * Constructor for CommandState.
     * @param fName String
     * @param pHome String
     * @param path String
     * @param compile String
     * @param run String
     * @param singleCmd String
     * @param singleClass String
     */
    public CommandState(final String fName, final String pHome, final String path, final String compile, 
            final String run, final String singleCmd, final String singleClass) {
        
        this.fileName = fName;
        this.projectHome = pHome;
        this.filePath = path;
        this.compileCommand = compile;
        this.runCommand = run;
        this.singleCommand = singleCmd;
        this.singleClass = singleClass;        
    }
    
    /**
     * Implementation Routine toString.
     * @return String
     */
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("[Project Name]: ");
        buf.append(this.fileName);
        buf.append(" path=>");
        buf.append(this.filePath);
        return buf.toString();
    }

    /**
     * @return the compileCommand
     */
    public String getCompileCommand() {
        return compileCommand;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @return the filePath
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * @return the projectHome
     */
    public String getProjectHome() {
        return projectHome;
    }

    /**
     * @return the runCommand
     */
    public String getRunCommand() {
        return runCommand;
    }

    /**
     * @return the singleClass
     */
    public String getSingleClass() {
        return singleClass;
    }

    /**
     * @return the singleCommand
     */
    public String getSingleCommand() {
        return singleCommand;
    }

    /**
     * @param compileCommand the compileCommand to set
     */
    public void setCompileCommand(String compileCommand) {
        this.compileCommand = compileCommand;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @param filePath the filePath to set
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * @param projectHome the projectHome to set
     */
    public void setProjectHome(String projectHome) {
        this.projectHome = projectHome;
    }

    /**
     * @param runCommand the runCommand to set
     */
    public void setRunCommand(String runCommand) {
        this.runCommand = runCommand;
    }

    /**
     * @param singleClass the singleClass to set
     */
    public void setSingleClass(String singleClass) {
        this.singleClass = singleClass;
    }

    /**
     * @param singleCommand the singleCommand to set
     */
    public void setSingleCommand(String singleCommand) {
        this.singleCommand = singleCommand;
    }

    /**
     * @return the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active the active to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }
}
