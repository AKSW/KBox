package org.aksw.kbox;

import java.net.URL;

public class ResourceLocate extends AbstractLocate {
	@Override
	public String URLToAbsolutePath(URL url) {
		return KBox.URLToAbsolutePath(url);
	}
}
