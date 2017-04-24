package org.aksw.kbox.utils;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

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
	
	public static long getContentLength(URL url) throws IOException {
        URLConnection conn = url.openConnection();
        Long contentLength = conn.getContentLengthLong();
        if(contentLength != null && contentLength >= 0) {
        	return contentLength;
        }
		String contentLengthValue = conn.getHeaderField("content-length");
		if(contentLengthValue != null) {
			return Long.valueOf(contentLengthValue);
		}
		return -1;
	}
	
	public static URL getURLForward(URL url) throws Exception {
		URLConnection con = url.openConnection();
		String location = con.getHeaderField("Location");
		if(location != null) {
			return new URL(location);
		}
		return url;
	}
}
