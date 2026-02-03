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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Berlin
 * @version $Revision: 1.0 $
 */
public class FileUtils {

  public static final int MAX_LINES_READ_MERGE = 3000;

  /////////////////////////////////////////////////////////////////

  private static final AllLogsFilenameFilter DEFAULT_FILTER = new AllLogsFilenameFilter();

  private static final Pattern ALL_LOG_PATTERN = Pattern.compile("(log|Z)");

  public static final class AllLogsFilenameFilter implements FilenameFilter {

    public boolean accept(final File dir, final String name) {
      // Extract the filename extension
      // And then only look for system files
      final Matcher m = ALL_LOG_PATTERN.matcher(FileUtils.getFileExt(name));
      return m.find();
    }
  } // End of the Class //

  public static final File[] listLogFiles(final File dir) {
    return dir.listFiles(DEFAULT_FILTER);
  }

  /////////////////////////////////////////////////////////////////

  /**
   * @author Berlin
   * @version $Revision: 1.0 $
   */
  private static final class SortableFileByPath implements Comparable {
    private String path;
    private final File file;

    /**
     * Constructor for SortableFileByPath.
     *
     * @param file File
     */
    private SortableFileByPath(final File file) {
      this.file = file;
      if (this.file != null) {
        this.path = this.file.getAbsolutePath();
      } else {
        this.path = "";
      }
    }

    /**
     * Implementation Routine compareTo.
     *
     * @param o Object
     * @return int
     */
    public int compareTo(Object o) {

      if (o instanceof File) {
        final File chkFile = (File) o;
        final String chkPath = chkFile.getAbsolutePath();
        return this.path.compareTo(chkPath);
      } else {
        return -1;
      }
    }

    /**
     * @return the file
     */
    public final synchronized File getFile() {
      return file;
    }
  } // End of class //

  /////////////////////////////////////////////////////////////////

  /**
   * Implementation Routine getFileExt.
   *
   * @param filename String
   * @return String
   */
  public static final String getFileExt(final String filename) {
    if (filename == null) {
      return StringUtils.EMPTY;
    }
    int i = filename.lastIndexOf(".");
    if (i == -1) {
      return StringUtils.EMPTY;
    }
    return filename.substring(i + 1);
  }

  /**
   * Implementation Routine getFileExt.
   *
   * @param file File
   * @return String
   */
  public static final String getFileExt(final File file) {
    if (file == null) {
      return StringUtils.EMPTY;
    }
    return getFileExt(file.getName());
  }

  /**
   * Implementation Routine readLinesFile.
   *
   * @param inFile File
   * @param prefixMode int
   * @param prefix String
   * @return String
   * @throws IOException
   */
  public static final String readLinesFile(
      final File inFile, final int prefixMode, final String prefix) throws IOException {
    return readLinesFile(inFile, prefixMode, prefix, MAX_LINES_READ_MERGE);
  }

