package org.aksw.kbox;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.zeroturnaround.zip.ZipUtil;


/**
 * Default install implementation for compressed files.
 * 
 * @author {@linkplain http://emarx.org}
 *
 */
public class ZipInstall extends AbstractInstall {
	
	public ZipInstall() {
		super(new ZipPathBinder());
	}

	@Override
	public void install(InputStream inputStream, URL target) throws Exception {
		File destDir = new File(urlToAbsolutePath(target));
		destDir.mkdirs();
		ZipUtil.unpack(inputStream, destDir);
		validate(target);
	}
}
