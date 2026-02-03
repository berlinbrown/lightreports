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
package com.octane.contract;

/**
 * The contract handler is a design by contract library to ensure reliable software. Design by
 * contract is a method of software construction where the contracts must be met for the routine to
 * work correctly.
 *
 * <p>Here is a non-technical metaphor to emphasize the benefits of design by contract:
 *
 * <p>The supplier must provide a certain product (obligation) and is entitled to expect that the
 * client has paid its fee (benefit). The client must pay the fee (obligation) and is entitled to
 * get the product (benefit). Both parties must satisfy certain obligations, such as laws and
 * regulations, applying to all contracts.
 *
 * <pre><code>
 * Example Eiffel Design By Contract Idiom.
 *
 *   require
 *            count <= capacity
 *            not key.empty
 *   do invoke()
 *   ensure
 *            has (x)
 *            item (key) = x
 *            count = old count + 1
 *   end
 * </code></pre>
 *
 * For more on design by contract, see:
 *
 * <p>http://en.wikipedia.org/wiki/Design_by_contract
 *
 * <p>This version of a contract is a simple version if of IPDFContract Handler.
 *
 * @version 1.0
 * @author Berlin
 */
public interface IContract {

  /** Use contract ignore when implementing the contract handler. */
  public static final Boolean CONTRACT_IGNORE = new Boolean(true);

  public static final Boolean CONTRACT_TRUE = new Boolean(true);
  public static final Boolean CONTRACT_FALSE = new Boolean(false);

  /**
   * Execute contract will invoke the #invokeContract method. In the execute method, check for the
   * validity of the preconditions and the post conditions.
   *
   * <p>The precondition can be null.
   *
   * @param precondInput - Precondition Input Data, can be null.
   * @return Post condition output
   */
  public Object execute(final Object precondInput);

  /**
   * Require that the preconditions are met.
   *
   * @param precondInput Pre condition Input.
   * @return Result of require method call.
   */
  public Object require(final Object precondInput);
} // End of Interface //
