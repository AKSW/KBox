package org.aksw.kbox.kns;

import java.net.URL;

public class KNSServer {
	
	private URL url = null;
	private KNSTable table = null;

	public KNSServer(URL url) throws Exception {
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
