/**
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
 * Date: 1/5/2009, added 5/5/2010, tested with Scala 2.8.r3
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
 * Home Page: http://code.google.com/p/lighttexteditor/
 * 
 * Contact: Berlin Brown <berlin dot brown at gmail.com>
 */
package org.berlin.light.toolkit

import java.io.File

/**
 * Simple templating for one-line filename settings.
 * 
 * Example settings:
 * %input_filename_no_ext% - Replace string with filename without extension.  my_filename
 * %input_filename_ext% - Replace string with filename.  my_filename.xml
 * 
 * %date1% - Date/time format one
 * %date2% - Date/time format one
 * %date2% - Date/time format one
 * 
 * Example filename settings one-liner:
 * 
 * %LIGHT_HOME%/_conf/sys/_work/report_output/r%date2%/%input_filename_no_ext%_%date1%.pdf
 * 
 * Will create a file:
 * 
 * home/_conf/sys/_work/report_output/r2009_03_03/some_file_200903031456.pdf
 * 
 * @author bbrown
 *
 */
class TemplateParseFileLine {

	val lightHomeKey = "%LIGHT_HOME%"
	
	def findTemplateLightHome(line:String) : String = {
	    
	        val installprop = if (System.getProperty("octane.install.dir") == null) "." else System.getProperty("octane.install.dir")
			val file = new File(installprop)
			line.replaceAll(lightHomeKey, file.getAbsolutePath)
	}		
	
	def findTemplateLightHomeToExternal(line:String) : String = {
        
            val installprop = if (System.getProperty("octane.install.dir") == null) "." else System.getProperty("octane.install.dir")
            val file = new File(installprop)            
            line.replaceAll(lightHomeKey, file.toURI().toURL().toExternalForm())
    }
		
} // End of the Class //