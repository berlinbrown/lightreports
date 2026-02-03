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
package com.octane.test;

import com.octane.util.junit.framework.TestCase;

/**
 * @author Berlin
 * @version $Revision: 1.0 $
 */
public abstract class BasicTestCase extends TestCase {

  private long testStartT;
  private long testEndT;
  private long procTime;

  public static final int IDX_NULL = 0;
  public static final int IDX_EMPTY = 1;
  public static final int IDX_WHITESPACE = 2;
  public static final int IDX_INVALID = 3;

  private static final Runtime runtime = Runtime.getRuntime();

  private long[] initMemory = new long[2];
  private long[] endMemory = new long[2];
  private long[] diffMemory = new long[2];

  private static int testCount = 0;

  /**
   * Sets up the test fixture.
   *
   * <p>Called before every test case method.
   */
  protected void setUp() throws Exception {
    super.setUp();
    // Run garbage collection before test begins
    runtime.gc();
    try {
      Thread.sleep(40);
    } catch (InterruptedException e) {
      ;
    }

    this.loadOptimisticTestObjects();
    this.loadPessimisticTestObjects();
    testStartT = System.currentTimeMillis();

    initMemory[0] = runtime.freeMemory();
    initMemory[1] = runtime.maxMemory();
  }

  /**
   * Implementation Routine toObjectString.
   *
   * @return String
   */
  protected String toObjectString() {
    return this.getClass().getName() + "@" + Integer.toHexString(this.hashCode());
  }

  /**
   * Tears down the test fixture.
   *
   * <p>Called after every test case method.
   */
  protected void tearDown() throws Exception {
    super.tearDown();
    testEndT = System.currentTimeMillis();
    procTime = testEndT - testStartT;
    System.out.println("    @INFO: Processing test time => " + procTime);

    endMemory[0] = runtime.freeMemory();
    endMemory[1] = runtime.maxMemory();

    diffMemory[0] = initMemory[0] - endMemory[0];
    diffMemory[1] = initMemory[1] - endMemory[1];

    final double[] diffMemoryD = new double[2];
    diffMemoryD[0] = diffMemory[0] / 1024.0;
    diffMemoryD[1] = diffMemory[1] / 1024.0;

    final double maxMem = runtime.maxMemory() / 1024.0;

    System.out.println(
        "    @INFO: Free Memory Diff => " + diffMemory[0] + " / " + diffMemoryD[0] + " kb");
    System.out.println(
        "    @INFO: Max Memory Diff => " + diffMemory[1] + " / " + diffMemoryD[1] + " kb");
    System.out.println("    @INFO: Max Memory => " + maxMem + " kb");
    System.out.println(
        "    @INFO: -- End of Test : " + testCount + " -- objid: [" + this.toObjectString() + "]");

    // Run garbage collection after test
    runtime.gc();
    testCount++;
  }

  /** Override this method to load pessimistic test mock objects. */
  protected abstract void loadPessimisticTestObjects();

  /** Override this method to load simple test data. */
  protected abstract void loadOptimisticTestObjects();
} // End of Class //
