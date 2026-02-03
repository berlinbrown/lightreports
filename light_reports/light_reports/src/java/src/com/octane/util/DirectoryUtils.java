/********************************************************************
 *
 * Copyright (c) 2006-2007 Berlin Brown and botnode.com  All Rights Reserved
 *
 * http://www.opensource.org/licenses/bsd-license.php
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * * Neither the name of the Botnode.com (Berlin Brown) nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written permission.
 *
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

import com.octane.global.OctaneGlobalState;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Berlin
 * @version $Revision: 1.0 $
 */
public class DirectoryUtils {

  /////////////////////////////////////////////////////////////////

  /**
   * @author Berlin
   * @version $Revision: 1.0 $
   */
  private static final class MergeSubDirFilter implements FilenameFilter {

    private final String targetRegexName;

    /**
     * Constructor for MergeSubDirFilter.
     *
     * @param regex String
     */
    public MergeSubDirFilter(final String regex) {
      this.targetRegexName = regex;
    }

    /**
     * Implementation Routine accept.
     *
     * @param dir File
     * @param name String
     * @return boolean
     * @see java.io.FilenameFilter#accept(File, String)
     */
    public boolean accept(File dir, String name) {
      final Pattern targetPattern = Pattern.compile(targetRegexName);
      final Matcher matchTarget = targetPattern.matcher(name);
      return matchTarget.find();
    }
  } // End of the class //

  /**
   * @author Berlin
   * @version $Revision: 1.0 $
   */
  private static final class MergeFilenameFilter implements FilenameFilter {

    private final String targetRegexName;

    /**
     * Constructor for MergeFilenameFilter.
     *
     * @param regex String
     */
    public MergeFilenameFilter(final String regex) {
      this.targetRegexName = regex;
    }

    /**
     * Implementation Routine accept.
     *
     * @param dir File
     * @param name String
     * @return boolean
     * @see java.io.FilenameFilter#accept(File, String)
     */
    public boolean accept(File dir, String name) {
      final Pattern targetPattern = Pattern.compile(targetRegexName);
      final Matcher matchTarget = targetPattern.matcher(name);
      return matchTarget.find();
    }
  } // End of the class //

  /////////////////////////////////////////////////////////////////

  /**
   * Implementation Routine getMergeFilesPrimary.
   *
   * @param state OctaneGlobalState
   * @return File[]
   */
  public static final File[] getMergeFilesPrimary(final OctaneGlobalState state) {
    return getMergeFiles(
        state.getMergeSetPrimaryParentDir(),
        state.getMergeSetPrimaryBaseDirRegex(),
        state.getMergeSetPrimaryFilenameFilterRegex());
  }

  /**
   * Implementation Routine getMergeFilesSecondary.
   *
   * @param state OctaneGlobalState
   * @return File[]
   */
  public static final File[] getMergeFilesSecondary(final OctaneGlobalState state) {
    return getMergeFiles(
        state.getMergeSetSecondaryParentDir(),
        state.getMergeSetSecondaryBaseDirRegex(),
        state.getMergeSetSecondaryFilenameFilterRegex());
  }

  /**
   * Implementation Routine getMergeFiles.
   *
   * @param parentDir String
   * @param dirRegex String
   * @param fileFilter String
   * @return File[]
   */
  public static final File[] getMergeFiles(
      final String parentDir, final String dirRegex, final String fileFilter) {
    // Always attempt to return a zero length array on error.
    if (StringUtils.isEmpty(parentDir)) {
      return new File[0];
    }

    final List listFiles = new ArrayList();
    // Given the parent directory.
    final File parentDirFile = new File(parentDir);
    if (parentDirFile.exists()) {
      final FilenameFilter filter = new MergeSubDirFilter(dirRegex);
      final File[] parentSubDirs = parentDirFile.listFiles(filter);
      for (int i = 0; i < parentSubDirs.length; i++) {
        if (parentSubDirs[i].isDirectory()) {
          // If directory found, then attempt to find the files of interest
          final FilenameFilter filenameFilter = new MergeFilenameFilter(fileFilter);
          final File[] targetFiles = parentSubDirs[i].listFiles(filenameFilter);
          for (int z = 0; z < targetFiles.length; z++) {
            listFiles.add(targetFiles[z]);
          }
        } // End of the if // is directory //
      } // End of the for // - outer  //

      // After if exists check, determine if we return merged files //
      final File[] res = (File[]) listFiles.toArray(new File[listFiles.size()]);
      if (res != null && (res.length > 0)) {
        return res;
      }
    }
    return new File[0];
  }
} // End of the Class //
