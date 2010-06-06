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
 * Main Description: Light is a simple text editor in clojure
 * Contact: Berlin Brown <berlin dot brown at gmail.com>
 *********************************************************************/
package com.light.test.contract;

import junit.framework.TestCase;

import com.light.contract.BasicContractHandler;
import com.light.contract.IContractHandler;
import com.light.contract.error.ContractError;
import com.light.contract.error.PostConditionError;
import com.light.contract.error.PreConditionError;

/**
 * Contract Test Case.
 * 
 * @version 1.0
 */
public final class SimpleContractTest extends TestCase {

    /**
     * Sets up the test fixture.
     *
     * Called before every test case method.
     */
    protected void setUp() {

    }

    /**
     * Tears down the test fixture.
     *
     * Called after every test case method.
     */
    protected void tearDown() {
    }
      
    public void testMyContractFailPreCond() {
            	    	
    	final BasicContract contract = new BasicContract();    	    	    	
    	contract.executeContract(new Boolean(true));
    	
    } // End of Test
    
    /**
     * Test square root, optimistic case.     
     */
    public void testContractCallback_SquareRoot1() {
    	
    	final double val = 9.0; 
    	
    	final IContractHandler contract = (new BasicContractHandler () {
			public Object invokeContract(Object precondInput) throws ContractError {
				Math.sqrt(val);
				return CONTRACT_TRUE;
			}
    	});
    	contract.executeContract(IContractHandler.CONTRACT_TRUE);
    	
    	// Try again
    	
    	final double val2 = -1;     	
    	final IContractHandler contract2 = (new BasicContractHandler () {
			public Object invokeContract(Object precondInput) throws ContractError {
				final double res = Math.sqrt(val2);
				System.out.println(res);
				return CONTRACT_TRUE;
			}
    	});
    	contract2.executeContract(IContractHandler.CONTRACT_TRUE);
    }
    
    public void testContractCallback_SquareRoot2() {
    
    	final double val = -1;
    	
    	//////////////////////////
    	// Build the contract
    	//////////////////////////
    	final IContractHandler contract = (new BasicContractHandler () {
    		
    		public Object require(Object precondInput) throws ContractError {
    			double input = ((Double) precondInput).doubleValue();
    			if (input < 0) {
    				throw new PreConditionError("Cannot perform square root on negative number"); 
    			}
    			return CONTRACT_TRUE;
    		}
    	
    		public Object ensure(Object postCondResult) throws ContractError {
    			// Ensure that the post conditions are met
    			double sqrtRes = ((Double) postCondResult).doubleValue();
    			double check   = sqrtRes * sqrtRes;
    			
    			// Check that the doubling the input will return the original value.    			
    			if (check != val) {
    				throw new PostConditionError("Output value is not valid, value => " + check);
    			}
    			return CONTRACT_TRUE;
    		}
    		
			public Object invokeContract(Object precondInput) throws ContractError {
				return new Double(Math.sqrt(val));				
			}
    	});
    	//////////////////////////
    	// Execute the contract
    	//////////////////////////
    	contract.executeContract(new Double(val));
    	
    }
        
    public void testContractCallback_SquareRoot3() {
        
    	final double val = 9;
    	
    	///////////////////////////////////////////////////
    	// Build the contract
    	///////////////////////////////////////////////////
    	final IContractHandler contract = (new BasicContractHandler () {
    		
    		// REQUIRE /////
    		public Object require(Object precondInput) throws ContractError {
    			double input = ((Double) precondInput).doubleValue();
    			if (input < 0) {
    				throw new PreConditionError("Cannot perform square root on negative number"); 
    			}
    			return CONTRACT_TRUE;
    		}
    		// ENSURE /////
    		public Object ensure(Object postCondResult) throws ContractError {
    			// Ensure that the post conditions are met
    			double sqrtRes = ((Double) postCondResult).doubleValue();
    			
    			// NOTE:! this will generate an error.
    			double check   = (sqrtRes * sqrtRes) + 1;
    			
    			// Check that the doubling the input will return the original value.    			
    			if (check != val) {
    				throw new PostConditionError("Output value is not valid, value => " + check);
    			}
    			return CONTRACT_TRUE;
    		}
    		// INVOKE CONTRACT /////
			public Object invokeContract(Object precondInput) throws ContractError {
				return new Double(Math.sqrt(val));				
			}
    	});
    	///////////////////////////////////////////////////
    	// Execute the contract
    	///////////////////////////////////////////////////
    	final Object res = contract.executeContract(new Double(val));    	    
    }
    
    /////////////////////////////////////////////////////////////////
    // Utilities
    /////////////////////////////////////////////////////////////////
    
    /**
     * Example implementation of the Contract Handler.
     */
    private static final class BasicContract extends BasicContractHandler {    	
		public Object invokeContract(Object precondInput) throws ContractError {
			return CONTRACT_TRUE;
		} // End of the method    	
    }
    
} // End of Test Suite Class
