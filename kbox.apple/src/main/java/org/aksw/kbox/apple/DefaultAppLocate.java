package org.aksw.kbox.apple;

import java.io.File;
import java.net.URL;

import org.aksw.kbox.KBox;

/**
 * 
 * @author {@linkplain http://emarx.org}
 *
 */
public class DefaultAppLocate extends DefaultAppLocator implements AppLocate {

	@Override
	public String locate(String resource, String format, String version)
			throws Exception {
		return URLToAbsolutePath(new URL(resource), format, version);
	}

	@Override
	public File locate(URL resource, String format, String version)
			throws Exception {
		File newFile = new File(URLToAbsolutePath(resource, format, version));
		return newFile;
	}

	@Override
	public boolean isValid(URL url, String format, String version)
			throws Exception {
		File resource = new File(URLToAbsolutePath(url, format, version));
		return KBox.isValid(resource.getAbsolutePath());
	}

}
