package org.aksw.kbox;

import java.net.URL;

public class AbstractPathBinder implements PathBinder {
	
	private PathBinder pathBinder = null;
	
	public AbstractPathBinder(PathBinder pathBinder) {
		this.pathBinder = pathBinder;
	}

	@Override
	public String urlToAbsolutePath(URL url) throws Exception {
		return pathBinder.urlToAbsolutePath(url);
	}

}
