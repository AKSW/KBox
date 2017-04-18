package org.aksw.kbox.apple;

import java.net.URL;

/**
 * 
 * @author {@linkplain http://emarx.org}
 *
 */
public interface PathBinder {
	
	/**
	 * 
	 * @param url
	 * @param format
	 * @param version
	 * @return 
	 * @throws Exception
	 */
	public String urlToAbsolutePath(URL url, String format, String version) throws Exception;	
}
