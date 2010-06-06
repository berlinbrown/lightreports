package com.light.network.proxy;

public class ProxyServerError extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String PROXY_ERR_MSG = "<Proxy Server>";
	
	public static final String ERROR = "ERROR";
	public static final String WARN  = "WARN";
	
	/**
	 * Constructs a new <code>ProxyServerError</code>.
	 */
	private ProxyServerError() {	
		super();
	}

	/**
	 * Constructs a new <code>JMRuntimeException</code>
	 * with the specified message.
	 *
	 * @param message the error message to give to the user.
	 */
	public ProxyServerError(final String level, final String message) {	
		super(level + ": " + message);
	}
	
	/**
	 * Constructs a new <code>JMRuntimeException</code>
	 * with the specified message.
	 *
	 * @param message the error message to give to the user.
	 */
	public ProxyServerError(final String message) {	
		super(PROXY_ERR_MSG + " " + message);
	}

}
