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
		String path = URLToAbsolutePath(url);
		File localFile = new File(path);
		if(localFile.exists() && KBox.isValid(path)) {
			return localFile;
		}
		return null;
	}
}
