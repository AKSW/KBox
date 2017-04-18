package org.aksw.kbox;

import java.net.URL;

public interface PathBinder {
	String URLToAbsolutePath(URL url) throws Exception;
}
