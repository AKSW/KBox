package org.aksw.kbox;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.aksw.kbox.utils.StreamUtils;

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
		File resourceDir = destFile.getParentFile();
		resourceDir.mkdirs();
		destFile.createNewFile();
		StreamUtils.stream(inputStream, destFile);
		validate(dest);
	}
	
	public void validate(URL url) throws Exception {
		File f = new File(urlToAbsolutePath(url));
		KBox.validate(f);
	}
}
