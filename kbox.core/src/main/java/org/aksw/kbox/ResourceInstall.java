package org.aksw.kbox;

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

}
