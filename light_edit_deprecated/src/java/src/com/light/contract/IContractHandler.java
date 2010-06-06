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
package com.light.contract;

import com.light.contract.error.ContractError;

/**
 * The contract handler is a design by contract library to ensure reliable software.
 * Design by contract is a method of software construction where the contracts
 * must be met for the routine to work correctly.
 * 
 * Here is a non-technical metaphor to emphasize the benefits of design by contract:
 * 
 * The supplier must provide a certain product (obligation) and is entitled to expect that the client has paid its fee (benefit). 
 * The client must pay the fee (obligation) and is entitled to get the product (benefit).
 * Both parties must satisfy certain obligations, such as laws and regulations, applying to all contracts.
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
 * http://en.wikipedia.org/wiki/Design_by_contract
 * 
 * @version 1.0
 */
public interface IContractHandler {
	
	public static final Boolean CONTRACT_TRUE    = new Boolean(true);
	public static final Boolean CONTRACT_IGNORE  = new Boolean(true);
	public static final Boolean CONTRACT_FALSE   = new Boolean(true);
	
	/**
	 * The precondition can be null if the contract allows for that.
	 * 
	 * @param    precondInput -  Precondition Input Data, can be null.
	 * @return                   Post condition output
	 */
	public Object invokeContract(final Object precondInput) throws ContractError;
	
	/**
	 * Execute contract will invoke the #invokeContract method.  In the execute method, 
	 * check for the validity of the preconditions and the post conditions.
	 * 
	 * The precondition can be null.
	 * 
	 * @param    precondInput -  Precondition Input Data, can be null.
	 * @return                   Post condition output
	 */
	public Object executeContract(final Object precondInput) throws ContractError;
	
	/**
	 * Require that the preconditions are met.
	 */
	public Object require(final Object precondInput) throws ContractError;
	
	/**
	 * Ensure that the postconditions are met.
	 */
	public Object ensure(final Object precondInput) throws ContractError;
		

}
