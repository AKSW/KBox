package org.aksw.kbox.apple;

import java.net.URL;

/**
 * 
 * @author {@linkplain http://emarx.org}
 *
 */
public interface AppLocator {
	public String URLToAbsolutePath(URL url, String format, String version) throws Exception;	
}
