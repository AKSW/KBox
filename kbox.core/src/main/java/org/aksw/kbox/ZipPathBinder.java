package org.aksw.kbox;

import java.io.File;
import java.net.URL;

public class ZipPathBinder implements PathBinder {
	@Override
	public String urlToAbsolutePath(URL url) throws Exception {
		File destFile = new File(KBox.urlToAbsolutePath(url));
		File resourceDir = destFile.getParentFile();
		String fileName = destFile.getName();
		File newResourceDir = new File(resourceDir.getAbsolutePath() + "/" + fileName.replace(".", "/"));
		return newResourceDir.getAbsolutePath();
	}
}
