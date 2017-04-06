package org.aksw.kbox.kns;

import java.net.URL;

public class KNSResolverVisitor implements KNSServerListVisitor {
	
	private URL resourceURL = null;
	private URL resolvedURL = null;
	private String format = null;
	private String version = null;
	
	public KNSResolverVisitor(URL resourceURL) {
		this.resourceURL = resourceURL;
	}

	public KNSResolverVisitor(URL resourceURL, String format, String version) {
		this(resourceURL);
		this.format = format;
		this.version = version;		
	}

	@Override
	public boolean visit(String knsServer) throws Exception {
		URL url = null;
		if(format == null || version == null) {
			url = KNSTable.resolve(resourceURL, new URL(knsServer));
		} else {
			url = KNSTable.resolve(resourceURL, new URL(knsServer), format, version);
		}
		if(url != null) {
			resolvedURL = url;
			return false;
		}
		return true;
	}
	
	public URL getResolvedURL() {
		return resolvedURL;
	}
}
