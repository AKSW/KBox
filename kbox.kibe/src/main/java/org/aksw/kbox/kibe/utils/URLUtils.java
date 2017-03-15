package org.aksw.kbox.kibe.utils;

import java.net.URL;

public class URLUtils {
	public static URL [] stringToURL(String... urls) throws Exception {
		URL[] urlArray = new URL[urls.length];		
		for(int i=0; i < urls.length; i++) {
			urlArray[i]  = new URL(urls[i]);
		}
		return urlArray;
	}	
}
