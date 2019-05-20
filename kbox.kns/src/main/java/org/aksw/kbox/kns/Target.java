package org.aksw.kbox.kns;

import java.net.URL;
import java.util.Map;

public class Target {
	private String install;
	private URL url;
	private Map<String,String> checksum;
	private Map<String,String> dataId;
	
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

	public Map<String,String> getDataId() {
		return dataId;
	}

	public void setDataId(Map<String,String> dataId) {
		this.dataId = dataId;
	}
}
