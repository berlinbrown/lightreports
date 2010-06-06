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

import java.lang.reflect.Method;

import com.octane.start.services.ClassPathLoaderService;

public final class PDFDynamicStartWin {
   
    private PDFDynamicStartWin() {
        
    }           
    public static final void createPDFWindowShell(final Object shell) throws Exception {
        
        if (shell == null) {
            System.err.println("Invalid arguments : service => shell =>" + shell);
            return;
        } // End of the if //
        
        //final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        //final ClassLoader cl = ClassLoader.getSystemClassLoader();
        final ClassPathLoaderService service = new ClassPathLoaderService();
        service.setInstallDir(OctaneLauncherMain.getInstallDir());
        service.setHostDir(OctaneLauncherMain.VAL_OCTANE_HOST_DIR);
        service.setHasDebug(OctaneLauncherMain.HAS_DEBUG);
        final ClassLoader cl = service.loadClasspathPDFNOSet();        
        synchronized(cl) {
            System.out.println("[debug] Service Class Loader.a: " + ClassLoader.getSystemClassLoader());
            System.out.println("[debug] Service Class Loader.b: " + cl);
            System.out.println("[debug] Service Class Loader.c: " + PDFDynamicStartWin.class.getClassLoader());
                       
            // Perform a simple sanity check
            System.out.println("-----------------");
            System.out.println("[From DynaClass.1] : " + cl.loadClass("com.octane.start.services.IStartService"));
            System.out.println("[From DynaClass.2] : " + cl.loadClass("org.eclipse.swt.SWT"));               
            System.out.println("[From DynaClass.3] : " + cl.loadClass("org.eclipse.swt.events.SelectionListener"));
            System.out.println("[From DynaClass.4] : " + cl.loadClass("com.toolkit.util.gui.SimpleXHtmlPDFWin"));           
            System.out.println("-----------------"); 
            
            Class dd = cl.loadClass("org.eclipse.swt.events.SelectionListener");
            ClassLoader dd2 = dd.getClassLoader();
            System.out.println("[From DynaClass.5] : " + dd2);
            System.out.println("*************");
            
            // Now launch the shell //
            // [commented] final Class winClass = Class.forName("com.toolkits.util.gui.SimpleXHtmlPDFWin");                       
            final Class winClass = dd2.loadClass("com.toolkit.util.gui.SimpleXHtmlPDFWin");
            final Method methodCreate = winClass.getMethod("createPDFWindowShell", new Class [] { Object.class });
            methodCreate.invoke(null, shell);
                        
        } // End of the Sync //
    }
    
} // End of the Class //
