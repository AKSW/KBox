package org.aksw.kbox.appel;

import java.net.URL;

public class KNSResolveVisitor implements KNSServerListVisitor {
	
	private URL resourceURL = null;
	private URL resolvedURL = null;
	
	public KNSResolveVisitor(URL resourceURL) {
		this.resourceURL = resourceURL;
	}

	@Override
	public boolean visit(String knsServer) throws Exception {
		URL url = KNSTable.resolve(resourceURL, new URL(knsServer));
		if(url != null) {
			resolvedURL = url;
			return true;
		}
		return false;
	}
	
	public URL getResolvedURL() {
		return resolvedURL;
	}
}
