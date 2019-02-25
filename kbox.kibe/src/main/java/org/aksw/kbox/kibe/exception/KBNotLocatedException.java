package org.aksw.kbox.kibe.exception;

public class KBNotLocatedException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8762611547404229890L;

	public KBNotLocatedException() {
		super("The knowledge base could not be located.");
	}
	
	public KBNotLocatedException(String message) {
		super(message);
	}
}
