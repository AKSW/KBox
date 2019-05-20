package org.aksw.kbox.kibe;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.aksw.kbox.kns.KNSServer;
import org.aksw.kbox.kns.KNSServerList;
import org.aksw.kbox.kns.KNSServerListVisitor;
import org.apache.log4j.Logger;

public class URLKNSServerList implements KNSServerList {

	private URL serverURL;

	private final static Logger logger = Logger.getLogger(URLKNSServerList.class);

	private final static String DEFAULT_KNS_SERVER_TABLE_URL = "table.server.kns";

	public URLKNSServerList(URL serverURL) {
		this.serverURL = serverURL;
	}

	@Override
	public boolean visit(KNSServerListVisitor visitor) throws Exception {
		URL serverTable = new URL(serverURL + org.aksw.kbox.URL.SEPARATOR + DEFAULT_KNS_SERVER_TABLE_URL);
		boolean next = true;
		try(InputStream is = serverTable.openStream()) {			
			try(BufferedReader in = new BufferedReader(new InputStreamReader(is))) {
				String line = null;
				while((line = in.readLine()) != null) {
				    KNSServer knsServer = null;
					try {
						knsServer = KNSServerFactory.get(line);
						next = visitor.visit(knsServer);
						if(!next) {
							return false;
						}
					} catch (Exception e) {
						logger.error("KNS Server Table entry could not be parsed: " + line, e);
					}
				}
			}
		}
		return next;
	}
}
