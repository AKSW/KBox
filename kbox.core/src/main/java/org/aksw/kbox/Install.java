package org.aksw.kbox;

import java.net.URL;

public interface Install {
	
	/**
	 * Install a given resource in a given URL.
	 * 
	 * @param resource the URL of the resource that is going to be published.
	 * @param dest the URL where the resource is going to be published.
	 * @throws Exception
	 */
	public void install(URL resource, URL dest) throws Exception;
}
