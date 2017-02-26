package org.aksw.kbox.kibe;

import kbox.apple.AppZipInstall;

public class KBInstall extends AppZipInstall {

	public final static String KB_GRAPH_DIR_NAME = "kbox.kb";
	public final static String FORMAT = "kbox.kibe";
	public final static String VERSION = "0";

	public KBInstall() {
		super(FORMAT, VERSION);
	}
//	
//	@Override
//	public void install(URL source, URL target) throws Exception {
//		URL finalDest = new URL(target.toString() + "/" + KB_GRAPH_DIR_NAME);
//		super.install(source, finalDest);
//	}

}
