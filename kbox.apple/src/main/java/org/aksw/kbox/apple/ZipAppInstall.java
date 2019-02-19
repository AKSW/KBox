package org.aksw.kbox.apple;

import java.io.File;
import java.io.InputStream;

import org.zeroturnaround.zip.ZipUtil;

/**
 * 
 * @author {@linkplain http://emarx.org}
 *
 */
public class ZipAppInstall extends AbstractCompressedDataInstall {
	@Override
	public void install(InputStream source, File target) throws Exception {
		ZipUtil.unpack(source, target);
	}
}