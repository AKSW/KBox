package org.aksw.kbox.fusca.exception;

public class ServerStartException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8495102644748797751L;

	public ServerStartException(String message, Exception e) {
		super(message, e);
	}
}
