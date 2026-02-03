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

import com.octane.start.services.ClassPathLoaderService;
import com.octane.start.services.IStartService;

/**
 * The launcher application performs all Octane program launching functionality. Check if the octane
 * application has been installed, if not installed launch simple swing gui application to install.
 * Perform junit check. Once installed, then launch application.
 *
 * <p>First priority service is to load the classpath and then launch Clojure.
 * --------------------------
 *
 * <p>Load the core libraries and then load the secondary libraries.
 */
public class OctaneLauncherMain implements IServiceRunner {

  // We are only looking for win32 filepaths //

  /////////////////////////////////////////////

  public static final String PROP_OCTANE_INSTALL_DIR = "octane.install.dir";

  public static final String PROP_OCTANE_HOST_DIR = "octane.host.dir";

  /////////////////////////////////////////////

  public static final String VAL_OCTANE_INSTALL_DIR =
      System.getProperty(PROP_OCTANE_INSTALL_DIR, ".");

  public static final String VAL_OCTANE_HOST_DIR = System.getProperty(PROP_OCTANE_HOST_DIR, "");

  public static final boolean HAS_DEBUG = (System.getProperty("DEBUG", null) != null);

  /////////////////////////////////////////////////////////////////

  public static final String getInstallDir() {
    if (Manager.isEmpty(VAL_OCTANE_INSTALL_DIR)) {
      // Set the system property with the default and return.
      System.setProperty(PROP_OCTANE_INSTALL_DIR, StartConsts.DEFAULT_INSTALL_DIR);
      return StartConsts.DEFAULT_INSTALL_DIR;
    } else {
      return VAL_OCTANE_INSTALL_DIR;
    }
  }

  /** All services are loading dynamically. */
  public final void runClasspathService() {
    try {
      final Class cClasspath = Class.forName(ClassPathLoaderService.DYNAMIC_CLASS_NAME);
      final IStartService service = (IStartService) cClasspath.newInstance();
      service.setInstallDir(getInstallDir());
      service.setHostDir(VAL_OCTANE_HOST_DIR);
      service.setHasDebug(HAS_DEBUG);
      service.runService();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();

    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } // End of the Try Catch //
  }

  /////////////////////////////////////////////////////////////////

  /**
   * Main entry point for the octane launcher.
   *
   * @param args
   */
  public static void main(final String[] args) {

    System.out.println(StartConsts.STARTUP_MSG);
    System.out.println("* Install Directory : " + VAL_OCTANE_INSTALL_DIR);
    final long startProcT = System.currentTimeMillis();
    final OctaneLauncherMain launcher = new OctaneLauncherMain();
    launcher.runClasspathService();
    final long endProcT = System.currentTimeMillis();
    final long tDiff = endProcT - startProcT;
    System.out.println("Processing Time : " + tDiff);
  }
} // End of the class //
