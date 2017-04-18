package org.askw.kbox.kns.exception;

public class ResourceDereferencingException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8762611547404229890L;

	public ResourceDereferencingException() {
		super("The resource could not be resolved.");
	}
	
	public ResourceDereferencingException(String resource) {
		super("The resource " + resource + " could not be derecerenced.");
	}
	
	public ResourceDereferencingException(String resource, Exception e) {
		super("The resource " + resource + " could not be derecerenced.", e);
	}
}
