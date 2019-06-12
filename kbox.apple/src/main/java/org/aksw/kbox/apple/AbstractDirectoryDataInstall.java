package org.aksw.kbox.apple;

import java.io.File;

/**
 * 
 * @author {@linkplain http://emarx.org}
 *
 */
public abstract class AbstractDirectoryDataInstall extends AbstractMultiSourceAppInstall {
	protected void createPath(File destPath) throws Exception {
		destPath.mkdirs();
	}
}