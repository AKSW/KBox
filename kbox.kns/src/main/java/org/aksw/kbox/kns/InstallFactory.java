package org.aksw.kbox.kns;

import java.util.HashMap;
import java.util.Map;

import org.aksw.kbox.apple.Install;
import org.aksw.kbox.apple.ResourceAppInstall;

public class InstallFactory {

	Map<String, Install> methods = new HashMap<String, Install>();

	public Install get(RN kn) {
		String decoder = kn.getEncoder();
		if(decoder == null) {
			return new ResourceAppInstall();
		}
		decoder = decoder.toLowerCase();
		return methods.get(decoder);
	}
	
	public void put(String id, Install install) {
		methods.put(id, install);
	}
	
	public Install get(String format) {
		return methods.get(format);
	}

}
