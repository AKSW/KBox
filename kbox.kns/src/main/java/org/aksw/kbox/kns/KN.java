package org.aksw.kbox.kns;

import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class KN extends KNComparator {
	
	public static final String NAME = "name";
	public static final String SOURCE = "source";
	public static final String URL = "url";
	public static final String TARGET = "target";
	public static final String DESC = "description";
	public static final String KNS = "kns";
	public static final String LICENSE = "license";
	public static final String SUBSETS = "subsets";
	public static final String FORMAT = "format";
	public static final String VERSION = "version";
	public static final String PUBLISHER = "publisher";
	public static final String OWNER = "owner";
	public static final String TAGS = "tags";
	public static final String CHECKSUM = "checksum";
	public static final String DATAID = "dataId";
	public static final String INSTALL = "install";
	public static final String LABEL = "label";
	
	private String name;
	private List<Target> targets;
	private String desc;
	private String kns;
	private String license;
	private String subsets;
	private String format;
	private String version;
	private String publisher;
	private String owner;
	private String label;
	private Set<String> versionTags;
	private Set<String> tags;
	
	public KN() {
	}
	
	public KN(String name, List<Target> targets, String desc) {
		this.name = name;
		this.targets = targets;
		this.desc = desc;
	}
	
	public KN(String name, List<Target> targets, String license, String subsets, String desc) {
		this.name = name;
		this.targets = targets;
		this.license = license;
		this.subsets = subsets;
		this.desc = desc;
	}
	
	public KN(String name, 
			List<Target> targets,
			String format, 
			String version, 
			String license, 
			String subsets, 
			String desc) {
		this.name = name;
		this.targets = targets;
		this.format = format;
	    this.version = version;
		this.license = license;
		this.subsets = subsets;
		this.desc = desc;
	}
	
	public KN(String name, 
			List<Target> targets,
			String format, 
			String version, 
			String license, 
			String subsets, 
			String desc, 
			String publisher) {
		this.name = name;
		this.targets = targets;
		this.format = format;
	    this.version = version;
		this.license = license;
		this.subsets = subsets;
		this.desc = desc;
		this.publisher = publisher;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<Target> getTargets() {
		return targets;
	}
	
	public void setTargets(List<Target> sources) {
		this.targets = sources;
	}
	
	public static KN parse(String json) throws Exception {
		JSONParser jsonParser = new JSONParser();
		Reader stringReader = new StringReader(json);
		JSONObject jsonObject = (JSONObject) jsonParser.parse(stringReader);
		String name = (String) jsonObject.get(NAME);
		JSONArray jsonTarget = (JSONArray) jsonObject.get(TARGET);
		java.util.List<Target> sources = new ArrayList<Target>();
		if(jsonTarget != null) {
			for(int s = 0; s < jsonTarget.size(); s++) {
				JSONObject jsonSource = (JSONObject) jsonTarget.get(s);
				String install = (String) jsonSource.keySet().toArray()[0];
				JSONArray jsonSources = (JSONArray) jsonSource.values().toArray()[0];
				if(jsonSources != null) {
					for(int i= 0; i < jsonSources.size(); i++) {
						JSONObject jsonSourceEntry = (JSONObject)jsonSources.get(i);
						String url = (String)jsonSourceEntry.get(URL);
						JSONObject jsonChecksum = (JSONObject) jsonSourceEntry.get(CHECKSUM);
						Map<String, String> checksum = new HashMap<String, String>();
						if(jsonChecksum != null) {
							for(int c = 0; c < jsonChecksum.keySet().size(); c++) {
								String key = (String) jsonChecksum.keySet().toArray()[c];
								String value = (String) jsonChecksum.values().toArray()[c];
								checksum.put(key, value);
							}
						}
						JSONObject jsonDataId = (JSONObject) jsonSourceEntry.get(DATAID);
						Map<String, String> dataId = new HashMap<String, String>();
						if(jsonDataId != null) {
							for(int di = 0; di < jsonDataId.keySet().size(); di++) {
								String key = (String) jsonDataId.keySet().toArray()[di];
								String value = (String) jsonDataId.values().toArray()[di];
								dataId.put(key, value);
							}
						}
						Target source = new Target();
						source.setInstall(install);
						source.setChecksum(checksum);
						source.setDataId(dataId);
						source.setURL(new URL(url));
						sources.add(source);
					}
				}
			}
		}
		String desc = (String) jsonObject.get(DESC);
		String publisher = (String) jsonObject.get(PUBLISHER);
		String owner = (String) jsonObject.get(OWNER);
		String label = (String) jsonObject.get(LABEL);
		String format = (String) jsonObject.get(FORMAT);
		Object o = jsonObject.get(LICENSE);
		String license = null;
		if(o != null) {
			license = o.toString().replace("\\", "");
		}		
		o = jsonObject.get(SUBSETS);
		String subsets = null;
		if(o != null) {
			subsets = o.toString().replace("\\", "");
		}
		JSONArray jsonAliases = (JSONArray) jsonObject.get(TAGS);
		Set<String> tags = null;
		if(jsonAliases != null) {
			tags = new HashSet<String>();
			for(int i= 0; i < jsonAliases.size(); i++) {
				tags.add((String)jsonAliases.get(i));				
			}
		}
		JSONObject jsonVersion = (JSONObject) jsonObject.get(VERSION);
		String version = null;
		Set<String> versionTags = null;
		if(jsonVersion != null) {
			version = (String) jsonVersion.get(NAME);
			JSONArray jsonVersionTags = (JSONArray) jsonVersion.get(TAGS);
			if(jsonVersionTags != null) {
				versionTags = new HashSet<String>();
				for(int i= 0; i < jsonVersionTags.size(); i++) {
					versionTags.add((String)jsonVersionTags.get(i));			
				}
			}
		}
		
		KN kn = new KN(name, sources, format, version, license, subsets, desc, publisher);
		kn.setOwner(owner);
		kn.setTags(tags);
		kn.setLabel(label);
		kn.setVersionTags(versionTags);
		return kn;
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
	
	public String getPublisher() {
		return publisher;
	}
	
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public boolean equals(String name) {
		return (this.name == name ||
				(this.name != null && this.name.equals(name)));
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
		println(out, "KNS:", kns);
		println(out, "KB:", name);
		println(out, "label:", label);
		println(out, "Description:", desc);
		println(out, "Subsets:", subsets);
		println(out, "License:", license);
		println(out, "Publisher:", publisher);
		println(out, "Owner:", owner);
		println(out, "Format:", format);
		println(out, "Version:", version);
	}
	
	private void println(PrintStream out, String label, String variable) {
		if(variable != null) {
			out.println(label + variable);
		}
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Set<String> getTags() {
		return tags;
	}

	public void setTags(Set<String> tags) {
		this.tags = tags;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Set<String> getVersionTags() {
		return versionTags;
	}

	public void setVersionTags(Set<String> versionTags) {
		this.versionTags = versionTags;
	}	
}
