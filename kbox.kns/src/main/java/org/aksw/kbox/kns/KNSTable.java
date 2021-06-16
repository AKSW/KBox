package org.aksw.kbox.kns;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

public class KNSTable {

	public final static String FILE_SERVER_TABLE_FILE_NAME = "table.kns";
	
	private final static Logger logger = Logger.getLogger(KNSTable.class);	
	
	private URL tableURL = null;
	
	public KNSTable(URL knsServer) throws MalformedURLException {		
		this(knsServer.toString());
	}

	public KNSTable(String knsServer) throws MalformedURLException {
		this.tableURL = new URL(knsServer + org.aksw.kbox.URL.SEPARATOR + FILE_SERVER_TABLE_FILE_NAME);
	}

	public boolean visit(KNSVisitor visitor) throws IOException {
		boolean next = true;
		try(InputStream is = tableURL.openStream()) {
			try(BufferedReader in = new BufferedReader(new InputStreamReader(is))) {
				String line = null;
				while((line = in.readLine()) != null) {
				    KN kn = null;
					try {
						String url = tableURL.toString();
						url = url.replace(org.aksw.kbox.URL.SEPARATOR + FILE_SERVER_TABLE_FILE_NAME, "");
						kn = KN.parse(line);
						kn.setKNS(url);
						next = visitor.visit(kn);
						if(!next) {
							break;
						}
					} catch (Exception e) {
						logger.warn("KNS Table entry could not be parsed: " + line, e);
					}
				}
			}
		}
		return next;
	}
}
