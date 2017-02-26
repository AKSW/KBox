package org.aksw.kbox.kibe;

import org.aksw.kbox.kns.KNSServerList;
import org.aksw.kbox.kns.KNSServerListVisitor;

public class KibeKNSServerList extends KNSServerList {
	
	// Default KNS table URL
	private final static String DEFAULT_KNS_TABLE_URL = "https://raw.github.com/AKSW/kbox/master";

	/**
	 * 
	 */
	private static final long serialVersionUID = 2837993975272090928L;
	
	@Override
	public void visit(KNSServerListVisitor visitor) throws Exception {
		boolean keep = visitor.visit(DEFAULT_KNS_TABLE_URL);
		if(keep) {
			super.visit(visitor);
		}
	}

}
