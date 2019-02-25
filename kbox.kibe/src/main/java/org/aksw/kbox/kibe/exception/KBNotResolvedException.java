package org.aksw.kbox.kibe.exception;

public class KBNotResolvedException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8762611547404229890L;

	public KBNotResolvedException() {
		super("The knowledge base could not be resolved.");
	}
	
	public KBNotResolvedException(String message) {
		super(message);
	}
}
