package org.aksw.kbox.kns;

import java.util.HashMap;
import java.util.Map;

import org.aksw.kbox.apple.AppInstall;
import org.aksw.kbox.apple.ResourceAppInstall;

public class InstallFactory {

	private Map<String, AppInstall> methods = new HashMap<String, AppInstall>();

	public AppInstall get(KN kn) {
		String decoder = kn.getTargets().get(0).getInstall();
		if(decoder == null) {
			return new ResourceAppInstall();
		}
		decoder = decoder.toLowerCase();
		return methods.get(decoder);
	}
	
	public void put(String id, AppInstall install) {
		methods.put(id, install);
	}
	
	public AppInstall get(String format) {
		return methods.get(format);
	}

}
