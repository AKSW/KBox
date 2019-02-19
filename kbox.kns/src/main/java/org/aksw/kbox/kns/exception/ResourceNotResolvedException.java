package org.aksw.kbox.kns.exception;

public class ResourceNotResolvedException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8762611547404229890L;

	public ResourceNotResolvedException() {
		super("The resource could not be resolved.");
	}
	
	public ResourceNotResolvedException(String message) {
		super(message);
	}
}
