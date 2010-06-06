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
package com.light.contract;

import com.light.contract.error.ContractError;
import com.light.contract.error.ContractException;
import com.light.contract.error.PostConditionError;
import com.light.contract.error.PreConditionError;

/**
 * The contract handler is a design by contract library to ensure reliable software.
 * Design by contract is a method of software construction where the contracts
 * must be met for the routine to work correctly.
 * 
 * For more on design by contract, see:
 * 
 * http://en.wikipedia.org/wiki/Design_by_contract
 */
public abstract class BasicContractHandler implements IContractHandler {

	/**
	 * Main contract entry point.  Invoke the <code>invokeContract</code> method.
	 * 
	 * @param	precondInput		Boolean object, are the precondition resources available
	 */
	public Object executeContract(final Object precondInput) throws ContractError {					
		try {
			// (1) Check the input precondition.
			require(precondInput);
			
			// (2) Invoke the contract and check the post conditions.
			final Object postCondition = this.invokeContract(precondInput);
			
			// (3) Ensure that the post conditions are met.
			ensure(postCondition);
			
			return postCondition;			
		} catch (Exception generalError) {
			throw new ContractException("Object Error while invoking contract routine.  Error => " + generalError.getMessage());
		} // End of the Try - Catch
					
	} // End of the method
	
	/**
	 * Require that the preconditions are met.
	 */
	public Object require(final Object precondInput) throws ContractError {
	
		// Perform initial precondition test
		if (precondInput == null) {
			throw new PreConditionError();
		}
		
		if (!(precondInput instanceof Boolean)) {
			throw new PreConditionError("<BasicContractHandler> Precondition Input should have Boolean type");
		}
		
		// Perform full precondition test
		final Boolean hasValidCondition = (Boolean) precondInput;
		if (!hasValidCondition.booleanValue()) {
			throw new PreConditionError();
		} // end of the if
		return CONTRACT_TRUE;
	}
	
	/**
	 * Ensure that the postconditions are met.  This differs a little from the
	 * other methods, the input is actually a POST condition value returned from invokeContract.
	 */
	public Object ensure(final Object postConditionInput) throws ContractError {
		if (postConditionInput == null) {
			throw new PostConditionError();
		}
		
		if (!(postConditionInput instanceof Boolean)) {
			throw new PreConditionError("<BasicContractHandler> Precondition Input should have Boolean type");
		}
		
		final Boolean hasValidCondition = (Boolean) postConditionInput;
		if (!hasValidCondition.booleanValue()) {
			throw new PostConditionError();
		}
		return CONTRACT_TRUE;
	}	
}