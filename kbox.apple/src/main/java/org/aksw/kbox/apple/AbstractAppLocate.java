package org.aksw.kbox.apple;

import java.io.File;
import java.net.URL;

import org.aksw.kbox.KBox;

public abstract class AbstractAppLocate extends AppPathBinder implements Locate, PathBinder {
	@Override
	public String locate(String resource, String format, String version)
			throws Exception {
		File localFile = locate(new URL(resource), format, version);
		if(localFile != null) {
			localFile.getAbsolutePath();
		}
		return null;
	}

	@Override
	public File locate(URL resource, String format, String version)
			throws Exception {
		File localFile = new File(urlToAbsolutePath(resource, format, version));
		if(localFile.exists() && isValid(resource, format, version)) {
			return localFile;
		}
		return null;
	}

	@Override
	public boolean isValid(URL url, String format, String version)
			throws Exception {
		File resource = new File(urlToAbsolutePath(url, format, version));
		return KBox.isValid(resource.getAbsolutePath());
	}
}
