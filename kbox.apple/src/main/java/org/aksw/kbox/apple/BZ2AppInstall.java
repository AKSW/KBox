package org.aksw.kbox.apple;

import java.io.File;
import java.io.InputStream;

import org.aksw.kbox.apple.util.UncompressUtil;

/**
 * 
 * @author {@linkplain http://emarx.org}
 *
 */
public class BZ2AppInstall extends AbstractMultiSourceAppInstall {
	public void install(InputStream source, File target) throws Exception {
		UncompressUtil.unpack(source, target);
	}
}