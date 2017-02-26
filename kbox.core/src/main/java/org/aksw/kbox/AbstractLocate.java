package org.aksw.kbox;

import java.io.File;
import java.net.URL;

public abstract class AbstractLocate implements Locate, Locator {
	public String locate(String url) throws Exception {
		File localFile = locate(new URL(url));
		if(localFile != null) {
			return localFile.getAbsolutePath();
		}
		return null;
	}

	public File locate(URL url) throws Exception {
		File localFile = new File(URLToAbsolutePath(url));
		if(localFile.exists()) {
			return localFile;
		}
		return null;
	}
}
