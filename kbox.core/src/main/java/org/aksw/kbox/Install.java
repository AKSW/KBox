package org.aksw.kbox;

import java.io.InputStream;
import java.net.URL;

/**
 * Install interface.
 * 
 * @author {@linkplain http://emarx.org}
 *
 */
public interface Install {
	
	/**
	 * Install a given resource in a given URL.
	 * 
	 * @param resource the URL of the resource that is going to be published.
	 * @param dest the URL where the resource is going to be published.
	 * @throws Exception
	 */
	public void install(URL resource, URL dest) throws Exception;
	
	/**
	 * Install a given resource in a given URL.
	 * 
	 * @param resource the InputStream of the resource that is going to be published.
	 * @param dest the URL where the resource is going to be published.
	 * @throws Exception
	 */
	public void install(InputStream resource, URL dest) throws Exception;
	
	public void validate(URL url) throws Exception;
}
