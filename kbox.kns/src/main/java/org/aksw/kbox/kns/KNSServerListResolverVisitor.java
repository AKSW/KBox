package org.aksw.kbox.kns;

import java.net.URL;

public class KNSServerListResolverVisitor implements KNSServerListVisitor {

	private URL resourceURL = null;
	private KN resolvedKN = null;
	private String format = null;
	private String version = null;

	public KNSServerListResolverVisitor(URL resourceURL) {
		this.resourceURL = resourceURL;
	}

	public KNSServerListResolverVisitor(URL resourceURL, String format, String version) {
		this(resourceURL);
		this.format = format;
		this.version = version;
	}

	@Override
	public boolean visit(KNSServer knsServer) throws Exception {
		KNSResolverVisitor resolver = new KNSResolverVisitor(resourceURL, format, version);
		knsServer.visit(resolver);
		KN rn = resolver.getResolvedKN();
		if(rn != null) {
			resolvedKN = rn;
			return false;
		}
		return true;
	}

	public KN getResolvedKN() {
		return resolvedKN;
	}
}
