package org.aksw.kbox.kns;

import java.net.URL;

public class KNSResolverVisitor implements KNSServerListVisitor {
	
	private URL resourceURL = null;
	private URL resolvedURL = null;
	private String format = null;
	private String version = null;
	private Resolver resolver = null;
	
	public KNSResolverVisitor(URL resourceURL, Resolver resolver) {
		this.resourceURL = resourceURL;
		this.resolver = resolver;
	}

	public KNSResolverVisitor(URL resourceURL, String format, String version, Resolver resolver) {
		this(resourceURL, resolver);
		this.format = format;
		this.version = version;
	}

	@Override
	public boolean visit(String knsServer) throws Exception {
		URL url = null;
		if(format == null && version == null) {
			url = resolver.resolve(new URL(knsServer), resourceURL);
		} else if(format != null && version != null){
			url = resolver.resolve(new URL(knsServer), resourceURL, format, version);
		} else if(format != null){
           url = resolver.resolve(new URL(knsServer), resourceURL, format);
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
