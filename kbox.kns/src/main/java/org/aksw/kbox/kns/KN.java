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
	
	public KN(String name, String target, String desc) {
		this.name = name;
		this.target = target;
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
		String desc = (String) jsonObject.get("desc");
		return new KN(name, target, desc);
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
	
	public void print(PrintStream out) {
		out.println("*****************************************************");
		out.println("KNS:" + getKNS());
		out.println("KB:" + getName());
		out.println("DESC:" + getDesc());
	}
}
