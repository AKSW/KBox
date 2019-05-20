package org.aksw.kbox;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

public abstract class AbstractInstall extends AbstractPathBinder implements Install {
	
	public AbstractInstall(PathBinder pathBinder) {
		super(pathBinder);
	}

	@Override
	public void install(URL source, URL dest) throws Exception {
		try(InputStream is = source.openStream()) {
			install(is, dest);
		}
	}

	@Override
	public void install(InputStream inputStream, URL dest) throws Exception {
		File destFile = new File(urlToAbsolutePath(dest));
		stream(inputStream, destFile);
		validate(dest);
	}
	
	abstract public void stream(InputStream inputStream, File dest) throws Exception;
	
	public void validate(URL url) throws Exception {
		File f = new File(urlToAbsolutePath(url));
		KBox.validate(f);
	}
}
