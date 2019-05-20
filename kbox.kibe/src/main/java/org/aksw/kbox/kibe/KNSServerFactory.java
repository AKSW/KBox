package org.aksw.kbox.kibe;

import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.aksw.kbox.kns.KBoxKNSServer;
import org.aksw.kbox.kns.KNSServer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class KNSServerFactory {
	
	public static KNSServer get(String json) throws MalformedURLException, Exception {
		JSONParser jsonParser = new JSONParser();
		Reader stringReader = new StringReader(json);
		JSONObject jsonObject = (JSONObject) jsonParser.parse(stringReader);
		String url = (String) jsonObject.get("url");
		KNSServer server = null;
		if(jsonObject.containsKey("sparql")) {
			JSONObject paramMap = (JSONObject) jsonObject.get("mapping");
			Map<String, String> mappings = new HashMap<String, String>();
			if(paramMap != null) {
				for(int i = 0; i < paramMap.keySet().size(); i++){
					String key = (String) paramMap.keySet().toArray()[i];
					String value = (String) paramMap.values().toArray()[i];
					mappings.put(key, value);
				}
			}
			String sparql = (String) jsonObject.get("sparql");
			server = new SPARQLKNSServer(url, sparql, mappings);
			return server;
		}
		server = new KBoxKNSServer(new URL(url));
		return server;
	}
}
