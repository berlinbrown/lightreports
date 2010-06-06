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
 * Main Description: Light is a simple text editor in clojure
 * Contact: Berlin Brown <berlin dot brown at gmail.com>
 *********************************************************************/

package com.light.pdf.gui;

/**
 * Light HTML Templates.
 */
public class LightHtmlPDFWinTemplates {

	/**
	 * Megabyte size.
	 */
    public static final double  megabytes        = 1024.0 * 1024.0;
    
    /**
     * Java runtime.
     */
    public static final Runtime java_runtime     = Runtime.getRuntime();

    /**
     * Free memory usage.
     * @return
     */
    public static final double free_memory_b  () { return java_runtime.freeMemory();  }
    
    /**
     * Total memory usage.
     * @return
     */
    public static final double total_memory_b () { return java_runtime.totalMemory(); }
    
    /**
     * Max memory usage.
     * @return
     */
    public static final double max_memory_b   () { return java_runtime.maxMemory();   }
    
    /**
     * Used memory usage.
     * @return
     */
    public static final double used_memory_b  () { return (total_memory_b() - free_memory_b()); }

    // Note: used memory == total memory - free memory
    /**
     * Free memory usage.
     */
    public static final double free_memory_m  () { return (int) (Math.floor ( (free_memory_b())  / megabytes )) ; }
    
    /**
     * Total memory usage.
     * @return
     */
    public static final double total_memory_m () { return (int) (Math.floor ( (total_memory_b()) / megabytes )) ; }
    
    /**
     * Max memory usage.
     * @return
     */
    public static final double max_memory_m   () { return (int) (Math.floor ( (max_memory_b())   / megabytes )) ; }
    
    /**
     * Used memory usage.
     * @return
     */
    public static final double used_memory_m  () { return (int) (Math.floor ( (used_memory_b())  / megabytes )) ; }

    /**
     * Memory Usage.
     * @return
     */
    public static final String memory_usage () {
       final StringBuffer buf = new StringBuffer()
                          .append("(").append(used_memory_m()).append("M/")
                          .append(free_memory_m())
                          .append("M [").append(total_memory_m()).append("M,")
                          .append((max_memory_m()))
                          .append("M ])");
        return buf.toString();
    }    

    /**
     * Example HTML Template.
     */
    protected static final StringBuffer _EXAMPLE_HTML_TEMPL_ = new StringBuffer()
        .append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"DTD/xhtml1-transitional.dtd\">")
        .append("<html xmlns = \"http://www.w3.org/1999/xhtml\">")
        .append("  <head><title>The Title</title></head>")
        .append("  <body>     \n")
        .append("    Example <span style=\"color: 999\">PDF for XHTMLRenderer</span> document.")
        .append("  </body>    \n")
        .append("<html>       \n")
        .append("\n");
    
    protected static final StringBuffer _CLASS_NOT_FOUND_TEMPL_ = new StringBuffer()
    .append("\n-----------\n")
    .append("Troubleshooting Class Not Found Errors\n")
    .append("-----------\n")
    .append("A Class not found error normally means that the parser library to parse the\n")
    .append("input HTML document is not included in the application's library. \n")
    .append("You can resolve this in three ways:\n")
    .append("(1) Ensure that the package and class name are input correctly.\n")
    .append("(2) Add the parser class to the classpath of this application.\n")
    .append("(3) Get the updated application and ensure that the  parser library is included.\n");
    
    /**
     * About Message.
     */
    protected static final StringBuffer _ABOUT_MSG_TEMPL_ = new StringBuffer()
        .append("XHtml to PDF Renderer Utility  \n\n")
        .append("Version: 0.2_20090308          \n\n")
        .append("(c) Copyright Berlin Brown and Botnode.com 2009.  All rights reserved.   \n")
        .append("This tool is powered by Eclipse technology (see SWT)\n\n")
        
        .append("- <<Working Directory>>:             \n")
        .append("  The Working Directory is the output folder where the PDF documents will get created. \n")
        .append("  Ensure that directory is writable.         \n")
        .append("  Example Usage: 'C:\\tmp\\pdf_work\\_work'\n\n")

        .append("- <<HTML Input Filename>>:           \n")
        .append("  Absolute path to the input HTML or XHTML document.               \n")
        .append("  Example Usage: 'C:\\workspace\\WebContent\\NewRepServlet2.html'\n\n")

        .append("- <<Output PDF Filename>>:           \n")
        .append("  File name for the output PDF document. The document will get written to the working directory.\n")
        .append("  Example Usage: 'xhtml_generated_pdf.pdf'\n\n")
        
        .append("- Troubleshooting:                 \n")
        .append("  * You must provide an input HTML document absolute file path.\n")
        .append("  Use the 'Open File' button to browse to a file.\n\n")
        .append("\n");

    
} // End of the Class

