package org.aksw.kbox.apple;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.aksw.kbox.utils.ZIPUtil;

/**
 * 
 * @author {@linkplain http://emarx.org}
 *
 */
public class ZipAppInstall extends AbstractInstall {
	
	@Override
	public void install(InputStream resource, URL dest, String format,
			String version) throws Exception {
		File destPath = new File(URLToAbsolutePath(dest, format, version));
		destPath.mkdirs();
		ZIPUtil.unzip(resource, destPath.getAbsolutePath());
	}
}