package org.aksw.kbox.kibe;

import java.net.URL;

import org.aksw.kbox.kns.CustomParamKNSServerList;
import org.aksw.kbox.kns.KNSServer;
import org.aksw.kbox.kns.KNSServerListVisitor;

public class DefaultKNSServerList extends CustomParamKNSServerList {
	
	// Default KNS table URL
	private final static String DEFAULT_KNS_TABLE_URL = "https://raw.github.com/AKSW/kbox/master";

	/**
	 * 
	 */
	private static final long serialVersionUID = 2837993975272090928L;
	
	@Override
	public boolean visit(KNSServerListVisitor visitor) throws Exception {
		KNSServer knsServer = new KNSServer(new URL(DEFAULT_KNS_TABLE_URL));
		boolean keep = visitor.visit(knsServer);
		if(keep) {
			keep = super.visit(visitor);
		}
		return keep;
	}

}
