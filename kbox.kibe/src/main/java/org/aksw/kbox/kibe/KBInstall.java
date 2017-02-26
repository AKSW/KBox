package org.aksw.kbox.kibe;

import java.net.URL;

import org.aksw.kbox.ZipInstall;

public class KBInstall extends ZipInstall {	

	public final static String KB_GRAPH_DIR_NAME = "kbox.kb";
	
	@Override
	public void install(URL source, URL target) throws Exception {
		URL finalDest = new URL(target.toString() + "/" + KB_GRAPH_DIR_NAME);
		super.install(source, finalDest);
	}

}
