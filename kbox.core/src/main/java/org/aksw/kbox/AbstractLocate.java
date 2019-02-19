package org.aksw.kbox;

import java.io.File;
import java.net.URL;

public abstract class AbstractLocate extends AbstractPathBinder implements Locate {

	public AbstractLocate(PathBinder pathBinder) {
		super(pathBinder);
	}
	
	@Override
	public String urlToAbsolutePath(URL url) throws Exception {
		return pathBinder.urlToAbsolutePath(url);
	}
	
	public String locate(String url) throws Exception {
		File localFile = locate(new URL(url));
		if(localFile != null) {
			return localFile.getAbsolutePath();
		}
		return null;
	}

	public File locate(URL url) throws Exception {
		String path = urlToAbsolutePath(url);
		File localFile = new File(path);
		if(localFile.exists() && isValid(url)) {
			return localFile;
		}
		return null;
	}
	
	@Override
	public boolean isValid(String url) throws Exception {
		return isValid(new URL(url));
	}
	
	@Override
	public boolean isValid(URL url) throws Exception {
		String path = urlToAbsolutePath(url);
		File localFile = new File(path);
		return KBox.isValid(localFile.getAbsolutePath());
	}
}
