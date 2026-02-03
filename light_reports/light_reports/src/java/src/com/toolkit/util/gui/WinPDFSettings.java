/**
 * Copyright (c) 2006-2010 Berlin Brown and botnode.com All Rights Reserved
 *
 * <p>http://www.opensource.org/licenses/bsd-license.php
 *
 * <p>All rights reserved.
 *
 * <p>Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * <p>* Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer. * Redistributions in binary form must reproduce the
 * above copyright notice, this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution. * Neither the name of the Botnode.com
 * (Berlin Brown) nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * <p>THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * <p>Date: 1/5/2009, updated 5/5/2010 7/15/2009 - Added Clojure 1.0, other performance fixes and
 * cleanups.
 *
 * <p>Main Description: Light Log Viewer is a tool for making it easier to search log files. Light
 * Log Viewer adds some text highlighting, quick key navigation to text files, simple graphs and
 * charts for monitoring logs, file database to quickly navigate to files of interest, and HTML to
 * PDF convert tool. Light Log was developed with a combination of Clojure 1.0, Java and Scala with
 * use of libs, SWT 3.4, JFreeChart, iText.
 *
 * <p>Quickstart : the best way to run the Light Log viewer is to click on the win32 batch script
 * light_logs.bat (you may need to edit the Linux script for Unix/Linux environments). Edit the
 * win32 script to add more heap memory or other parameters.
 *
 * <p>The clojure source is contained in : HOME/src/octane The java source is contained in :
 * HOME/src/java/src
 *
 * <p>To build the java source, see : HOME/src/java/build.xml and build_pdf_gui.xml
 *
 * <p>Additional Development Notes: The SWT gui and other libraries are launched from a dynamic
 * classloader. Clojure is also started from the same code, and reflection is used to dynamically
 * initiate Clojure. See the 'start' package. The binary code is contained in the octane_start.jar
 * library.
 *
 * <p>Home Page: http://code.google.com/p/lighttexteditor/
 *
 * <p>Contact: Berlin Brown <berlin dot brown at gmail.com>
 */
package com.toolkit.util.gui;

/**
 * Set objects that were created from Clojure side of the application.
 *
 * @author AP417
 */
public class WinPDFSettings {

  /** Type: org.eclipse.swt.custom.StyledText */
  private org.eclipse.swt.widgets.Widget styledText;

  public org.eclipse.swt.widgets.Widget getStyledText() {
    return styledText;
  }

  public synchronized String getMainBufferText() {

    if (!this.hasStyledText()) {
      return "";
    }

    return ((org.eclipse.swt.custom.StyledText) this.getStyledText()).getText();
  }

  public synchronized WinPDFSettings setMainBufferWidget(
      org.eclipse.swt.widgets.Widget styledText) {
    this.styledText = styledText;
    return this;
  }

  /**
   * Return true if the styled text is available.
   *
   * @return
   */
  public boolean hasStyledText() {
    return (this.styledText != null);
  }
} // End of the Class //
