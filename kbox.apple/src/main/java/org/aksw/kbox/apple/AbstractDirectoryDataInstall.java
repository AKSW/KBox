package org.aksw.kbox.apple;

import java.io.File;
import java.security.InvalidParameterException;

/**
 * 
 * @author {@linkplain http://emarx.org}
 *
 */
public abstract class AbstractDirectoryDataInstall extends AbstractMultiSourceAppInstall {
	protected void createPath(File destPath) throws Exception {
		if(destPath.isFile()) {
			throw new InvalidParameterException("The parameter destPath should be a directory.");
		}
		destPath.mkdirs();
	}
}