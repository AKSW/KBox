package org.aksw.kbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;


/**
 * RBox is the core class of Resource Box project.
 * It contains the main functions to manipulate resources in your personal 
 * Resource Box. 
 * 
 * @author http://emarx.org
 *
 */
public class KBox {
		
	private final static String KBOX_FOLDER = ".kbox";
	private final static String KBOX_RESOURCE_FOLDER = "RBOX_RESOURCE_FOLDER";
	private final static String KBOX_CONFIG_CONTEXT = "kbox";
	
	public final static String KBOX_CONFIG_FILE = ".config";
	public final static String KBOX_DIR = System.getProperty("user.home") + File.separator + KBOX_FOLDER;
	
	private static String cachedResourceFolderPath = null;
	
	static {
		init();
	}
	
	protected static void init() {
		File kBoxDir = new File(KBOX_DIR);
		if(!kBoxDir.exists()) {
			kBoxDir.mkdir();
			setResourceFolder(KBOX_DIR);
		}
	}
	
	private static CustomParams getParams() {
		CustomParams params = new CustomParams(KBOX_DIR + File.separator + KBOX_CONFIG_FILE, 
				KBOX_CONFIG_CONTEXT);
		return params;
	}
	
	/**
	 * Converts an URL to a local path.
	 * 
	 * @param url an URL.
	 */
	public static String URLToPath(URL url) {
		String urlPath = url.getPath();
		String host = url.getHost();
		String protocol = url.getProtocol();
		int port = url.getPort();
		String[] pathDirs = urlPath.split("/");
		String[] hostDirs = host.split("\\.");
		ArrayUtils.reverse(hostDirs);
		String path = protocol + File.separator;
		if(port != -1) {
			path += port + File.separator;
		}
		for(String hostDir : hostDirs) {
			path += hostDir + File.separator;
		}
		for(String pathDir : pathDirs) {
			path += pathDir + File.separator;
		}
		path = path.substring(0, path.length()-2);
		return path;
	}
	
	/**
	 * Converts an URL to an absolute local path.
	 * 
	 * @param url an URL.
	 */
	public static String URLToAbsolutePath(URL url) {
		String resourceFolder = getResourceFolder();
		File resource = new File(resourceFolder + File.separator + URLToPath(url));
		return resource.getAbsolutePath();
	}
	
	
	/**
	 * Create a local directory representing the url.
	 * 
	 * @param the local directory representing the given URL.
	 */
	public static File newDir(URL url) {
		File resource = new File(URLToAbsolutePath(url));
		resource.mkdirs();
		return resource;
	}
	
	/**
	 * Replace the old resource folder by the given one.
	 * 
	 * @param resourceFolderPath - a full path folder where the resources were going to be saved.
	 * The default resource folder is "user/.rbox".
	 */
	public static void setResourceFolder(String resourceFolderPath) {
		CustomParams params = getParams();
		File resourceFolder = new File(resourceFolderPath);
		if(!resourceFolder.exists()) {
			resourceFolder.mkdir();
		}
		params.setProperty(KBOX_RESOURCE_FOLDER, resourceFolderPath);
		cachedResourceFolderPath = resourceFolderPath;
	}
	
	/**
	 * Get the default resource folder.
	 * 
	 * @return the full path of the default resource folder
	 */
	public static String getResourceFolder() {
		if(cachedResourceFolderPath != null) {
			return cachedResourceFolderPath;
		}
		CustomParams params = getParams();
		cachedResourceFolderPath = params.getProperty(KBOX_RESOURCE_FOLDER, KBOX_DIR);
		return cachedResourceFolderPath;
	}
	
	/**
	 * Get a local representation of the remote resource.
	 * If the representation of the resource already exists,
	 * return it, otherwise create a local representation and 
	 * return it.
	 * 
	 * This method will not try to install the resource and will return null in 
	 * case it does not exists.
	 * 
	 * @param url - the remote URL of the resource to be retrieved.
	 * @return a file pointing to a local materialization of the resource.
	 * @throws Exception if the resource can not be located or some error occurs during
	 * the local resource materialization.
	 */
	public static File getResource(URL url) throws Exception {
		return getResource(url, false);
	}
	
	/**
	 * Get a local representation of the remote resource.
	 * If the representation of the resource already exists,
	 * return it, otherwise create a local representation and 
	 * return it.
	 * 
	 * @param url - the remote URL of the resource to be retrieved.
	 * @param install - specify if the resource should be installed (true) or not (false).
	 * @return a file pointing to a local materialization of the resource.
	 * @throws Exception if the resource can not be located or some error occurs during
	 * the local resource materialization.
	 */
	public static File getResource(URL url, boolean install) throws Exception {
		File resource = new File(URLToAbsolutePath(url));
		if(!resource.exists() && !install) {
			return null;
		} else if(resource.exists() && !install) {
			return resource;
		}
		install(url, url.openStream());		
		return resource;
	}
	
	/**
	 * Get a local representation of the remote resource.
	 * If the representation of the resource already exists,
	 * return it, otherwise create a local representation and 
	 * return it.
	 * 
	 * @param url - the remote URL of the resource to be retrieved.
	 * @return an - InputStream pointing to a local materialization of the resource.
	 * @throws Exception if the resource can not be located or some error occurs during
	 * the local resource materialization.
	 */
	public static InputStream getResourceAsStream(URL url) throws Exception {
		return getResourceAsStream(url, false);
	}
	
	/**
	 * Get a local representation of the remote resource.
	 * If the representation of the resource already exists,
	 * return it, otherwise create a local representation and 
	 * return it.
	 * 
	 * This method will not try to install the resource and will return null in 
	 * case it does not exists.
	 * 
	 * @param url - the remote URL of the resource to be retrieved.
	 * @param install - specify if the resource should be installed (true) or not (false).
	 * @return an InputStream pointing to a local materialization of the resource.
	 * @throws Exception if the resource can not be located or some error occurs during
	 * the local resource materialization.
	 */
	public static InputStream getResourceAsStream(URL url, boolean install) throws Exception {
		return new FileInputStream(getResource(url, install));
	}
	
	
	/**
	 * Publish a given file in a given URL local directory.
	 * This function allows RBox to serve files to applications, acting as proxy to the publised file.
	 * The file that is published in a give URL u will be located when the client execute 
	 * the function RBox.getResource(u). 
	 * 
	 * 
	 * @param url - the URL were the file is going to be published.
	 * @param file - the URL of the file that is going to be published at the given URL.
	 * @throws Exception if the resource does not exist or can not be copied or some error 
	 * occurs during the resource publication.
	 */
	public static void install(URL url, URL file) throws Exception {
		install(url, file.openStream());
	}
	
	/**
	 * Publish a given file in a given URL local directory.
	 * This function allows RBox to serve files to applications, acting as proxy to the published file.
	 * The file that is published in a give URL u will be located when the client execute 
	 * the function RBox.getResource(u). 
	 * 
	 * @param url - the URL were the file is going to be published.
	 * @param inputStream - the inputStream that is going to be published in the given URL.
	 * @throws Exception if the resource does not exist or can not be copied or some error 
	 * occurs during the resource publication.
	 */
	public static void install(URL url, InputStream inputStream) throws Exception {
		File resource = new File(URLToAbsolutePath(url));
		File resourceDir = resource.getParentFile();
		resourceDir.mkdirs();
		resource.createNewFile();
		ReadableByteChannel rbc = Channels.newChannel(inputStream);
		try(FileOutputStream fos = new FileOutputStream(resource);) {
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		}
	}
}