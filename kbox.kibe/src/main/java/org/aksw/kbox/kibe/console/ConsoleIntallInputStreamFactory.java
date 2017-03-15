package org.aksw.kbox.kibe.console;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.aksw.kbox.InputStreamFactory;
import org.aksw.kbox.kibe.stream.InputStreamInstaller;

public class ConsoleIntallInputStreamFactory implements InputStreamFactory {

	@Override
	public InputStream get(URL url) throws IOException {
//		InputStream is = url.openStream();
		URLConnection urlConnection = url.openConnection();
		ConsoleStreamListener listener = new ConsoleStreamListener("Installing", urlConnection.getContentLength());
		InputStreamInstaller installer = new InputStreamInstaller(url.openStream(), listener);
		return installer;
	}

}