  /**
   * Implementation Routine readLinesFile.
   *
   * @param inFile File
   * @param prefixMode int
   * @param prefix String
   * @param maxLinesRead int
   * @return String
   * @throws IOException
   */
  public static final String readLinesFile(
      final File inFile, final int prefixMode, final String prefix, int maxLinesRead)
      throws IOException {

    final StringBuffer resultBuffer = new StringBuffer(128);
    final FileInputStream stream = new FileInputStream(inFile);
    final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
    try {
      String data = "";
      int lineNumber = 1;
      boolean maxReached = false;
      do {
        // Perform simple max, -1 means ignore the max value.
        maxReached = (maxLinesRead == -1) ? false : (lineNumber >= maxLinesRead);
        data = reader.readLine();
        if (data != null) {
          final String curPrefix = prefix == null ? "" : prefix;
          resultBuffer.append(curPrefix).append(lineNumber).append(':');
          resultBuffer.append(inFile.getAbsolutePath()).append(':');
          resultBuffer.append(data).append('\n');
        }
        lineNumber++;
      } while ((data != null) && !maxReached);

      // Add a line number max indicator
      if (maxReached) {
        resultBuffer.append("...\n");
        resultBuffer.append("Max of ").append(lineNumber - 1).append(" lines reached for ");
        resultBuffer.append(inFile.getAbsolutePath());
        resultBuffer.append(data).append('\n');
      } // End of the if //

    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e) {
          ;
        }
      }
    } // End of Finally //
    return resultBuffer.toString();
  }

  /**
   * Implementation Routine readLinesFileSimple.
   *
   * @param inFile File
   * @param prefixMode int
   * @param prefix String
   * @return String
   */
  public static final String readLinesFileSimple(
      final File inFile, final int prefixMode, final String prefix) {
    try {
      return readLinesFile(inFile, prefixMode, prefix);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return "";
  }

  /**
   * Count the number of lines in the buffer.
   *
   * @param inFile File
   * @return String
   * @throws IOException
   */
  public static final String readLinesRaw(final File inFile) throws IOException {

    final StringBuffer resultBuffer = new StringBuffer(128);
    final FileInputStream stream = new FileInputStream(inFile);
    final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
    try {
      String data = "";
      int lineNumber = 1;
      do {
        data = reader.readLine();
        if (data != null) {
          resultBuffer.append(data).append('\n');
        }
        lineNumber++;
      } while (data != null);

    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e) {
          ;
        }
      }
    } // End of Finally //
    return resultBuffer.toString();
  }

  /**
   * Implementation Routine concatFiles.
   *
   * @param inFilesList List
   * @param prefixMode int
   * @param prefix String
   * @return String
   */
  public static final String concatFiles(
      final List inFilesList, final int prefixMode, final String prefix) {
    if (inFilesList == null) {
      return StringUtils.EMPTY;
    }

    return concatFiles(
        (File[]) inFilesList.toArray(new File[inFilesList.size()]), prefixMode, prefix);
  }

  /**
   * Open the files from the list and combine all of the content into a string.
   *
   * @param inFiles File[]
   * @param prefixMode int
   * @param prefix String
   * @return String
   */
  public static final String concatFiles(
      final File[] inFiles, final int prefixMode, final String prefix) {

    if (inFiles != null) {

      final List forSortList = new ArrayList(20);
      for (int i = 0; i < inFiles.length; i++) {
        forSortList.add(new SortableFileByPath(inFiles[i]));
      } // End of for - build list //

      Collections.sort(forSortList);
      final SortableFileByPath[] files =
          (SortableFileByPath[]) forSortList.toArray(new SortableFileByPath[forSortList.size()]);

      // Continue with files //
      final StringBuffer resultBuffer = new StringBuffer(128);
      for (int fileIndex = 0; fileIndex < files.length; fileIndex++) {
        final File file = files[fileIndex].getFile();
        if (file.exists() && file.isFile()) {

          // Read the data from file into the stringbuffer
          resultBuffer.append(readLinesFileSimple(file, prefixMode, prefix));
        } // End of the if //
      } // End of the for //

      return resultBuffer.toString();
    } else {
      return "";
    } // End of if files exist
  } // End of Implementation Routine //

  /**
   * Count the number of lines in the buffer.
   *
   * @param strData String
   * @return int
   * @throws IOException
   */
  public static final int countLinesBuffer(final String strData) throws IOException {

    if (strData != null) {
      final ByteArrayInputStream stream = new ByteArrayInputStream(strData.getBytes());
      final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
      int lineNumber = 0;
      try {
        String data = "";
        do {
          data = reader.readLine();
          if (data != null) {
            lineNumber++;
          } // End of the if //
        } while (data != null);
        return lineNumber;
      } finally {
        if (reader != null) {
          try {
            reader.close();
          } catch (IOException e) {
            ;
          }
        }
      } // End of Finally //
    } // End of Main If //
    return 0;
  }
} // End of the Class //
