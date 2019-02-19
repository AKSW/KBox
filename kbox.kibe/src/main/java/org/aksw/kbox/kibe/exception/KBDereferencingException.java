package org.aksw.kbox.kibe.exception;

public class KBDereferencingException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8762611547404229890L;

	public KBDereferencingException() {
		super("The knowledge base could not be dereferenced.");
	}
	
	public KBDereferencingException(String kb) {
		super("The knowledge base " + kb + " could not be dereferenced.");
	}
	
	public KBDereferencingException(String kb, Exception e) {
		super("The knowledge base " + kb + " could not be dereferenced.", e);
	}
}
