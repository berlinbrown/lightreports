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
/**
 * Also see:
 * http://static.springframework.org/spring/docs/2.5.x/api/org/springframework/beans/factory/access/SingletonBeanFactoryLocator.html
 */
package com.octane.start;

import com.octane.start.services.IStartService;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class OctaneClojureScript {

  private static final String BASIC_TEST_WIN_GLOBALS = "octane.toolkit.octane_script_globals";

  private static final String BASIC_TEST_WIN_NAMESPACE = "octane.toolkit.octane_main_window";

  // private static final String BASIC_TEST_WIN_NAMESPACE = "test.simple_main";

  /////////////////////////////////////////////////////////////////

  public static final Object invokeSymbolCreate(ClassLoader classloader, final String nsName)
      throws Exception {
    final Class invoked_class = classloader.loadClass("clojure.lang.Symbol");
    final Class[] method_param_types = new Class[1];
    method_param_types[0] = String.class;
    final Method main = invoked_class.getDeclaredMethod("create", method_param_types);

    final Object[] method_params = new Object[1];
    method_params[0] = nsName;
    return main.invoke(null, method_params);
  }

  public static final Object invokeVarIntern(
      ClassLoader classloader, final Object ns, final Object sy) throws Exception {
    final Class invoked_class = classloader.loadClass("clojure.lang.Var");
    final Class nameSpaceClass = classloader.loadClass("clojure.lang.Namespace");
    final Class symbolClass = classloader.loadClass("clojure.lang.Symbol");

    final Class[] method_param_types = new Class[2];
    method_param_types[0] = nameSpaceClass;
    method_param_types[1] = symbolClass;
    final Method main = invoked_class.getDeclaredMethod("intern", method_param_types);

    System.out.println("invokeVarIntern : " + ns + " |" + sy);

    final Object[] method_params = new Object[2];
    method_params[0] = ns;
    method_params[1] = sy;
    return main.invoke(null, method_params);
  }

  public static final Object invokeVarIntern(
      ClassLoader classloader, final Object ns, final Object sy, final Object root)
      throws Exception {
    final Class invoked_class = classloader.loadClass("clojure.lang.Var");
    final Class nameSpaceClass = classloader.loadClass("clojure.lang.Namespace");
    final Class symbolClass = classloader.loadClass("clojure.lang.Symbol");

    final Class[] method_param_types = new Class[3];
    method_param_types[0] = nameSpaceClass;
    method_param_types[1] = symbolClass;
    method_param_types[2] = Object.class;
    final Method main = invoked_class.getDeclaredMethod("intern", method_param_types);

    final Object[] method_params = new Object[3];
    method_params[0] = ns;
    method_params[1] = sy;
    method_params[2] = root;
    return main.invoke(null, method_params);
  }

  public static final Object invokeFindOrCreate(ClassLoader classloader, Object sy)
      throws Exception {
    final Class invoked_class = classloader.loadClass("clojure.lang.Namespace");
    final Class symbolClass = classloader.loadClass("clojure.lang.Symbol");

    final Class[] method_param_types = new Class[1];
    method_param_types[0] = symbolClass;
    final Method main = invoked_class.getDeclaredMethod("findOrCreate", method_param_types);

    final Object[] method_params = new Object[1];
    method_params[0] = sy;
    return main.invoke(null, method_params);
  }

  public static final Object invokeVarInvoke(
      ClassLoader classloader, final Object obj, final Object sy) throws Exception {
    final Class invoked_class = classloader.loadClass("clojure.lang.Var");
    final Class[] method_param_types = new Class[1];
    method_param_types[0] = Object.class;
    final Method main = invoked_class.getDeclaredMethod("invoke", method_param_types);

    final Object[] method_params = new Object[1];
    method_params[0] = sy;
    return main.invoke(obj, method_params);
  }

  public static final Object invokeClojureNS(ClassLoader classloader) throws Exception {
    final Class invoked_class = classloader.loadClass("clojure.lang.RT");
    final Field field = invoked_class.getDeclaredField("CLOJURE_NS");
    return field.get(invoked_class);
  }

  // INVOKE CONTRACT /////
  public Object invokeContract(IStartService precondInput, ClassLoader cl) throws Exception {

    final IStartService startService = (IStartService) precondInput;
    //////////////////////////////////
    // Init the clojure main library
    //////////////////////////////////
    final Object symbolClojureMain = invokeSymbolCreate(cl, "clojure.main");
    final Object varRequire =
        invokeVarIntern(cl, invokeClojureNS(cl), invokeSymbolCreate(cl, "require"));

    invokeVarInvoke(cl, varRequire, symbolClojureMain);

    // Call require on our utility clojure code
    // Set the variable spring-context for use in the clojure script
    final Object findOrCreate =
        invokeFindOrCreate(cl, invokeSymbolCreate(cl, BASIC_TEST_WIN_GLOBALS));
    System.out.println("invoke NSFindOrCreate : " + findOrCreate);
    invokeVarIntern(
        cl, findOrCreate, invokeSymbolCreate(cl, "*global-start-service*"), startService);
    invokeVarIntern(cl, findOrCreate, invokeSymbolCreate(cl, "*global-class-loader*"), cl);

    // Launch the main window.
    invokeVarInvoke(cl, varRequire, (invokeSymbolCreate(cl, BASIC_TEST_WIN_NAMESPACE)));
    return true;
  }

  /**
   * Main entry point.
   *
   * @param args
   */
  public static final void main(final IStartService service, ClassLoader cl) throws Exception {

    final OctaneClojureScript contract = new OctaneClojureScript();
    contract.invokeContract(service, cl);
  }
} // End of the Class
