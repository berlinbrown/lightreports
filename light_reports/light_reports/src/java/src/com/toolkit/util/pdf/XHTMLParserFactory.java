/**
 * Copyright (c) 2006-2007 Berlin Brown and botnode.com All Rights Reserved
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
 * <p>Date: 1/5/2009 7/15/2009 - Added Clojure 1.0, other performance fixes and cleanups.
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
 * <p>Metrics: (as of 7/15/2009) Light Log Viewer consists of 6500 lines of Clojure code, and
 * contains wrapper code around the Java source. There are 2000+ lines of Java code in the Java
 * library for Light Log Viewer.
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
package com.toolkit.util.pdf;

/**
 * Utility class for taking an input HTML document, formatting the content and then ensuring that is
 * properly formed for XHTMLRenderer and TagSoup.
 *
 * <p>Example J2EE Filter, configured through the web.xml
 *
 * @see XHTMLRendererFilter
 *     <p>Example Implementation of HTML Parser handler
 * @see ScoreboardHandler
 *     <p>Example Servlet, stream PDF to the client
 * @see PDFServlet
 *     <p>Base Xhtmlrenderer parser utilities
 * @see XHTMLRendererBase
 *     <p>This utility is available at the APPLICATION LEVEL (can be used without web libs) or WEB
 *     LEVEL: <strong>PDF Parser Available: <i>APPLICATION LEVEL</i></strong>
 * @author Berlin Brown
 * @version 1.0 on 2/1/2009
 */
public class XHTMLParserFactory {

  /**
   * Implementation Routine create.
   *
   * @param full_classname String
   * @param origHtmlData String
   * @return XHTMLRendererText
   */
  public static final XHTMLRendererText create(
      final String full_classname, final String origHtmlData) {

    // Resolve bug where error message may flow through to parsing objects.
    if ((origHtmlData == null) || (origHtmlData.length() <= 8)) {
      XHTMLRendererBase.println(
          "<Factory parser create> invalid document, falling back to error document");
      return null;
    }
    XHTMLRendererBase.println(
        "<Factory parser create> attempting to create parser, classname="
            + full_classname
            + " size="
            + origHtmlData.length());
    if ((full_classname != null) && (full_classname.length() != 0)) {

      // Dynamically add parser
      Class parserClass;
      try {
        parserClass = Class.forName(full_classname);
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
        throw new RuntimeException(
            "Invalid parser class (ClassNotFoundException) err=" + e.getMessage());
      }
      XHTMLRendererText parser;
      try {
        parser =
            (XHTMLRendererText)
                parserClass.getConstructors()[0].newInstance(new Object[] {origHtmlData});

      } catch (Exception x) {
        x.printStackTrace();
        throw new RuntimeException(
            "Invalid parser class (" + x.getClass().getName() + ") err=" + x.getMessage());
      }
      parser.setText(origHtmlData);
      return parser;

    } else {
      return null;
    } // End of the if //
  } // End of the method
}
