package org.aksw.kbox;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.aksw.kbox.utils.GzipUtils;

/**
 * Default install implementation for Gzip compressed files.
 * 
 * @author {@linkplain http://emarx.org}
 *
 */
public class GzipInstall extends ResourceInstall {
	
	@Override
	public void install(InputStream resource, URL target) throws Exception {
		File destPath = new File(urlToAbsolutePath(target));
		destPath.mkdirs();
		GzipUtils.unpack(resource, destPath);
	}
}
