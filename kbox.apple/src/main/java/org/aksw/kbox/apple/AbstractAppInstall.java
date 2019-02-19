package org.aksw.kbox.apple;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.aksw.kbox.InputStreamFactory;
import org.aksw.kbox.apple.stream.DefaultInputStreamFactory;
import org.aksw.kbox.utils.URLUtils;

/**
 * 
 * @author {@linkplain http://emarx.org}
 *
 */
public abstract class AbstractAppInstall extends AppPathBinder implements Install, PathBinder {

	@Override
	public void install(URL resource, URL dest, String format, String version)
			throws Exception {		
		install(resource, dest, format, version, new DefaultInputStreamFactory());
	}

	@Override
	public void install(URL resource, URL dest, String format, String version, InputStreamFactory isFactory) throws Exception {
		URL forwardURL = URLUtils.getURLForward(resource);
		try(InputStream is = isFactory.get(forwardURL)) {
			install(is, dest, format, version);			
		}
	}
	
	@Override
	public void install(InputStream inputStream, URL dest, String format,
			String version) throws Exception {
		File destPath = new File(urlToAbsolutePath(dest, format, version));
		destPath.mkdirs();
		if(destPath.isFile()) {
			destPath.createNewFile();
		}
		install(inputStream, destPath);
		register(dest, format, version);
		validate(dest, format, version);
	}
	
	public abstract void install(InputStream source, File target) throws Exception;
	
	public void register(URL path, String format, String version) throws Exception {
		File destFile = new File(urlToAbsolutePath(path, format, version));
		KBox.register(path, destFile, format, version);
	}

	@Override
	public void validate(URL path, String format, String version) throws Exception {
		File destFile = new File(urlToAbsolutePath(path, format, version));
		KBox.validate(destFile);
	}
}
