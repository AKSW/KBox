package org.aksw.kbox.kibe;

import kbox.apple.AppLocate;

public class KBLocate extends AppLocate {
	
//	public final static String KB_GRAPH_DIR_NAME = "kbox.kb";
	public final static String FORMAT = "kbox.kibe";
	public final static String VERSION = "0";
	
	public KBLocate() {
		super(FORMAT, VERSION);
	}
	
//	@Override
//	public File locate(URL url) throws Exception {
//		File localfile = super.locate(url);
//		if(localfile == null) {
//			return null;
//		}
//		File localDataset = new File(localfile.getAbsolutePath() + File.separator + KB_GRAPH_DIR_NAME);
//		if(!localDataset.exists()) {
//			return null;
//		}
//		return localDataset;
//	}

}
