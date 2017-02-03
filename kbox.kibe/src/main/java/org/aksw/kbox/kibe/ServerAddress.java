package org.aksw.kbox.kibe;

public class ServerAddress {
	
	private final static String DEFAULT_FUSEKI_QUERY_URL_API = "query";
	
	private String url = null;
	
	public ServerAddress(String url) {
		this.url = url;
	}
	
	public String getURL() {
		return url;
	}
	
	public String getQueryURL() {
		return url + "/" + DEFAULT_FUSEKI_QUERY_URL_API;
	}
}
