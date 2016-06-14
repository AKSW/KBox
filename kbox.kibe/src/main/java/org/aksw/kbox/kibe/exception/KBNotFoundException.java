package org.aksw.kbox.kibe.exception;

public class KBNotFoundException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8762611547404229890L;

	public KBNotFoundException() {
		super("The Knowledbase could not be found");
	}
	
	public KBNotFoundException(String error) {
		super(error);
	}
}
