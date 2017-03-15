package org.aksw.kbox;

import java.io.InputStream;
import java.net.URL;

public interface InputStreamFactory {
	public InputStream get(URL url) throws Exception;
}
