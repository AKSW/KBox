package org.aksw.kbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.InvalidPathException;

import org.aksw.kbox.utils.ArrayUtils;
import org.aksw.kbox.utils.ObjectUtils;
import org.apache.log4j.Logger;

/**
 * KBox is the core class of Knowledge Box project. It contains the main
 * functions to manipulate resources in your personal Knowledge Box.
 * 
 * @author {@linkplain http://emarx.org}
 *
 */
public class KBox extends ObjectUtils {

	private final static String KBOX_FOLDER = ".kbox";
	private final static String KBOX_RESOURCE_FOLDER = "KBOX_RESOURCE_FOLDER";
	private final static String KBOX_CONFIG_CONTEXT = "kbox";

	public final static String KBOX_CONFIG_FILE = ".config";
	public final static String KBOX_DIR = System.getProperty("user.home")
			+ File.separator + KBOX_FOLDER;
	
	private static final String CHK = ".chk";

	private final static Logger logger = Logger.getLogger(KBox.class);

	static {
		try {
			init();
		} catch (Exception e) {
			logger.error("Error initializing KBox.", e);
		}
	}

	protected static void init() throws Exception {
		File kBoxDir = new File(KBOX_DIR);
		if (!kBoxDir.exists()) {
			kBoxDir.mkdir();
			setResourceFolder(KBOX_DIR);
		}
	}

	private static CustomParams getParams() {
		CustomParams params = new CustomParams(KBOX_DIR + File.separator
				+ KBOX_CONFIG_FILE, KBOX_CONFIG_CONTEXT);
		return params;
	}

	/**
	 * Converts an URL to a local path.
	 * 
	 * @param url an URL.
	 */
	public static String urlToPath(URL url) {
		String urlPath = url.getPath();
		String host = url.getHost();
		String protocol = url.getProtocol();
		int port = url.getPort();
		String[] pathDirs = urlPath.split("/");
		String[] hostDirs = host.split("\\.");
		ArrayUtils.reverse(hostDirs);
		String path = protocol + File.separator;
		if (port != -1) {
			path += port + File.separator;
		}
		for (String hostDir : hostDirs) {
			path += hostDir + File.separator;
		}
		for (String pathDir : pathDirs) {
			pathDir = pathDir.replace(":", File.separator);
			path += pathDir + File.separator;
		}
		path = path.substring(0, path.length() - 1);
		return path;
	}
	
	/**
	 * Returns the local resource path of the give URL.
	 * 
	 * @param url the resource URL
	 * @return the URL of the local resource path.
	 */
	public static File locate(URL url) throws Exception {
		ResourceLocate locate = new ResourceLocate();
		return locate(url, locate);
	}
	
	/**
	 * Returns the local resource path given by the locate.
	 * 
	 * @param url the resource URL
	 * @return the local path of the resource.
	 */
	public static File locate(URL url, Locate locate) throws Exception  {
		return locate.locate(url);
	}

	/**
	 * Converts an URL to an absolute local path.
	 * 
	 * @param url an URL.
	 */
	public static String urlToAbsolutePath(URL url) {
		String resourceFolder = getResourceFolder();
		File resource = new File(resourceFolder + File.separator
				+ urlToPath(url));
		return resource.getAbsolutePath();
	}

	/**
	 * Create a KBox directory representing the {@link URL}.
	 * 
	 * @param the KBox directory representing the given {@link URL}.
	 */
	public static File newDir(URL url) {
		File resource = new File(urlToAbsolutePath(url));
		resource.mkdirs();
		return resource;
	}

	/**
	 * Replace the old resource folder by the given one.
	 * 
	 * @param resourceFolderPath - a full path folder where the 
	 *        resources were going to be saved. 
	 *        The default resource folder is "user/.kbox".
	 * 
	 */
	public static void setResourceFolder(String resourceFolderPath) {
		File resourceDir = new File(resourceFolderPath);
		if (!(resourceDir.exists() || resourceDir.isDirectory())) {
			throw new InvalidPathException(resourceFolderPath,
					"The given path does not exist or is not a directory.");
		}
		CustomParams params = getParams();
		params.setProperty(KBOX_RESOURCE_FOLDER, resourceFolderPath);
	}

	/**
	 * Get the default resource folder.
	 * 
	 * @return the full path of the default resource folder.
	 */
	public static String getResourceFolder() {
		CustomParams params = getParams();
		String resourceFolderPath = params.getProperty(KBOX_RESOURCE_FOLDER,
				KBOX_DIR);
		return resourceFolderPath;
	}

