package org.aksw.kbox.kibe.console;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.aksw.kbox.kibe.stream.InputStreamFactory;
import org.aksw.kbox.kibe.stream.InputStreamInstaller;

public class ConsoleIntallInputStreamFactory implements InputStreamFactory {

	@Override
	public InputStream get(URL url) throws IOException {
		ConsoleStreamListener listener = new ConsoleStreamListener("Installing", -1);
		InputStreamInstaller installer = new InputStreamInstaller(url.openStream(), listener);
		return installer;
	}

}
