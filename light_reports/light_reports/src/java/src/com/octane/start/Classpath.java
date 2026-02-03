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

//
// Modfied for log viewer. 6/11/2009
// ========================================================================
// Copyright 2002-2005 Mort Bay Consulting Pty. Ltd.
// ------------------------------------------------------------------------
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ========================================================================

package com.octane.start;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Class to handle CLASSPATH construction
 *
 * @author Jan Hlavaty, modified by Berlin Brown
 * @version $Revision: 1.0 $
 */
public class Classpath {

  Vector _elements = new Vector(20);

  /** Constructor for Classpath. */
  public Classpath() {}

  /**
   * Constructor for Classpath.
   *
   * @param initial String
   */
  public Classpath(String initial) {
    addClasspath(initial);
  }

  /**
   * Implementation Routine addComponent.
   *
   * @param component String
   * @return boolean
   */
  public boolean addComponent(String component) {
    if ((component != null) && (component.length() > 0)) {
      try {
        File f = new File(component);
        if (f.exists()) {
          File key = f.getCanonicalFile();
          if (!_elements.contains(key)) {
            _elements.add(key);
            return true;
          }
        }
      } catch (IOException e) {
      }
    }
    return false;
  }

  /**
   * Implementation Routine addComponent.
   *
   * @param component File
   * @return boolean
   */
  public boolean addComponent(File component) {

    if (component != null) {
      try {
        if (component.exists()) {
          File key = component.getCanonicalFile();
          if (!_elements.contains(key)) {
            _elements.add(key);
            return true;
          }
        }
      } catch (IOException e) {
      }
    }
    return false;
  }

  /**
   * Implementation Routine addClasspath.
   *
   * @param s String
   * @return boolean
   */
  public boolean addClasspath(String s) {

    boolean added = false;
    if (s != null) {
      StringTokenizer t = new StringTokenizer(s, File.pathSeparator);
      while (t.hasMoreTokens()) {
        added |= addComponent(t.nextToken());
      }
    }
    return added;
  }

  /**
   * Implementation Routine toString.
   *
   * @return String
   */
  public String toString() {

    StringBuffer cp = new StringBuffer(1024);
    int cnt = _elements.size();
    if (cnt >= 1) {
      cp.append(((File) (_elements.elementAt(0))).getPath());
    }
    for (int i = 1; i < cnt; i++) {
      cp.append('\n');
      cp.append(File.pathSeparatorChar);
      cp.append(((File) (_elements.elementAt(i))).getPath());
    }
    return cp.toString();
  }

  /**
   * Implementation Routine getClassLoader.
   *
   * @return ClassLoader
   */
  public ClassLoader getClassLoader() {

    int cnt = _elements.size();
    URL[] urls = new URL[cnt];
    for (int i = 0; i < cnt; i++) {
      try {
        String u = ((File) (_elements.elementAt(i))).toURL().toString();
        u = encodeFileURL(u);
        urls[i] = new URL(u);

      } catch (MalformedURLException e) {
      }
    }

    ClassLoader parent = Thread.currentThread().getContextClassLoader();
    System.out.println("[Classpath Loader] - thread class loader parent == " + (parent == null));
    if (parent == null) {
      System.out.println("[Classpath Loader] - using class classloader parent");
      parent = Classpath.class.getClassLoader();
    }
    if (parent == null) {
      System.out.println("[Classpath Loader] - using system classloader parent");
      parent = ClassLoader.getSystemClassLoader();
    }
    return new Loader(urls, parent);
  }

  /**
   * @author Berlin
   */
  private class Loader extends URLClassLoader {
    String name;

    /**
     * Constructor for Loader.
     *
     * @param urls URL[]
     * @param parent ClassLoader
     */
    Loader(URL[] urls, ClassLoader parent) {
      super(urls, parent);
      name = "StartLoader:" + super.toString() + ":" + Arrays.asList(urls);
    }

    /**
     * Implementation Routine toString.
     *
     * @return String
     */
    public String toString() {
      return name;
    }
  } // End of the Class //

  /**
   * Implementation Routine encodeFileURL.
   *
   * @param path String
   * @return String
   */
  public static String encodeFileURL(String path) {

    byte[] bytes;
    try {
      bytes = path.getBytes("UTF-8");
    } catch (UnsupportedEncodingException e) {
      bytes = path.getBytes();
    }
    StringBuffer buf = new StringBuffer(bytes.length * 2);
    buf.append("file:");

    synchronized (buf) {
      for (int i = 5; i < bytes.length; i++) {
        byte b = bytes[i];
        switch (b) {
          case '%':
            buf.append("%25");
            continue;
          case ' ':
            buf.append("%20");
            continue;
          case '/':
          case '.':
          case '-':
          case '_':
            buf.append((char) b);
            continue;
          default:
            // let's be over conservative here!
            if (Character.isJavaIdentifierPart((char) b)) {
              if (b >= 'a' && b <= 'z' || b >= 'A' && b <= 'Z' || b >= '0' && b <= '9') {
                buf.append((char) b);
                continue;
              }
            }
            buf.append('%');
            buf.append(Integer.toHexString((0xf0 & (int) b) >> 4));
            buf.append(Integer.toHexString((0x0f & (int) b)));
            continue;
        }
      }
    }
    return buf.toString();
  }
} // End of Class //
