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
package com.octane.global;

import java.io.File;
import java.util.ResourceBundle;

import com.octane.util.DirectoryUtils;

public final class OctaneGlobalState {

    public static final String EMPTY = "";
    
    /////////////////////////////////////////////////////////////////
    
    public static final String DEFAULT_PARENT_DIR = "\\tmp";
    public static final String DEFAULT_BASE_DIR_REGEX = "serv";
    public static final String DEFAULT_FILENAME_FILTER = "SystemOut";
    
    /////////////////////////////////////////////////////////////////
    
    /**
     * For the merge collection, the primary parent directory might be
     * set to:
     * '/machine/logs'
     * The parent directory contains the sub-directories we are interested in.     
     */
    private String mergeSetPrimaryParentDir = DEFAULT_PARENT_DIR;
    
    /**
     * Expression to match on for sub-directories.
     */
    private String mergeSetPrimaryBaseDirRegex = DEFAULT_BASE_DIR_REGEX;
    
    /**
     * Expression to match the file on.
     */
    private String mergeSetPrimaryFilenameFilterRegex = DEFAULT_FILENAME_FILTER;
    
    /////////////////////////////////////////////////////////////////
    
    /**
     * For the merge collection, the primary parent directory might be
     * set to:
     * '/machine/logs'
     * The parent directory contains the sub-directories we are interested in.     
     */
    private String mergeSetSecondaryParentDir = "\\\\tmp\\logs";
    
    /**
     * Expression to match on for sub-directories.
     */
    private String mergeSetSecondaryBaseDirRegex = "serv";
    
    /**
     * Expression to match the file on.
     */
    private String mergeSetSecondaryFilenameFilterRegex = "SystemOut";

    /////////////////////////////////////////////////////////////////
    
    public synchronized final void setMergeSetPrimary(final String parentDir, final String dirRegex, final String filterName) {
        this.setMergeSetPrimaryParentDir(parentDir);
        this.setMergeSetPrimaryBaseDirRegex(dirRegex);
        this.setMergeSetPrimaryFilenameFilterRegex(filterName);
    }
    
    public synchronized final void setMergeSetSecondary(final String parentDir, final String dirRegex, final String filterName) {
        this.setMergeSetSecondaryParentDir(parentDir);
        this.setMergeSetSecondaryBaseDirRegex(dirRegex);
        this.setMergeSetSecondaryFilenameFilterRegex(filterName);
    }
    
    /////////////////////////////////////////////////////////////////
    
    private String safeGet(final ResourceBundle conf, final String key) {
        if (conf != null) {
            final String res = conf.getString(key) == null ? EMPTY : conf.getString(key);
            return res;
        }
        return EMPTY;
    }
    
    public synchronized final void setMergeSetPrimary(final ResourceBundle conf) {
        if (conf != null) {                                            
            this.setMergeSetPrimaryParentDir(safeGet(conf, "Merge_Set_Primary_ParentDir"));
            this.setMergeSetPrimaryBaseDirRegex(safeGet(conf, "Merge_Set_Primary_BaseDir_Regex"));
            this.setMergeSetPrimaryFilenameFilterRegex(safeGet(conf, "Merge_Set_Primary_Filename_Regex"));
        } // End of the if //
    }
    
    public synchronized final void setMergeSetSecondary(final ResourceBundle conf) {
        if (conf != null) {
            this.setMergeSetSecondaryParentDir(safeGet(conf, "Merge_Set_Secondary_ParentDir"));
            this.setMergeSetSecondaryBaseDirRegex(safeGet(conf, "Merge_Set_Secondary_BaseDir_Regex"));
            this.setMergeSetSecondaryFilenameFilterRegex(safeGet(conf, "Merge_Set_Secondary_Filename_Regex"));
        } // End of the if //
    }
    
    public final File [] getMergeFilesPrimary() {
        return DirectoryUtils.getMergeFilesPrimary(this);
    }
    
    public final File [] getMergeFilesSecondary() {
        return DirectoryUtils.getMergeFilesSecondary(this);
    }
                    
    /**
     * @return the mergeSetPrimaryBaseDirRegex
     */
    public synchronized final String getMergeSetPrimaryBaseDirRegex() {
        return mergeSetPrimaryBaseDirRegex;
    }

    /**
     * @param mergeSetPrimaryBaseDirRegex the mergeSetPrimaryBaseDirRegex to set
     */
    public synchronized final void setMergeSetPrimaryBaseDirRegex(String mergeSetPrimaryBaseDirRegex) {
        this.mergeSetPrimaryBaseDirRegex = mergeSetPrimaryBaseDirRegex;
    }

    /**
     * @return the mergeSetPrimaryFilenameFilterRegex
     */
    public synchronized final String getMergeSetPrimaryFilenameFilterRegex() {
        return mergeSetPrimaryFilenameFilterRegex;
    }

    /**
     * @param mergeSetPrimaryFilenameFilterRegex the mergeSetPrimaryFilenameFilterRegex to set
     */
    public synchronized final void setMergeSetPrimaryFilenameFilterRegex(String mergeSetPrimaryFilenameFilterRegex) {
        this.mergeSetPrimaryFilenameFilterRegex = mergeSetPrimaryFilenameFilterRegex;
    }

    /**
     * @return the mergeSetPrimaryParentDir
     */
    public synchronized final String getMergeSetPrimaryParentDir() {
        return mergeSetPrimaryParentDir;
    }

    /**
     * @param mergeSetPrimaryParentDir the mergeSetPrimaryParentDir to set
     */
    public synchronized final void setMergeSetPrimaryParentDir(String mergeSetPrimaryParentDir) {
        this.mergeSetPrimaryParentDir = mergeSetPrimaryParentDir;
    }

    /**
     * @return the mergeSetSecondaryBaseDirRegex
     */
    public synchronized final String getMergeSetSecondaryBaseDirRegex() {
        return mergeSetSecondaryBaseDirRegex;
    }

    /**
     * @param mergeSetSecondaryBaseDirRegex the mergeSetSecondaryBaseDirRegex to set
     */
    public synchronized final void setMergeSetSecondaryBaseDirRegex(String mergeSetSecondaryBaseDirRegex) {
        this.mergeSetSecondaryBaseDirRegex = mergeSetSecondaryBaseDirRegex;
    }

    /**
     * @return the mergeSetSecondaryFilenameFilterRegex
     */
    public synchronized final String getMergeSetSecondaryFilenameFilterRegex() {
        return mergeSetSecondaryFilenameFilterRegex;
    }

    /**
     * @param mergeSetSecondaryFilenameFilterRegex the mergeSetSecondaryFilenameFilterRegex to set
     */
    public synchronized final void setMergeSetSecondaryFilenameFilterRegex(String mergeSetSecondaryFilenameFilterRegex) {
        this.mergeSetSecondaryFilenameFilterRegex = mergeSetSecondaryFilenameFilterRegex;
    }

    /**
     * @return the mergeSetSecondaryParentDir
     */
    public synchronized final String getMergeSetSecondaryParentDir() {
        return mergeSetSecondaryParentDir;
    }

    /**
     * @param mergeSetSecondaryParentDir the mergeSetSecondaryParentDir to set
     */
    public synchronized final void setMergeSetSecondaryParentDir(String mergeSetSecondaryParentDir) {
        this.mergeSetSecondaryParentDir = mergeSetSecondaryParentDir;
    }
    
    /////////////////////////////////////////////////////////////////   
    
} // End of the Class //
