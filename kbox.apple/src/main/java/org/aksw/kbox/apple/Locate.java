package org.aksw.kbox.apple;

import java.io.File;
import java.net.URL;

/**
 * 
 * @author {@linkplain http://emarx.org}
 *
 */
public interface Locate {
	/**
	 * Install a given resource in a given URL.
	 * 
	 * @param resource the URL of the resource.
	 * @param dest the URL where the resource is going to be published.
	 * @param format the format.
	 * @param version the version.
	 * 
	 * @throws Exception if something goes wrong during the locating process.
	 */
	public String locate(String resource, String format, String version) throws Exception;
	
	/**
	 * Return the absolute path to the resource given by the URL or null if it
	 * does not exist.
	 * 
	 * @param url the URL of the resource
	 * @param format the format.
	 * @param version the version.
	 * 
	 * @return the absolute path to the local file representing by the given url or null
	 *         if it does not exist
	 *         
	 * @throws Exception if something goes wrong during the locating process.
	 */	
	public File locate(URL resource, String format, String version) throws Exception;
	
	/**
	 * Checks if a given resource has a valid installation.
	 * 
	 * @param url the URL of the resource
	 * @param format the format.
	 * @param version the version.
	 * 
	 * @return true if the resource has a valid installation and false otherwise.
	 * 
	 * @throws Exception if something goes wrong during the validating process.
	 */
	public boolean isValid(URL url, String format, String version) throws Exception;

}
