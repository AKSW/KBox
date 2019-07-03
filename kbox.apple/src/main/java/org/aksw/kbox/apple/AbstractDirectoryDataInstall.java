package org.aksw.kbox.apple;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.security.InvalidParameterException;

import org.aksw.kbox.InputStreamFactory;

/**
 * 
 * @author {@linkplain http://emarx.org}
 *
 */
public abstract class AbstractDirectoryDataInstall extends AbstractMultiSourceAppInstall {
	protected void createPath(File destPath) throws Exception {
		if(destPath.isFile()) {
			throw new InvalidParameterException("The parameter destPath should be a directory.");
		}
		destPath.mkdirs();
	}
	
	public void install(URL source, File dest, InputStreamFactory isFactory) throws Exception {
		try(InputStream is = isFactory.get(source)) {
			install(is, dest);
		}
	}
}