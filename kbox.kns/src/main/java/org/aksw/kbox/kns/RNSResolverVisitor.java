package org.aksw.kbox.kns;

import java.net.URL;

public class RNSResolverVisitor implements RNSServerListVisitor {

	private URL resourceURL = null;
	private RN resolvedKN = null;
	private String format = null;
	private String version = null;
	private Resolver resolver = null;

	public RNSResolverVisitor(URL resourceURL, Resolver resolver) {
		this.resourceURL = resourceURL;
		this.resolver = resolver;
	}

	public RNSResolverVisitor(URL resourceURL, String format, String version, Resolver resolver) {
		this(resourceURL, resolver);
		this.format = format;
		this.version = version;
	}

	@Override
	public boolean visit(RNService knsServer) throws Exception {
		RN rn = null;
		URL knsServerURL = knsServer.getURL();
		rn = resolver.resolve(knsServerURL, resourceURL, format, version);
		if(rn != null) {
			resolvedKN = rn;
			return false;
		}
		return true;
	}

	public RN getResolvedKN() {
		return resolvedKN;
	}
}