	/**
	 * Get a local mirror of the given resource. 
	 * This method will not try to install the resource and will return null in
	 * case it does not exists.
	 * 
	 * @param url the remote {@link URL} of the resource to be retrieved.
	 * 
	 * @return a file pointing to a local KBox resource.
	 * 
	 * @throws Exception if the resource can not be located or some error occurs
	 *             during the KBox resource lookup.
	 */
	public static File getResource(URL url) throws Exception {
		return getResource(url, false);
	}

	/**
	 * Get a local mirror of the remote resource or null 
	 * if does not exist. 
	 * If the flag install is set to true, returns a local copy 
	 * of the resource if it already exists or create it otherwise.
	 * 
	 * @param url the remote {@link URL} of the resource to be retrieved.
	 * @param install specify if the resource should be installed in case in does
	 *         no exist (true) or not (false).
	 * 
	 * @return a file pointing to a local resource.
	 * 
	 * @throws Exception if the resource can not be located or some error 
	 * 			occurs while creating the local mirror.
	 */
	public static File getResource(URL url, boolean install) throws Exception {
		ResourceLocate resourceLocate = new ResourceLocate();
		ResourceInstall resourceInstall = new ResourceInstall();
		return getResource(url, resourceLocate, resourceInstall, install);
	}

	/**
	 * Get a local mirror of the remote resource or null 
	 * if does not exist. 
	 * If the flag install is set to true, returns a local copy 
	 * of the resource if it already exists or create it otherwise.
	 * 
	 * @param url the remote URL of the resource to be retrieved.
	 * @param install specify if the resource should be installed (true) or not
	 *            (false).
	 * @param method the method that will be used to install the resource in case
	 *        it is not installed and install install param is set true.
	 * 
	 * @return a {@link File} pointing to a local copy of the resource or {@link null} otherwise.
	 * 
	 * @throws Exception if the resource can not be located or some error occurs
	 *             while creating the local mirror.
	 */
	public static File getResource(URL url, Install method, boolean install)
			throws Exception {
		ResourceLocate resourceLocate = new ResourceLocate();
		return getResource(url, resourceLocate, method, install);
	}
	
	/**
	 * Get a local mirror of the remote resource or null 
	 * if does not exist. 
	 * If the flag install is set to true, returns a local copy 
	 * of the resource if it already exists or create it otherwise.
	 * 
	 * @param url the remote {@link URL} of the resource to be retrieved.
	 * @param install specify if the resource should be installed (true) or not
	 *            (false).
	 * @param method the method that will be used to install the resource in case
	 *        it is not installed and install install param is set true.
	 * 
	 * @return a {@link File} pointing to a local copy of the resource or {@link null} otherwise.
	 * 
	 * @throws Exception if the resource can not be located or some error occurs
	 *             while creating the local mirror.
	 */
	public static File getResource(URL url, Locate locateMethod, Install installMethod, boolean install)
			throws Exception {
		File resource = locateMethod.locate(url);
		if (resource != null) {
			return resource;
		}
		install(url, url, installMethod);
		return locateMethod.locate(url);
	}

	/**
	 * Get a local mirror of the remote resource or {@link null} if it does 
	 * not exist.
	 * 
	 * @param url the remote {@link URL} of the resource to be retrieved.
	 * 
	 * @return an {@link InputStream} pointing to a local copy of the
	 *         resource.
	 *         
	 * @throws Exception if the resource can not be located or some error occurs
	 *             while creating the local mirror.
	 */
	public static InputStream getResourceAsStream(URL url) throws Exception {
		return getResourceAsStream(url, false);
	}

	/**
	 * Get a local representation of the remote resource or null 
	 * if does not exist. 
	 * If the flag install is set to true, returns a local copy 
	 * of the resource if it already exists or create it otherwise.
	 * 
	 * This method will not try to install the resource and will return null in
	 * case it does not exists.
	 * 
	 * @param url the remote {@link URL} of the resource to be retrieved.
	 * @param install specify if the resource should be installed in case in does
	 *        no exist (true) or not (false).
	 * 
	 * @return an {@link InputStream} pointing to a local copy of the
	 *         resource.
	 *         
	 * @throws Exception if the resource can not be located or some error occurs
	 *         while copying the resource.
	 */
	public static InputStream getResourceAsStream(URL url, boolean install)
			throws Exception {
		return new FileInputStream(getResource(url, install));
	}

	/**
	 * Publish the dereferenced file in the given path on your Knowledge Box
	 * (KBox) resource folder. This function allows KBox to serve files to
	 * applications, acting as proxy to the published file. The file that is
	 * published in a give URL u will be located when the client execute the
	 * function KBox.getResource(u).
	 * 
	 * @param source the {@link URL} of the file that is going to be published at the
	 *            given {@link URL}.
	 * @param dest the {@link URL} were the dereferenced file is going to be
	 *            published.
	 * 
	 * @throws Exception if the resource does not exist or can not be copied 
	 *         or some error occurs during the resource publication.
	 */
	public static void install(URL source, URL dest) throws Exception {
		try (InputStream is = source.openStream()) {
			install(is, dest);
		}
	}

