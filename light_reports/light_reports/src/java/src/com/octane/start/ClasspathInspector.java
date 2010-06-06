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

import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Find classes in the classpath (reads JARs and classpath folders).
 * 
 * @author [Original Developer] P&aring;l Brattberg, brattberg@gmail.com
 * 
 */
@SuppressWarnings("unchecked")
public class ClasspathInspector {
    
    //private static final Logger LOGGER = Logger.getLogger(ClasspathInspector.class);    
    public static final boolean DEBUG = true;

    public static List<Class> getAllKnownClasses() {
        List<Class> classFiles = new ArrayList<Class>();
        List<File> classLocations = getClassLocationsForCurrentClasspath();
        for (File file : classLocations) {
            classFiles.addAll(getClassesFromPath(file));
        }
        return classFiles;
    }

    public static List<Class> getMatchingClasses(Class interfaceOrSuperclass) {
        List<Class> matchingClasses = new ArrayList<Class>();
        List<Class> classes = getAllKnownClasses();
        log("checking %s classes", classes.size());
        for (Class clazz : classes) {
            if (interfaceOrSuperclass.isAssignableFrom(clazz)) {
                matchingClasses.add(clazz);
                log("class %s is assignable from %s", interfaceOrSuperclass, clazz);
            }
        }
        return matchingClasses;
    }

    public static List<Class> getMatchingClasses(String validPackagePrefix, Class interfaceOrSuperclass) {
        throw new IllegalStateException("Not yet implemented!");
    }

    public static List<Class> getMatchingClasses(String validPackagePrefix) {
        throw new IllegalStateException("Not yet implemented!");
    }

    private static Collection<? extends Class> getClassesFromPath(File path) {
        if (path.isDirectory()) {
            return getClassesFromDirectory(path);
        } else {
            return getClassesFromJarFile(path);
        }
    }

    private static String fromFileToClassName(final String fileName) {
        return fileName.substring(0, fileName.length() - 6).replaceAll("/|\\\\", "\\.");
    }

    private static List<Class> getClassesFromJarFile(File path) {
        List<Class> classes = new ArrayList<Class>();
        log("getClassesFromJarFile: Getting classes for %s", path);

        try {
            if (path.canRead()) {
                JarFile jar = new JarFile(path);
                Enumeration<JarEntry> en = jar.entries();
                while (en.hasMoreElements()) {
                    JarEntry entry = en.nextElement();
                    if (entry.getName().endsWith("class")) {
                        String className = fromFileToClassName(entry.getName());
                        log("\tgetClassesFromJarFile: found %s", className);
                        loadClass(classes, className);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read classes from jar file: " + path, e);
        }

        return classes;
    }

    private static List<Class> getClassesFromDirectory(File path) {
        List<Class> classes = new ArrayList<Class>();
        log("getClassesFromDirectory: Getting classes for " + path);

        // get jar files from top-level directory
        List<File> jarFiles = listFiles(path, new FilenameFilter() {            
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        }, false);
        for (File file : jarFiles) {
            classes.addAll(getClassesFromJarFile(file));
        }

        // get all class-files
        List<File> classFiles = listFiles(path, new FilenameFilter() {            
            public boolean accept(File dir, String name) {
                return name.endsWith(".class");
            }
        }, true);

        // List<URL> urlList = new ArrayList<URL>();
        // List<String> classNameList = new ArrayList<String>();
        int substringBeginIndex = path.getAbsolutePath().length() + 1;
        for (File classfile : classFiles) {
            String className = classfile.getAbsolutePath().substring(substringBeginIndex);
            className = fromFileToClassName(className);
            log("Found class %s in path %s: ", className, path);
            loadClass(classes, className);
        }

        return classes;
    }

    private static List<File> listFiles(File directory, FilenameFilter filter, boolean recurse) {
        List<File> files = new ArrayList<File>();
        File[] entries = directory.listFiles();

        // Go over entries
        for (File entry : entries) {
            // If there is no filter or the filter accepts the
            // file / directory, add it to the list
            if (filter == null || filter.accept(directory, entry.getName())) {
                files.add(entry);
            }

            // If the file is a directory and the recurse flag
            // is set, recurse into the directory
            if (recurse && entry.isDirectory()) {
                files.addAll(listFiles(entry, filter, recurse));
            }
        }

        // Return collection of files
        return files;
    }

    public static List<File> getClassLocationsForCurrentClasspath() {
        List<File> urls = new ArrayList<File>();
        String javaClassPath = System.getProperty("java.class.path");
        if (javaClassPath != null) {
            for (String path : javaClassPath.split(File.pathSeparator)) {
                urls.add(new File(path));
            }
        }
        return urls;
    }

    // todo: this is only partial, probably
    public static URL normalize(URL url) throws MalformedURLException {
        String spec = url.getFile();

        // get url base - remove everything after ".jar!/??" , if exists
        final int i = spec.indexOf("!/");
        if (i != -1) {
            spec = spec.substring(0, spec.indexOf("!/"));
        }

        // uppercase windows drive
        url = new URL(url, spec);
        final String file = url.getFile();
        final int i1 = file.indexOf(':');
        if (i1 != -1) {
            String drive = file.substring(i1 - 1, 2).toUpperCase();
            url = new URL(url, file.substring(0, i1 - 1) + drive + file.substring(i1));
        }

        return url;
    }

    private static void log(String pattern, final Object ... args) {
        if (DEBUG) {
            System.out.printf(pattern + "\n", args);
            //LOGGER.debug(pattern + "\n" + args);
        }
    }
    
    private static void warn(String pattern, final Object ... args) {
        if (DEBUG) {
            //System.out.printf(pattern + "\n", args);
            //LOGGER.debug(pattern + "\n" + args);
        }
    }

    private static void loadClass(List<Class> classes, String className) {
        try {
            Class claz = Class.forName(className, false, ClassLoader.getSystemClassLoader());
            classes.add(claz);
        } catch (ClassNotFoundException cnfe) {
            warn("ClassNotFoundException: Could not load class %s: %s", className, cnfe);
        } catch (NoClassDefFoundError e) {
            warn("NoClassDefFoundError: Could not load class %s: %s", className, e);
        }
    }

    public static final void main() {
        // find all classes in classpath
        List<Class> allClasses = ClasspathInspector.getAllKnownClasses();
        System.out.printf("There are %s classes available in the classpath\n", allClasses.size());

        // find all classes that implement/subclass an interface/superclass
        List<Class> serializableClasses = ClasspathInspector.getMatchingClasses(Serializable.class);
        for (Class clazz : serializableClasses) {
            System.out.printf("%s is Serializable\n", clazz);
        }
        System.out.printf("There are %s Serializable classes available in the classpath\n", serializableClasses.size());

    }
    
    public static void main(String[] args) {
        main();
    }

} // End of the Class //
