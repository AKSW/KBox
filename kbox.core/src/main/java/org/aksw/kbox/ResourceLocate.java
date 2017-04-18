package org.aksw.kbox;

import java.net.URL;

public class ResourceLocate extends AbstractLocate {
	@Override
	public String urlToAbsolutePath(URL url) throws Exception {
		return KBox.urlToAbsolutePath(url);
	}
}
