package org.aksw.kbox.apple;

import java.io.File;
import java.net.URL;

import org.aksw.kbox.KBox;

/**
 * 
 * @author {@linkplain http://emarx.org}
 *
 */
public class AppPathBinder implements PathBinder {
	public String urlToAbsolutePath(URL url, String format, String version) throws Exception {
		return KBox.getResourceFolder()
				+ pathBuilder(format)
				+ pathBuilder(version)
				+ File.separator
				+ KBox.urlToPath(url);
	}
	
	private String pathBuilder(String string) {
		StringBuilder pathBuilder = new StringBuilder();
		if(string != null) {
			pathBuilder.append(File.separator
				+ string);
		}
		return pathBuilder.toString();
	}
}
