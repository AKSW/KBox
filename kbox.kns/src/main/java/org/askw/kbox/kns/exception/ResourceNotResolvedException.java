package org.askw.kbox.kns.exception;

public class ResourceNotResolvedException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8762611547404229890L;

	public ResourceNotResolvedException() {
		super("The knowledge base could not be found.");
	}
	
	public ResourceNotResolvedException(String kb) {
		super("The resource could not be found: " + kb);
	}
}
