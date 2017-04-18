package org.aksw.kbox;

import java.net.URL;

public interface PathBinder {
	String urlToAbsolutePath(URL url) throws Exception;
}
