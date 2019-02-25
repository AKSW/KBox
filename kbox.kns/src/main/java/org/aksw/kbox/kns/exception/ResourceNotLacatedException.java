package org.aksw.kbox.kns.exception;

public class ResourceNotLacatedException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8762611547404229890L;

	public ResourceNotLacatedException() {
		super("The resource could not be located.");
	}
	
	public ResourceNotLacatedException(String message) {
		super(message);
	}
}
