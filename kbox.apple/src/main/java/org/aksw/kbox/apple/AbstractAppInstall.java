package org.aksw.kbox.apple;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.aksw.kbox.InputStreamFactory;

/**
 * 
 * @author {@linkplain http://emarx.org}
 *
 */
public abstract class AbstractAppInstall extends DefaultAppLocator implements AppInstall, AppLocator {

	@Override
	public void install(URL source, URL target, String format, String version)
			throws Exception {
		try(InputStream is = source.openStream()) {
			install(is, target, format, version);
			validate(target, format, version);
		}
	}
	
	@Override
	public void install(URL resource, URL dest, String format, String version, InputStreamFactory isFactory) throws Exception {
		try(InputStream is = isFactory.get(resource)) {
			install(is, dest, format, version);
			validate(dest, format, version);
		}
	}
	
	@Override
	public void validate(URL path, String format, String version) throws Exception {
		File destFile = new File(URLToAbsolutePath(path, format, version));
		KBox.validate(destFile);
	}

}
