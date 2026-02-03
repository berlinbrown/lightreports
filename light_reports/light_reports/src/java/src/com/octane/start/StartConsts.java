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
package com.octane.start;

import java.text.MessageFormat;

public final class StartConsts {

  public static final String VERSION = "0.8.20090715";

  public static final String DEFAULT_INSTALL_DIR = "C:\\usr\\local\\projects\\light_logs\\";

  public static final String CLOJURE_MAIN_CLASSNAME = "clojure.lang.Script";

  public static final String CLOJURE_STARTUP_SCRIPT =
      "src\\octane\\toolkit\\octane_main_window.clj";

  /** Field NL. */
  public static final char NL = '\n';

  /** Field EMPTY. (value is """") */
  public static final String EMPTY = "";

  private StartConsts() {
    throw new AssertionError();
  }

  private static final String STARTUP_MSG_TEMPL =
      new StringBuffer(128)
          .append("* Octane Launcher - ")
          .append(VERSION)
          .append(NL)
          .append("* Java Version     : {0}")
          .append(NL)
          .append("* Operating System : {1}")
          .append(NL)
          .append("* Tmp Directory    : {2}")
          .append(NL)
          .toString();

  public static final String VAL_TMP_DIR = System.getProperty("java.io.tmpdir", "/tmp/");
  public static final String VAL_JAVA_VERSION = System.getProperty("java.version", "");
  public static final String VAL_OS_NAME = System.getProperty("os.name", "");
  public static final String VAL_JAVA_HOME =
      System.getProperty("java.home", "C:\\Program Files\\Java\\jdk1.5.0_11");

  public static final String STARTUP_MSG =
      MessageFormat.format(
          STARTUP_MSG_TEMPL, new Object[] {VAL_JAVA_VERSION, VAL_OS_NAME, VAL_TMP_DIR});
  /////////////////////////////////////////////////////////////////
  // Default java libraries to dynamically load
  // relative to the install directory (win32 paths)
  /////////////////////////////////////////////////////////////////

  public static final String[] JAVA_LIBRARIES = {
    "lib\\pdf\\xercesImpl.jar",
    "lib\\pdf\\tagsoup-1.2.jar",
    "lib\\log4j-1.2.15.jar",
    "lib\\octane_commons.jar",
    "lib\\clojure.jar",
    "lib\\swt\\win32\\swt.jar",
    "lib\\jfreechart\\jcommon-1.0.15.jar",
    "lib\\jfreechart\\jfreechart-1.0.12.jar",
    "lib\\jfreechart\\jfreechart-1.0.12-swt.jar",
    "lib\\jfreechart\\gnujaxp.jar",
    "lib\\jfreechart\\swtgraphics2d.jar",
    "lib\\jfreechart\\jfreechart-1.0.12-experimental.jar",
    "lib\\pdf\\minium.jar",
    "lib\\pdf\\core-renderer.jar",
    "lib",
    "conf",
    "src"
  };

  public static final String[] JAVA_LIBRARIES_SECONDARY = {
    "lib\\log4j-1.2.15.jar",
    "lib\\octane_commons.jar",
    "lib\\clojure.jar",
    "lib\\swt\\win32\\swt.jar",
    "lib\\jfreechart\\jcommon-1.0.15.jar",
    "lib\\jfreechart\\jfreechart-1.0.12.jar",
    "lib\\jfreechart\\jfreechart-1.0.12-swt.jar",
    "lib\\jfreechart\\gnujaxp.jar",
    "lib\\jfreechart\\swtgraphics2d.jar",
    "lib\\jfreechart\\jfreechart-1.0.12-experimental.jar",
    "lib\\pdf\\xercesImpl.jar",
    "lib\\pdf\\minium.jar",
    "lib\\pdf\\tagsoup-1.2.jar",
    "lib\\pdf\\core-renderer.jar",
    "lib",
    "conf",
    "src"
  };

  public static final String[] JAVA_LIBRARIES_PDF = {
    "lib\\log4j-1.2.15.jar",
    "lib\\octane_commons.jar",
    "lib\\swt\\win32\\swt.jar",
    "lib\\pdf\\xercesImpl.jar",
    "lib\\pdf\\minium.jar",
    "lib\\pdf\\tagsoup-1.2.jar",
    "lib\\pdf\\core-renderer.jar",
  };

  public static final String[] JAVA_LIBRARIES_NETWORK = {
    "lib\\log4j-1.2.15.jar", "lib\\octane_commons.jar", "lib\\clojure.jar", "conf", "src"
  };
} // End of th Class //
