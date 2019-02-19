package org.aksw.kbox.apple;

import java.io.File;
import java.io.InputStream;

import org.aksw.kbox.utils.GzipUtils;

/**
 * 
 * @author {@linkplain http://emarx.org}
 *
 */
public class GZipAppInstall extends AbstractCompressedDataInstall {
	public void install(InputStream source, File target) throws Exception {
		GzipUtils.unpack(source, target);
	}
}