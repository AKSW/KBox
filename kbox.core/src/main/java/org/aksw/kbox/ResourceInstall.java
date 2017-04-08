package org.aksw.kbox;

import java.net.URL;

/**
 * Default install implementation for files.
 * 
 * @author {@linkplain http://emarx.org}
 *
 */
public class ResourceInstall extends AbstractInstall {
	
	public ResourceInstall() {		
	}
	
	public ResourceInstall(InputStreamFactory isFactory) {
		super(isFactory);
	}

	@Override
	public String URLToAbsolutePath(URL url) throws Exception {
		return KBox.URLToAbsolutePath(url);
	}	

}
