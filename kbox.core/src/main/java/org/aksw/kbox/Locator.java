package org.aksw.kbox;

import java.net.URL;

public interface Locator {
	String URLToAbsolutePath(URL url) throws Exception;
}
