package org.aksw.kbox;

import java.io.File;
import java.net.URL;

public interface Locate {
	
	/**
	 * Return the local File of the resource given by the URL or null if it
	 * does not exist.
	 * 
	 * @param url the URL of the resource
	 * @return the absolute path to the local file representing by the given url or null
	 *         if it does not exist
	 *         
	 * @throws Exception if something goes wrong during the locating process.
	 */
	File locate(URL url) throws Exception;
	
	/**
	 * Return the absolute path to the resource given by the URL or null if it
	 * does not exist.
	 * 
	 * @param url the URL of the resource
	 * @return the absolute path to the local file representing by the given url or null
	 *         if it does not exist
	 *         
	 * @throws Exception if something goes wrong during the locating process.
	 */
	String locate(String url) throws Exception;
}
