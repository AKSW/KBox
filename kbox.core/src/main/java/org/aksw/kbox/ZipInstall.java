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
public class ZipInstall implements Install {	
	
	@Override
	public void install(URL source, URL dest) throws Exception {
		install(source.openStream(), dest);
	}

	@Override
	public void install(InputStream resource, URL dest) throws Exception {
		File urlDir = KBox.newDir(dest);
		ZIPUtil.unzip(resource, urlDir.getAbsolutePath());
	}

}
