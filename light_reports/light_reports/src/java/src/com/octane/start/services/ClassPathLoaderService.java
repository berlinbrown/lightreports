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
package com.octane.start.services;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.octane.start.Classpath;
import com.octane.start.OctaneClojureScript;
import com.octane.start.OctaneLauncherMain;
import com.octane.start.StartConsts;

public class ClassPathLoaderService implements IStartService {

    private String hostDir;

    private String installDir;

    private boolean debug;
                
    public static final String DYNAMIC_CLASS_NAME = "com.octane.start.services.ClassPathLoaderService";

    private boolean loadedMainLibraries = false;
    
    //////////////////////////////////////////////////////////////////
    
    public String toString() {
        final StringBuffer buf = new StringBuffer(80);
        buf.append("ClassPathLoaderService : ").append(super.toString()).append(':');
        buf.append(" Install-Dir : ").append(this.installDir);
        return buf.toString();
    }
        
    /**
     * @return the debug
     */
    public boolean hasDebug() {
        return debug;
    }

    /**
     * @param debug
     *            the debug to set
     */
    public void setHasDebug(final boolean debug) {
        this.debug = debug;
    }

    /**
     * @return the hostDir
     */
    public String getHostDir() {
        return hostDir;
    }

    /**
     * @param hostDir
     *            the hostDir to set
     */
    public void setHostDir(final String hostDir) {
        this.hostDir = hostDir;
    }
    
    /**
     * @return the installDir
     */
    public String getInstallDir() {
        return installDir;
    }

    /**
     * @param installDir
     *            the installDir to set
     */
    public void setInstallDir(final String installDir) {
        this.installDir = installDir;
    }
    
    public void setHostDir() {
        this.setHostDir(OctaneLauncherMain.VAL_OCTANE_HOST_DIR);
    }
    
    public void setInstallDir() {
        this.setInstallDir(OctaneLauncherMain.getInstallDir());
    }
    
    public void setHasDebug() {
        this.setHasDebug(OctaneLauncherMain.HAS_DEBUG);
    }
    
    public void init() {
        this.setHasDebug();
        this.setHostDir();
        this.setInstallDir();
    }

    /**
     * Run a simple filesystem check on the files we are attempting to load.
     * This method will not fail on error.
     * 
     */
    private final void jarSanityCheck() {
        jarSanityCheck(StartConsts.JAVA_LIBRARIES);
    }
    
    private final void jarSanityCheckSecondary() {
        jarSanityCheck(StartConsts.JAVA_LIBRARIES_SECONDARY);
    }
    
    private final void jarSanityCheck(final String [] jars) {
        int filesPass = 0;
        int totalFiles = 0;
        for (int libNameIndex = 0; libNameIndex < jars.length; libNameIndex++) {
            final String relPath = jars[libNameIndex];
            final String path = this.getInstallDir() + relPath;
            final File libFile = new File(path);
            if (libFile.exists()) {
                System.out.println("[INFO : Lib File Check] : file=" + libFile.getAbsolutePath() + " pass ..." + libFile.exists());
                filesPass++;
            } else {
                System.out.println("[INFO : Lib File Check] : file=" + libFile.getAbsolutePath() + " *INVALID* ..." + libFile.exists());
            }
            totalFiles++;
        } // End of For //
        System.out.println("[INFO : Lib File Check] : valid files=" + filesPass + " / total=" + totalFiles);    
    }

    private final void invokeMain(ClassLoader classloader, String classname, String[] args) throws IllegalAccessException,
            InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
        
