package org.aksw.kbox.kibe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

public class KNSTable {
	
	private final static Logger logger = Logger.getLogger(KNSTable.class);	
	
	private URL tableURL = null;
	
	public KNSTable(URL url) throws MalformedURLException {
		tableURL = url;
	}
	
	public KNSTable(String url) throws MalformedURLException {
		tableURL = new URL(url);
	}
	
	public void visit(KNSVisitor visitor) throws IOException {
		InputStream is = tableURL.openStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		String line = null;
		while((line = in.readLine()) != null) {
		    KN kn = null;
			try {
				String url = tableURL.toString();
				kn = KN.parse(line);
				kn.setKNS(url);
				visitor.visit(kn);
			} catch (Exception e) {
				logger.error("KNS Table entry could not be parsed: " + line, e);
			}
		}
	}
}
