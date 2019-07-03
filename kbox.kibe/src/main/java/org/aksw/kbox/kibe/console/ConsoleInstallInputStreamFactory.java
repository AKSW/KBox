package org.aksw.kbox.kibe.console;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.aksw.kbox.InputStreamFactory;
import org.aksw.kbox.kibe.stream.InputStreamInstaller;
import org.aksw.kbox.utils.URLUtils;

public class ConsoleInstallInputStreamFactory implements InputStreamFactory {
	
	private final static String MESSAGE = "Installing";

	@Override
	public InputStream get(URL url) throws IOException {
		ConsoleStreamListener listener = new ConsoleStreamListener(MESSAGE + " " + url, URLUtils.getContentLength(url));
		InputStreamInstaller installer = new InputStreamInstaller(url.openStream(), listener);
		return installer;
	}

}
