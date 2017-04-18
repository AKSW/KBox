package org.aksw.kbox.apple;

import java.io.File;
import java.net.URL;

import org.aksw.kbox.KBox;

/**
 * 
 * @author {@linkplain http://emarx.org}
 *
 */
public class DefaultAppLocator implements AppLocator {
	public String URLToAbsolutePath(URL url, String format, String version) throws Exception {
		return KBox.getResourceFolder()
				+ File.separator
				+ format
				+ File.separator
				+ version
				+ File.separator
				+ KBox.URLToPath(url);
	}
}
