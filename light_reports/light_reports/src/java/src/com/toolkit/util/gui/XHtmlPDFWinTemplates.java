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

/**
 * @author Berlin Brown
 * @version 1.0 on 2/1/2009
 */
public class XHtmlPDFWinTemplates {

  public static final double megabytes = 1024.0 * 1024.0;
  public static final Runtime java_runtime = Runtime.getRuntime();

  /**
   * Implementation Routine free_memory_b.
   *
   * @return double
   */
  public static final double free_memory_b() {
    return java_runtime.freeMemory();
  }

  /**
   * Implementation Routine total_memory_b.
   *
   * @return double
   */
  public static final double total_memory_b() {
    return java_runtime.totalMemory();
  }

  /**
   * Implementation Routine max_memory_b.
   *
   * @return double
   */
  public static final double max_memory_b() {
    return java_runtime.maxMemory();
  }

  /**
   * Implementation Routine used_memory_b.
   *
   * @return double
   */
  public static final double used_memory_b() {
    return (total_memory_b() - free_memory_b());
  }

  // Note: used memory == total memory - free memory
  /**
   * Implementation Routine free_memory_m.
   *
   * @return double
   */
  public static final double free_memory_m() {
    return (int) (Math.floor((free_memory_b()) / megabytes));
  }

  /**
   * Implementation Routine total_memory_m.
   *
   * @return double
   */
  public static final double total_memory_m() {
    return (int) (Math.floor((total_memory_b()) / megabytes));
  }

  /**
   * Implementation Routine max_memory_m.
   *
   * @return double
   */
  public static final double max_memory_m() {
    return (int) (Math.floor((max_memory_b()) / megabytes));
  }

  /**
   * Implementation Routine used_memory_m.
   *
   * @return double
   */
  public static final double used_memory_m() {
    return (int) (Math.floor((used_memory_b()) / megabytes));
  }

  /**
   * Implementation Routine memory_usage.
   *
   * @return String
   */
  public static final String memory_usage() {
    final StringBuffer buf =
        new StringBuffer()
            .append("(")
            .append(used_memory_m())
            .append("M/")
            .append(free_memory_m())
            .append("M [")
            .append(total_memory_m())
            .append("M, ")
            .append((max_memory_m()))
            .append("M ])");
    return buf.toString();
  }

  protected static final StringBuffer _ABOUT_MSG_TEMPL_ =
      new StringBuffer()
          .append("Quick XHtml to PDF Renderer Utility  \n\n")
          .append("Version: 0.1_20090206          \n\n")
          .append("-------                              \n")
          .append("- Working Directory:             \n")
          .append(
              "  The Working Directory is the output folder where the PDF documents will get"
                  + " created. \n")
          .append("  Ensure that directory is writable.         \n")
          .append("  Example Usage: 'C:\\tmp\\pdf_work\\_work'\n\n")
          .append("- HTML Input Filename:           \n")
          .append("  Absolute path to the input HTML or XHTML document.               \n")
          .append("  Example Usage: 'C:\\workspace\\WebContent\\File.html'\n\n")
          .append("- Output PDF Filename:           \n")
          .append(
              "  File name for the output PDF document. The document will get written to the"
                  + " working directory.\n")
          .append("  Example Usage: 'xhtml_generated_pdf.pdf'\n\n")
          .append("- Parser Class:                  \n")
          .append("  The parser class is needed to pre-format the input HTML document before it \n")
          .append(
              "  is input to the xhtmlrenderer parser process.  For example, use this class to"
                  + " remove HTML <form> tags.\n")
          .append(
              "  The parer class implementation must sub-class  "
                  + " 'com.toolkit.util.pdf.filter.XHTMLRendererText'.\n")
          .append("  Example Usage: 'com.toolkit.util.pdf.filter.ParserHandler'\n\n")
          .append(
              "- Create PDF (without Parser):  Create the PDF document without a filter parser"
                  + " class. \n")
          .append(
              "- Create PDF (with Parser): Create a PDF document using a parser class in the"
                  + " 'Parser Class field'.  \n")
          .append("- Open HTML File: Browse to an input HTML document.                 \n\n")
          .append("- Troubleshooting:                 \n")
          .append("  * You must provide an input HTML document absolute file path.\n")
          .append("  Use the 'Open File' button to browse to a file.\n\n")
          .append("- The base file URL for the CSS is located at:\n")
          .append("  file:///usr/local/htdocs/              \n")
          .append("- Or you may use the <LIGHT_HOME>/pdf/styles directory:\n")
          .append("  See <LIGHT_HOME>/pdf/styles            \n")
          .append(
              "  When including CSS or images, those files should exist relative to this dir.\n")
          .append("\n");

  protected static final StringBuffer _EXAMPLE_HTML_TEMPL_ =
      new StringBuffer()
          .append(
              "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\""
                  + " \"DTD/xhtml1-transitional.dtd\">")
          .append("<html xmlns = \"http://www.w3.org/1999/xhtml\">")
          .append("  <head><title>The Title</title></head>")
          .append("  <body>     \n")
          .append("    Example <span style=\"color: 999\">PDF</span> document.")
          .append("  </body>    \n")
          .append("<html>       \n")
          .append("\n");

  protected static final StringBuffer _CLASS_NOT_FOUND_TEMPL_ =
      new StringBuffer()
          .append("\n-----------\n")
          .append("Troubleshooting Class Not Found Errors\n")
          .append("-----------\n")
          .append("A Class not found error normally means that the parser library to parse the\n")
          .append("input HTML document is not included in the application's library. \n")
          .append("You can resolve this in three ways:\n")
          .append("(1) Ensure that the package and class name are input correctly.\n")
          .append("(2) Add the parser class to the classpath of this application.\n")
          .append(
              "(3) Get the updated application and ensure that the  parser librar is included.\n");
} // End of the Class
