package org.aksw.kbox;

import java.net.URL;

public class ResourcePathBinder implements PathBinder {
	@Override
	public String urlToAbsolutePath(URL url) throws Exception {
		return KBox.urlToAbsolutePath(url);
	}
}
