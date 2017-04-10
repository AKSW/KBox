package org.aksw.kbox.kns;

import java.net.URL;

public class KBResolver implements Resolver {

	@Override
	public KN resolve(URL knsServerURL, URL resourceURL) throws Exception {
		KN resolvedKN = KNSTable.resolve(knsServerURL, resourceURL);
		return resolvedKN;
	}
	
	@Override
	public KN resolve(URL knsServerURL, URL resourceURL, String format) throws Exception {
		KN resolvedKN = KNSTable.resolve(resourceURL, knsServerURL, format, null);
		return resolvedKN;
	}
	
	@Override
	public KN resolve(URL knsServerURL, URL resourceURL, String format, String version) throws Exception {
		KN resolvedKN = KNSTable.resolve(knsServerURL, resourceURL, format, version);
		return resolvedKN;
	}

}
