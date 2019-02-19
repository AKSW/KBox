package org.aksw.kbox.kns;

import java.net.URL;

public class RNService {
	
	private URL url = null;
	private KNSTable table = null;

	public RNService(URL url) throws Exception {
		this.url = url;
		this.table = new KNSTable(url);
	}
	
	public URL getURL() {
		return url;
	}
	
	public KNSTable getKNSTable() {
		return table;
	}

}
