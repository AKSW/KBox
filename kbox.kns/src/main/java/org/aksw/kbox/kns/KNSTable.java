package org.aksw.kbox.kns;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class KNSTable {

	public final static String[] FILE_SERVER_TABLE_FILE_NAME_ARRAY = new String[] {"table.kns", "table.kns.json"};
	
	private final static Logger logger = Logger.getLogger(KNSTable.class);	
	
	protected URL tableURL = null; //todo change into private
	private int type;

	
	public KNSTable(URL knsServer) throws MalformedURLException {		
		this(knsServer.toString());
	}

	public KNSTable(String knsServer) throws MalformedURLException {
		URL url = null;
		for (int i = 0; i < FILE_SERVER_TABLE_FILE_NAME_ARRAY.length; i++) {
			String file = FILE_SERVER_TABLE_FILE_NAME_ARRAY[i];
			url = new URL(knsServer + org.aksw.kbox.URL.SEPARATOR + file);
			try {
				url.getContent();
				logger.info(file + " is available at the given endpoint");
				type = i;
				break;
			} catch (IOException e) {
				logger.warn(file + " is not available at the given endpoint.");
			}
		}
		this.tableURL = url;
	}

	protected KNSTable() { // to be used with the subclasses
		// TODO: 6/14/21 Remove this constructor
	}


	public boolean visit(KNSVisitor visitor) throws IOException {
		if (type == 0) {
			return defaultVisit(visitor);
		} else {
			return jsonVisit(visitor);
		}
	}

	private boolean defaultVisit(KNSVisitor visitor) throws IOException {
		boolean next = true;
		try(InputStream is = tableURL.openStream()) {
			try(BufferedReader in = new BufferedReader(new InputStreamReader(is))) {
				String line = null;
				while((line = in.readLine()) != null) {
					KN kn = null;
					try {
						String url = tableURL.toString();
						url = url.replace(org.aksw.kbox.URL.SEPARATOR + FILE_SERVER_TABLE_FILE_NAME_ARRAY[type], "");
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

	private boolean jsonVisit(KNSVisitor visitor) throws IOException {
		boolean next = true;
		JSONParser parser = new JSONParser();
		try(InputStream is = tableURL.openStream()) {
			try {
				JSONArray knsJsonArr = (JSONArray) parser.parse(new InputStreamReader(is));
				for (Object o : knsJsonArr) {
					JSONObject knJsonObject = (JSONObject) o;
					KN kn;
					try {
						String url = tableURL.toString();
						url = url.replace(org.aksw.kbox.URL.SEPARATOR + FILE_SERVER_TABLE_FILE_NAME_ARRAY[type], "");
						kn = KN.parse(knJsonObject.toJSONString());
						kn.setKNS(url);
						next = visitor.visit(kn);
						if(!next) {
							break;
						}
					} catch (Exception e) {
						logger.warn("KNS Table entry could not be parsed: " + knJsonObject, e);
					}
				}
			} catch (ParseException e) {
				logger.error("Error wile parsing the json", e);
			}
		}
		return next;
	}
}
