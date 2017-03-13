package org.aksw.kbox.kns;

import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class KN {
	private String name;
	private String target;
	private String desc;
	private String kns;
	private String license;
	private String subsets;
	
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
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getTarget() {
		return target;
	}
	
	public void setTarget(String target) {
		this.target = target;
	}
	
	public static KN parse(String string) throws Exception {
		JSONParser jsonParser = new JSONParser();
		Reader stringReader = new StringReader(string);
		JSONObject jsonObject = (JSONObject) jsonParser.parse(stringReader);
		String name = (String) jsonObject.get("name");
		String target = (String) jsonObject.get("target");
		String desc = (String) jsonObject.get("description");
		Object o = jsonObject.get("license");
		String license = null;
		if(o!= null) {
			license = o.toString().replace("\\", "");
		}		
		o = jsonObject.get("subsets");
		String subsets = null;
		if(o != null) {
			subsets = o.toString().replace("\\", "");
		}
		return new KN(name, target, license, subsets, desc);
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
	
	public void print(PrintStream out) {
		out.println("*****************************************************");
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

	public void printURL(PrintStream out) {
		out.println(getName());
	}
}
