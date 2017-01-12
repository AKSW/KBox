package org.aksw.kbox.kibe.stream;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class DefaultInputStreamFactory implements InputStreamFactory {

	@Override
	public InputStream get(URL url) throws IOException {
		return url.openStream();
	}

}
