package org.aksw.kbox;

import java.net.URL;

public class ResourceLocate extends AbstractLocate {
	@Override
	public String URLToAbsolutePath(URL url) throws Exception {
		return KBox.URLToAbsolutePath(url);
	}
}
