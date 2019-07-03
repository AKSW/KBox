package org.aksw.kbox.kns;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aksw.kbox.utils.URLUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Source {
	private String install;
	private URL url;
	private Map<String,String> checksum;
	
	public String getInstall() {
		return install;
	}

	public void setInstall(String install) {
		this.install = install;
	}

	public URL getURL() {
		return url;
	}

	public void setURL(URL url) {
		this.url = url;
	}

	public Map<String,String> getChecksum() {
		return checksum;
	}

	public void setChecksum(Map<String,String> checksum) {
		this.checksum = checksum;
	}
	
	public static List<Source> toTarget(String json) throws Exception {
		JSONParser jsonParser = new JSONParser();
		Reader stringReader = new StringReader(json);
		JSONArray jsonArray = (JSONArray) jsonParser.parse(stringReader);
		return toTarget(jsonArray);
	}
	
	public static List<Source> toTarget(JSONArray targetArray) throws Exception {
		java.util.List<Source> sources = new ArrayList<Source>();
		if(targetArray != null) {
			for(int m = 0; m < targetArray.size(); m++) {
				JSONArray jsonMirror = (JSONArray) targetArray.get(m);
				for(int s = 0; s < jsonMirror.size(); s++) {
					JSONObject jsonSource = (JSONObject) jsonMirror.get(s);
					String install = (String) jsonSource.keySet().toArray()[0];
					JSONArray jsonSources = (JSONArray) jsonSource.values().toArray()[0];
					if(jsonSources != null) {
						for(int i= 0; i < jsonSources.size(); i++) {
							JSONObject jsonSourceEntry = (JSONObject)jsonSources.get(i);
							String url = (String)jsonSourceEntry.get(KN.URL);
							JSONObject jsonChecksum = (JSONObject) jsonSourceEntry.get(KN.CHECKSUM);
							Map<String, String> checksum = new HashMap<String, String>();
							if(jsonChecksum != null) {
								for(int c = 0; c < jsonChecksum.keySet().size(); c++) {
									String key = (String) jsonChecksum.keySet().toArray()[c];
									String value = (String) jsonChecksum.values().toArray()[c];
									checksum.put(key, value);
								}
							}
							JSONObject jsonDataId = (JSONObject) jsonSourceEntry.get(KN.DATAID);
							Map<String, String> dataId = new HashMap<String, String>();
							if(jsonDataId != null) {
								for(int di = 0; di < jsonDataId.keySet().size(); di++) {
									String key = (String) jsonDataId.keySet().toArray()[di];
									String value = (String) jsonDataId.values().toArray()[di];
									dataId.put(key, value);
								}
							}
							Source source = new Source();
							source.setInstall(install);
							source.setChecksum(checksum);
							URI uri = new URI(url);
							URL knName = null;
							if(uri.isAbsolute() && URLUtils.hasValidURLHost(uri)) {
								knName = new URL(url);
							} else {
								File file = new File(url);
								knName = file.toURI().toURL();
							}
							source.setURL(knName);
							sources.add(source);
						}
					}
				}
			}
		}
		return sources;
	}
}
