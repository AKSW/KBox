package org.aksw.kbox.kibe;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class KBoxMain {
	public static void main(String[] args) throws Exception {
		URL urlSource = new URL("http://openqa.aksw.org/download/openqa/queryparser/openqa.queryparser.tbsl-v0.0.7-beta.par");
		HttpURLConnection connection = (HttpURLConnection)urlSource.openConnection();
//		  connection.set
//		connection.setRequestMethod("HEAD");  

//          connection.connect();
          System.out.println("========"+connection.getContentLength());
//		connection.setRequestMethod("HEAD");
//		connection.getInputStream();
//		System.out.println(connection.getContentLengthLong());
          
          URLConnection conn = new URL("http://vmdbpedia.informatik.uni-leipzig.de:3031/foaf.kb").openConnection();
          InputStream stream = conn.getInputStream();

          System.out.println("total size: "+conn.getContentLength());//size8
	}
}
