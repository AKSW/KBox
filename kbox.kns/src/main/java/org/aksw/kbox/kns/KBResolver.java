package org.aksw.kbox.kns;

import java.net.URL;

public class KBResolver implements Resolver {

	@Override
	public KN resolve(URL knsServerURL, URL resourceURL) throws Exception {
		return KNSTable.resolve(knsServerURL, resourceURL);
	}
	
	@Override
	public KN resolve(URL knsServerURL, URL resourceURL, String format) throws Exception {
		return KNSTable.resolve(resourceURL, knsServerURL, format, null);
	}
	
	@Override
	public KN resolve(URL knsServerURL, URL resourceURL, String format, String version) throws Exception {
		return KNSTable.resolve(knsServerURL, resourceURL, format, version);
	}

}