	/**
	 * Publish the dereferenced file in a given {@link URL} in your corresponding local
	 * Knowledge Box (KBox) resource folder. This function allows KBox to serve
	 * files to applications, acting as proxy to the published file. The file
	 * that is installed in a give {@link URL} u will be located when the client
	 * executes the function KBox.getResource(u).
	 * 
	 * @param url the {@link URL} of the resource that is going to be published at the
	 *            given {@link URL}.
	 * 
	 * @throws Exception if the resource does not exist or can not be copied or some
	 *             error occurs during the resource publication.
	 */
	public static void install(URL url) throws Exception {
		try (InputStream is = url.openStream()) {
			install(is, url);
		}
	}

	/**
	 * Publish the given input stream in the given {@link URL} on your local Knowledge
	 * Box (KBox) resource folder. This function allows KBox to serve files to
	 * applications, acting as proxy to the published file. The file that is
	 * installed in a give URL u will be located when the client execute the
	 * function KBox.getResource(u).
	 * 
	 * @param source the {@link InputStream} that is going to be published in 
	 *        the given {@link URL}.
	 * @param dest the {@link URL} were the file is going to be published. 
	 * 
	 * @throws Exception if the resource does not exist or can not be copied or 
	 *         some error occurs during the resource publication.
	 */
	public static void install(InputStream source, URL dest)
			throws Exception {
		ResourceInstall resourceInstall = new ResourceInstall();
		install(source, dest, resourceInstall);
	}

	/**
	 * Creates a mirror for the given file in a given {@link URL}. This function allows
	 * KBox to serve files to applications, acting as proxy to the mirrored
	 * file. The file that is published in a give {@link URL} u will be located when the
	 * client execute the function KBox.getResource(u).
	 * 
	 * @param source the {@link URL} of the file that is going to be published at the
	 *        given {@link URL}.
	 * @param dest the {@link URL} where the file is going to be published.
	 * @param install a customized method for installation.
	 * 
	 * @throws Exception if the resource does not exist or can not be copied or some
	 *             error occurs during the resource publication.
	 */
	public static void install(URL source, URL dest, Install install)
			throws Exception {
		install.install(source, dest);
	}

	/**
	 * Publish a given file in a given {@link URL} local directory. This function allows
	 * KBox to serve files to applications, acting as proxy to the published
	 * file. The file that is published in a give URL u will be located when the
	 * client execute the function KBox.getResource(u).
	 * 
	 * @param source the {@link InputStream} of the file that is going to be published at
	 *            the given {@link URL}.
	 * @param dest the {@link URL} where the file is going to be published.
	 * @param install the customized method for installation.
	 * 
	 * @throws Exception if the resource does not exist or can not be copied or some
	 *             error occurs during the resource publication.
	 */
	public static void install(InputStream source, URL dest, Install install)
			throws Exception {
		install.install(source, dest);
	}

	/**
	 * Install a given ZIP file from a given {@link URL}.
	 * 
	 * @param source the source {@link URL} containing the ZIP file to be installed.
	 * @param dest the {@link URL} whereas the source will be installed.
	 * 
	 * @throws Exception if an error occurs during the installation.
	 */
	public static void installFromZip(URL source, URL dest) throws Exception {
		ZipInstall zipInstall = new ZipInstall();
		install(source, dest, zipInstall);
	}

	/**
	 * Install a given ZIP file from a given {@link URL}.
	 * 
	 * @param source the source {@link URL} containing the ZIP file to be installed.
	 * 
	 * @throws Exception if an error occurs during the installation.
	 */
	public static void installFromZip(URL source) throws Exception {
		installFromZip(source, source);
	}
	
	public static void validate(File resource) throws Exception {
		validate(resource.getAbsolutePath());
	}
	
	public static void validate(String resourcePath) throws Exception {
		String canonicalPath = getCanonicalPath(resourcePath);
		File file = new File(canonicalPath + CHK);
		file.createNewFile();
	}
	
	public static boolean isValid(String resourcePath) throws Exception {
		String canonicalPath = getCanonicalPath(resourcePath);
		File file = new File(canonicalPath + CHK);
		return file.exists();
	}
	
	private static String getCanonicalPath(String path) throws Exception {
		return getCanonicalPath(new File(path));
	}
	
	private static String getCanonicalPath(File path) throws Exception {
		return path.getCanonicalPath();
	}
}