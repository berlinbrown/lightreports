/**
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
 */

/**
 * Also see:
 * http://static.springframework.org/spring/docs/2.5.x/api/org/springframework/beans/factory/access/SingletonBeanFactoryLocator.html
 */
package com.light.clojure;

import clojure.lang.Namespace;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;

import com.light.contract.BasicContractHandler;
import com.light.contract.IContractHandler;
import com.light.contract.error.ContractError;
import com.light.contract.error.ContractException;
import com.light.contract.error.PreConditionError;

public class BasicTestWinMain extends BasicContractHandler {
		
	private static final String BEAN_FACTORY = "beanFactoryRef-testwin.xml";
	
	private static final String [] CLASSPATH_CONTEXTS    = { "conf/applicationContext-testwin.xml" };
	
	private static final String BASIC_TEST_WIN_GLOBALS   = "light.test.win.spring_globals";
	
	private static final String BASIC_TEST_WIN_NAMESPACE = "light.test.win.basic_test_window";
	
	/////////////////////////////////////////////////////////////////
	
	// REQUIRE /////
	/**
	 * 
	 */
	public Object require(Object precondInput) throws ContractError {
				
		if (precondInput == null) {
			throw new PreConditionError("Cannot perform square root on negative number"); 
		}
		return CONTRACT_TRUE;
	}
	// ENSURE /////
	
	/**
	 * Ensure that the contract post conditions are met.
	 */
	public Object ensure(Object postCondResult) throws ContractError {		
		return CONTRACT_IGNORE;
	}
	// INVOKE CONTRACT /////
	public Object invokeContract(Object precondInput) throws ContractError {
		
		//final ApplicationContext context = (ApplicationContext) precondInput;
		//////////////////////////////////
		// Init the clojure main library
		//////////////////////////////////
		final Symbol symbolClojureMain       = Symbol.create("clojure.main");
		final Namespace namespaceClojureMain = Namespace.findOrCreate(symbolClojureMain);		
		final Var varRequire                 = Var.intern(RT.CLOJURE_NS, Symbol.create("require"));
		
		// Setup clojure/main
		try {
			varRequire.invoke(symbolClojureMain);
			
			// Call require on our utility clojure code
			// Set the variable spring-context for use in the clojure script
			//Var.intern(Namespace.findOrCreate(Symbol.create(BASIC_TEST_WIN_GLOBALS)), Symbol.create("*spring-context*"), context);
			
			// Launch the main window.
			varRequire.invoke(Symbol.create(BASIC_TEST_WIN_NAMESPACE));
			
		} catch (Exception e) {
			throw new ContractException(e.getMessage());
		}		

		return CONTRACT_IGNORE;
	}
	
	/**
	 * Main entry point.
	 * @param args
	 */
	public static void main(final String [] args) throws Exception {

		//final BeanFactoryLocator bfl = SingletonBeanFactoryLocator.getInstance(BEAN_FACTORY);
		//final BeanFactoryReference bf = bfl.useBeanFactory("com.lightedit.clojure.LightApplicationContext");
		 
		// now use some bean from factory 
		//final BeanFactory beanFactoryContext = bf.getFactory();

		final IContractHandler contract = new BasicTestWinMain();
		contract.executeContract("");
	}
	
} // End of the Class
