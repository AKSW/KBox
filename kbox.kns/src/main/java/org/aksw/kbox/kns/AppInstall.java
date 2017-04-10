package org.aksw.kbox.kns;

import java.io.InputStream;
import java.net.URL;

import org.aksw.kbox.InputStreamFactory;
import org.aksw.kbox.Install;

public interface AppInstall extends Install {
	/**
	 * Install a given resource in a given URL.
	 * 
	 * @param resource the URL of the resource that is going to be published.
	 * @param dest the URL where the resource is going to be published.
	 * @param format the format.
	 * @param version the version.
	 * 
	 * @throws Exception
	 */
	public void install(URL resource, URL dest, String format, String version) throws Exception;
	
	/**
	 * Install a given resource in a given URL.
	 * 
	 * @param resource the URL of the resource that is going to be published.
	 * @param dest the URL where the resource is going to be published.
	 * @param format the format.
	 * @param version the version.
	 * 
	 * @throws Exception
	 */
	public void install(URL resource, URL dest, String format, String version, InputStreamFactory factory) throws Exception;
	
	/**
	 * Install a given resource in a given URL.
	 * 
	 * @param resource the InputStream of the resource that is going to be published.
	 * @param dest the URL where the resource is going to be published.
	 * @param format the format.
	 * @param version the version.
	 * 
	 * @throws Exception
	 */
	public void install(InputStream resource, URL dest, String format, String version) throws Exception;
}
