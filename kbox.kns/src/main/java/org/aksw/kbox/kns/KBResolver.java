package org.aksw.kbox.kns;

import java.net.URL;

public class KBResolver implements Resolver {
	public KBResolver() {		
	}

	@Override
	public URL resolve(URL knsServerURL, URL resourceURL) throws Exception {
		URL resolvedURL = KNSTable.resolve(knsServerURL, resourceURL);
		return resolvedURL;
	}
	
	@Override
	public URL resolve(URL knsServerURL, URL resourceURL, String format) throws Exception {
		URL resolvedURL = KNSTable.resolve(resourceURL, knsServerURL, format, null);
		return resolvedURL;
	}
	
	@Override
	public URL resolve(URL knsServerURL, URL resourceURL, String format, String version) throws Exception {
		URL resolvedURL = KNSTable.resolve(resourceURL, knsServerURL, format, version);
		return resolvedURL;
	}

}
