package org.aksw.kbox.kns;

import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class KN {
	private String name;
	private String target;
	private String desc;
	private String kns;
	private String license;
	private String subsets;
	private String format;
	private String version;
	
	public KN(String name, String target, String desc) {
		this.name = name;
		this.target = target;
		this.desc = desc;
	}
	
	public KN(String name, String target, String license, String subsets, String desc) {
		this.name = name;
		this.target = target;
		this.license = license;
		this.subsets = subsets;
		this.desc = desc;
	}
	
	public KN(String name, String target, String format, String version, String license, String subsets, String desc) {
		this.name = name;
		this.target = target;
		this.format = format;
	    this.version = version;
		this.license = license;
		this.subsets = subsets;
		this.desc = desc;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getTarget() {
		return target;
	}
	
	public URL getTargetURL() throws Exception {
		return new URL(target);
	}
	
	public void setTarget(String target) {
		this.target = target;
	}
	
	public static KN parse(String json) throws Exception {
		JSONParser jsonParser = new JSONParser();
		Reader stringReader = new StringReader(json);
		JSONObject jsonObject = (JSONObject) jsonParser.parse(stringReader);
		String name = (String) jsonObject.get("name");
		String target = (String) jsonObject.get("target");
		String desc = (String) jsonObject.get("description");
		String version = (String) jsonObject.get("version");
		String format = (String) jsonObject.get("format");
		Object o = jsonObject.get("license");
		String license = null;
		if(o != null) {
			license = o.toString().replace("\\", "");
		}		
		o = jsonObject.get("subsets");
		String subsets = null;
		if(o != null) {
			subsets = o.toString().replace("\\", "");
		}
		return new KN(name, target, format, version, license, subsets, desc);
	}
	
	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}

	public void setKNS(String url) {
		this.kns = url;
	}
	
	public String getKNS() {
		return this.kns;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public String getSubsets() {
		return subsets;
	}

	public void setSubsets(String subsets) {
		this.subsets = subsets;
	}

	public void printURL(PrintStream out) {
		out.println(getName());
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public boolean equals(String name) {
		return this.name.equals(name);
	}

	public boolean equals(String name, String format, String version) {
		return this.name.equals(name) &&
				(format == null || format.equals(this.format)) &&
				(version == null || version.equals(this.version));
	}

	public boolean contains(String name, String format, String version) {
		return this.name.contains(name) &&
				(format == null || format.equals(this.format)) &&
				(version == null || version.equals(this.version));
	}

	public void print(PrintStream out) {
		out.println("KNS:" + getKNS());
		out.println("KB:" + getName());
		String description = getDesc();
		String subsets = getSubsets();
		String license = getLicense();
		if(subsets != null) {
			out.println("Subsets:" + subsets);
		}
		if(license != null) {
			out.println("License:" + license);
		}
		if(description != null) {
			out.println("Description:" + description);
		}
	}
}
