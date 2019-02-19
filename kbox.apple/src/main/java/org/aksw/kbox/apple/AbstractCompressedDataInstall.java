package org.aksw.kbox.apple;

import java.io.File;

public abstract class AbstractCompressedDataInstall extends AbstractAppInstall {
	protected void createPath(File destPath) throws Exception {
		destPath.mkdirs();
	}
}
