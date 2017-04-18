package org.aksw.kbox;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.aksw.kbox.utils.ZIPUtil;

/**
 * Default install implementation for compressed files.
 * 
 * @author {@linkplain http://emarx.org}
 *
 */
public class ZipInstall extends ResourceInstall {
	
	@Override
	public void install(InputStream resource, URL target) throws Exception {
		File destPath = new File(urlToAbsolutePath(target));
		destPath.mkdirs();
		ZIPUtil.unzip(resource, destPath.getAbsolutePath());
	}
}
