package org.aksw.kbox.kibe;

import java.io.File;
import java.net.URL;

import org.aksw.kbox.ResourceLocate;

public class KBLocate extends ResourceLocate {
	
	public final static String KB_GRAPH_DIR_NAME = "kbox.kb";
	
	@Override
	public File locate(URL url) throws Exception {
		File localfile = super.locate(url);
		if(localfile == null) {
			return null;
		}
		File localDataset = new File(localfile.getAbsolutePath() + File.separator + KB_GRAPH_DIR_NAME);
		if(!localDataset.exists()) {
			return null;
		}
		return localDataset;
	}

}
