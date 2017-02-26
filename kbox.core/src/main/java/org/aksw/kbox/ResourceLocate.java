package org.aksw.kbox;

import java.io.File;
import java.net.URL;

public class ResourceLocate implements Locate {

	@Override
	public File locate(URL url) throws Exception {
		File localFile = new File(KBox.URLToAbsolutePath(url));
		if(localFile.exists()) {
			return localFile;
		}
		return null;
	}

	@Override
	public String locate(String url) throws Exception {
		File localFile = locate(new URL(url));
		if(localFile != null) {
			return localFile.getAbsolutePath();
		}
		return null;
	}

}
