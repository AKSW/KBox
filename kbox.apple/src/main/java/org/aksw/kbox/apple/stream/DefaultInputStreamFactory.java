package org.aksw.kbox.apple.stream;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.aksw.kbox.InputStreamFactory;

public class DefaultInputStreamFactory implements InputStreamFactory {

	@Override
	public InputStream get(URL url) throws IOException {
		return url.openStream();
	}

}
