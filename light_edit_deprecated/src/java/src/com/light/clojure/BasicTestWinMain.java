/**
 * Also see:
 * http://static.springframework.org/spring/docs/2.5.x/api/org/springframework/beans/factory/access/SingletonBeanFactoryLocator.html
 */
package com.light.clojure;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.beans.factory.access.SingletonBeanFactoryLocator;
import org.springframework.context.ApplicationContext;

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
		final ApplicationContext context = (ApplicationContext) precondInput;
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
			Var.intern(Namespace.findOrCreate(Symbol.create(BASIC_TEST_WIN_GLOBALS)), Symbol.create("*spring-context*"), context);
			
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

		final BeanFactoryLocator bfl = SingletonBeanFactoryLocator.getInstance(BEAN_FACTORY);
		final BeanFactoryReference bf = bfl.useBeanFactory("com.lightedit.clojure.LightApplicationContext");
		 
		// now use some bean from factory 
		final BeanFactory beanFactoryContext = bf.getFactory();

		final IContractHandler contract = new BasicTestWinMain();
		contract.executeContract(beanFactoryContext);
	}
	
} // End of the Class
