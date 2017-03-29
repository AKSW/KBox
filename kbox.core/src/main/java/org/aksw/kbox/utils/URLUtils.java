package org.aksw.kbox.utils;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class URLUtils {
	public static URL[] stringToURL(String... urls) throws Exception {
		URL[] urlArray = new URL[urls.length];		
		for(int i=0; i < urls.length; i++) {
			urlArray[i]  = new URL(urls[i]);
		}
		return urlArray;
	}
	
	public static boolean checkStatus(URL url, int code) throws IOException {
		URLConnection conn = url.openConnection();
		HttpURLConnection huc =  (HttpURLConnection) url.openConnection(); 
		huc.setRequestMethod("HEAD");
		conn.connect();
		return huc.getResponseCode() == code;
	}
	
	public static URL[] fileToURL(File... files) throws MalformedURLException {
		URL[] urls = new URL[files.length];
		int i = 0;
		for(File file : files) {
			urls[i] = file.toURI().toURL();
			i++;
		}		
		return urls;
	}
}
