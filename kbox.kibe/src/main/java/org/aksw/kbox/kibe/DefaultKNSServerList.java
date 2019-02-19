package org.aksw.kbox.kibe;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;

import org.aksw.kbox.kns.CustomRNSServerList;
import org.aksw.kbox.kns.RNService;
import org.aksw.kbox.kns.RNSServerListVisitor;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class DefaultKNSServerList extends CustomRNSServerList {
	
	
	private final static Logger logger = Logger.getLogger(DefaultKNSServerList.class);
	
	// Default KNS table URL
	private final static String DEFAULT_KNS_TABLE_URL = "https://raw.github.com/AKSW/kbox/master";
	private final static String DEFAULT_KNS_SERVER_TABLE_URL = "table.server.kns";

	/**
	 * 
	 */
	private static final long serialVersionUID = 2837993975272090928L;
	
	@Override
	public boolean visit(RNSServerListVisitor visitor) throws Exception {
		URL serverTable = new URL(DEFAULT_KNS_TABLE_URL + "/" + DEFAULT_KNS_SERVER_TABLE_URL);
		InputStream is = serverTable.openStream();
		boolean next = true;
		try(BufferedReader in = new BufferedReader(new InputStreamReader(is))) {
			String line = null;
			while((line = in.readLine()) != null) {
			    RNService knsServer = null;
				try {
					knsServer = parse(line);
					next = visitor.visit(knsServer);
					if(!next) {
						return false;
					}
				} catch (Exception e) {
					logger.error("KNS Server Table entry could not be parsed: " + line, e);
				}
			}
		}
		if(next) {
			next = super.visit(visitor);
		}
		return next;
	}
	
	private RNService parse(String json) throws Exception {
		JSONParser jsonParser = new JSONParser();
		Reader stringReader = new StringReader(json);
		JSONObject jsonObject = (JSONObject) jsonParser.parse(stringReader);
		String url = (String) jsonObject.get("url");
		RNService server = new RNService(new URL(url));
		return server;
	}

}
