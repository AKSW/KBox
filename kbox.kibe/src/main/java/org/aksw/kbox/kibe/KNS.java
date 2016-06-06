package org.aksw.kbox.kibe;

import java.io.Reader;
import java.io.StringReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class KNS {
	private String name;
	private String target;
	private String desc;
	
	public KNS(String name, String target) {
		this.name = name;
		this.target = target;
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
	
	public static KNS parse(String string) throws Exception {
       org.json.simple.parser.JSONParser jsonParser = new JSONParser();
       Reader stringReader = new StringReader(string);
       JSONObject jsonObject = (JSONObject) jsonParser.parse(stringReader);
		String name = (String) jsonObject.get("name");
		String target = (String) jsonObject.get("target");
		return new KNS(name, target);
	}
	
	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}
}
