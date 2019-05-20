package org.aksw.kbox;

import java.io.File;
import java.io.InputStream;

import org.aksw.kbox.utils.StreamUtils;

/**
 * Default install implementation for files.
 * 
 * @author {@linkplain http://emarx.org}
 *
 */
public class ResourceInstall extends AbstractInstall {

	public ResourceInstall() {
		super(new ResourcePathBinder());
	}	

	public void stream(InputStream inputStream, File dest) throws Exception {
		File resourceDir = dest.getParentFile();
		resourceDir.mkdirs();
		dest.createNewFile();
		StreamUtils.stream(inputStream, dest);
	}
	
}
