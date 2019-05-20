package org.aksw.kbox;

import java.io.File;
import java.io.InputStream;

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
	public void stream(InputStream inputStream, File target) throws Exception {
		target.mkdirs();
		ZipUtil.unpack(inputStream, target);
	}
}
