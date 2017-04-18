package org.aksw.kbox.apple;

import java.net.URL;

/**
 * 
 * @author {@linkplain http://emarx.org}
 *
 */
public interface AppValidator {
	public boolean URLToAbsolutePath(URL url, String format, String version) throws Exception;
}
