package org.aksw.kbox.apple;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.aksw.kbox.InputStreamFactory;

/**
 * 
 * @author {@linkplain http://emarx.org}
 *
 */
public interface AppInstall {
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
	 * @param resources the URL to the resources that are going to be published.
	 * @param dest the URL where the resource is going to be published.
	 * @param format the format.
	 * @param version the version.
	 * 
	 * @throws Exception
	 */
	public void install(URL[] resource, URL dest, String format, String version) throws Exception;
	
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
	 * Install the given resources in a given URL.
	 * 
	 * @param resources the URL of the resources that are going to be published.
	 * @param dest the URL where the resource is going to be published.
	 * @param format the format.
	 * @param version the version.
	 * 
	 * @throws Exception
	 */
	public void install(URL[] resources, URL dest, String format, String version, InputStreamFactory isFactory) throws Exception;
	
	public void install(URL resource, File dest, InputStreamFactory isFactory) throws Exception;
	
	public void install(URL[] resource, File dest, InputStreamFactory isFactory) throws Exception;
	
	public void validate(URL url, String format, String version) throws Exception;
	
	public void register(URL url, String format, String version) throws Exception;
	
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
	public void install(InputStream source, URL dest, String format, String version) throws Exception;
	
	/**
	 * Install a given resource in a given URL.
	 * 
	 * @param sources the InputStreams of the resource that is going to be published.
	 * @param dest the URL where the resource is going to be published.
	 * @param format the format.
	 * @param version the version.
	 * 
	 * @throws Exception
	 */
	public void install(InputStream sources[], URL dest, String format, String version) throws Exception;
	
	public void install(InputStream source, File target) throws Exception;
	
	public void install(InputStream[] sources, File target) throws Exception;
}