        final Class invoked_class = classloader.loadClass(classname);                
        final Class[] method_param_types = new Class[1];
        method_param_types[0] = args.getClass();
        final Method main = invoked_class.getDeclaredMethod("main", method_param_types);        
        final Object[] method_params = new Object[1];
        method_params[0] = args;
        main.invoke(null, method_params);
    }

    private static final void debugClassLoader(final java.lang.ClassLoader cl) {
        if (cl != null) {
            final int threadCount = Thread.activeCount();
            System.out.println("Active threads : " + threadCount);
            try {                
                System.out.println(cl.loadClass("clojure.lang.Script"));
                System.out.println(cl.loadClass("org.eclipse.swt.SWT"));
                System.out.println(cl.loadClass("org.eclipse.swt.events.SelectionListener"));
                System.out.println(cl.loadClass("org.apache.log4j.Logger"));
                
                try {
                    System.out.println(cl.loadClass("org.xhtmlrenderer.util.XRLog"));
                } catch(Exception ne) {
                    System.out.println("Debug Class Loader Err : " + ne);
                }
                System.out.println("[INFO] Thread ID : " + Thread.currentThread());
            } catch (ClassNotFoundException e) {
                System.out.println("Debug Class Loader Err");
                e.printStackTrace();
            }
        } // End of the if //    
    }
    
    private final synchronized ClassLoader loadClasspath() {
        return loadClasspath(StartConsts.JAVA_LIBRARIES);
    }
    
    public final synchronized ClassLoader loadClasspathSecondary() {
        return loadClasspath(StartConsts.JAVA_LIBRARIES_SECONDARY);
    }
    
    public final synchronized ClassLoader loadClasspathPDFNOSet() {
        return loadClasspathNoSet(StartConsts.JAVA_LIBRARIES_PDF);
    }
    
    private final ClassLoader loadClasspath(final String [] jars) {
        final ClassLoader cl = loadClasspathNoSet(jars);
        // Invoke main(args) using new classloader.        
        Thread.currentThread().setContextClassLoader(cl);
        return cl;
    }
    
    public final ClassLoader loadClasspathNoSet(final String [] jars) {
        
        final Classpath classpath = new Classpath();              
        int filesPass = 0;
        int totalFiles = 0;
        
        final File tools = new File(StartConsts.VAL_JAVA_HOME + "\\..\\lib\\tools.jar");
        System.out.println("tools.jar exists? ... " + tools.exists() + " | " + tools.getAbsolutePath());
        classpath.addComponent(tools);        
        
        // http://lists.jboss.org/pipermail/jboss-user/2006-December/031233.html
        
        for (int libNameIndex = 0; libNameIndex < jars.length; libNameIndex++) {
            try {
                final String relPath = jars[libNameIndex];
                final String path = this.getInstallDir() + relPath;
                final File libFile = new File(path);
                
                /////////////////////////////////
                // Load the classpath component using the dynamic loader
                /////////////////////////////////
                final boolean res = classpath.addComponent(libFile);
                System.out.println("        . Classpath Loader Check : addComponent? " + res);
                filesPass++;
            } catch(Exception e) {
                e.printStackTrace();
            } // End of the Try - Catch //
            totalFiles++;
        } // End of For //
        System.out.println("[INFO : Classpath Loader Check] : valid files=" + filesPass + " / total=" + totalFiles);
        
        final ClassLoader cl = classpath.getClassLoader();        
        System.out.println("[INFO : Classpath Loader Check] : classpath=" + cl);               
        return cl;
    }
    
    public void loadSecondaryLibraries() {
        if (this.loadedMainLibraries) {
            final Runnable runnable = new Runnable() {
                public void run() {
                    System.out.println("Loading secondary libraries");
                    jarSanityCheckSecondary();
                    final ClassLoader cl = loadClasspathSecondary();
                    debugClassLoader(cl);
                }
            };
            new Thread(runnable).start();
        } // End of if //
    }
    
    /**
     * Classpath loader service.
     */
    public synchronized void runService() {
        System.out.println("Running classpath loader service, loaded? " + this.loadedMainLibraries);
        try {
            if (this.loadedMainLibraries) {                
                this.loadSecondaryLibraries();                
                return;
            }
            this.jarSanityCheck();            
            // Load the loader with the components, that were verified 
            // as part of the sanity check.
            ClassLoader cl = this.loadClasspath();
            final String absPathScript = this.getInstallDir() + StartConsts.CLOJURE_STARTUP_SCRIPT;           
            debugClassLoader(cl);
            loadedMainLibraries = true;
            System.out.println("Initial run service classloader : " + cl);
            OctaneClojureScript.main(this, cl);            
        } catch(Exception e) {
            e.printStackTrace();
        } // End of Try - Catch //
    }

    public int exitVM(int exitCode) {
        System.out.println("Exiting VM - " + exitCode);
        System.exit(exitCode);
        return exitCode;
    }

} // End of the class //
