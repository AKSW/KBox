package org.aksw.kbox.apple;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.aksw.kbox.utils.StreamUtils;

/**
 * 
 * @author {@linkplain http://emarx.org}
 *
 */
public class ResourceAppInstall extends AbstractAppInstall {
	
	@Override
	public void install(InputStream inputStream, URL dest, String format,
			String version) throws Exception {
		File destPath = new File(urlToAbsolutePath(dest, format, version));
		File parentDir = new File(destPath.getParent());
		parentDir.mkdirs();
		destPath.createNewFile();
		install(inputStream, destPath);
	}
	
	@Override
	public void install(InputStream source, File target) throws Exception {
		StreamUtils.stream(source, target);
	}
}