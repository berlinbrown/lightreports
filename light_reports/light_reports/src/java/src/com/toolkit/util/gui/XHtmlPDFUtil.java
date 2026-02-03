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
package com.toolkit.util.gui;

import java.io.File;

/**
 * Misc PDF utils
 *
 * @author Berlin Brown
 * @version 1.0 on 2/1/2009
 */
public final class XHtmlPDFUtil {

  public static final String _AD_PROP_ = "exe_xhtmlrenderer_adobe";
  public static final String _AD_8_ = "C:\\Program Files\\Adobe\\Reader 8.0\\Reader\\AcroRd32.exe";
  public static final String _AD_9_ = "C:\\Program Files\\Adobe\\Reader 9.0\\Reader\\AcroRd32.exe";
  public static final String _AD_HOME_ = "C:\\Program Files\\Adobe";

  /////////////////////////////////////////////////////////////////
  // Adobe Reader Detect UTils
  // We will detect reader first by system property
  // Then by the Adobe 8
  // Then by what is found in the directory
  // Returns 'null' on error.
  /////////////////////////////////////////////////////////////////
  /**
   * Implementation Routine checkAdobe.
   *
   * @param path String
   * @param msg String
   * @return String
   */
  private String checkAdobe(final String path, final String msg) {
    if (path != null) {
      final File propf = new File(path);
      if (propf.exists() && (!propf.isDirectory())) {
        // Note: file may not be an executable
        System.out.println("INFO: Found Adobe Reader at <" + msg + "> : " + path);
        return propf.getAbsolutePath();
      } else {
        System.out.println("WARN: Could not find Adobe Reader at <" + msg + "> : " + path);
      } // End of
    } else {
      return null;
    }
    return null;
  }

  /**
   * Implementation Routine findAdobeReader.
   *
   * @return String
   */
  public String findAdobeReader() {

    try {
      final String adobeReaderProp = System.getProperty(_AD_PROP_);
      final String res1 = checkAdobe(adobeReaderProp, "Prop check");
      if (res1 != null) {
        return res1;
      } else {
        // Prop check failed
        // Adobe 8 check
        final String res2 = checkAdobe(_AD_9_, "Find Adobe 9");
        if (res2 != null) {
          return res2;
        } else {
          // Adobe 8 check failed.
          return findAdobeFiles();
        }
      } // End of if

    } catch (Exception e) {
      e.printStackTrace();
      return null;
    } // End of try catch
  } // End of Implementation Routine //

  /**
   * Implementation Routine findAdobeFiles.
   *
   * @return String
   */
  public String findAdobeFiles() {
    final File dir = new File(_AD_HOME_);
    if (dir.isDirectory()) {
      // Iterate through all the files until a match is found.
      final File dirs[] = dir.listFiles();
      for (int i = 0; i < dirs.length; i++) {
        if (dirs[i].isDirectory() && dirs[i].canRead()) {
          final File reader_exe =
              new File(
                  dirs[i].getAbsolutePath()
                      + File.separator
                      + "Reader"
                      + File.separator
                      + "AcroRd32.exe");
          final String res3 = checkAdobe(reader_exe.getAbsolutePath(), "Find Adobe Reader");
          if (res3 != null) {
            // fast fail on first find
            return res3;
          }
        }
      } // End of for //
    } else {
      return null;
    }
    return null;
  }
} // End of the Class //
