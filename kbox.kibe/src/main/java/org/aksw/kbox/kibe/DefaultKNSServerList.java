package org.aksw.kbox.kibe;

import java.net.MalformedURLException;
import java.net.URL;

import org.aksw.kbox.kns.CustomKNSServerList;
import org.aksw.kbox.kns.KNSServerListVisitor;

public class DefaultKNSServerList extends URLKNSServerList {
	
	// Default KNS table URL
	private final static String DEFAULT_KNS_TABLE_URL = "https://raw.githubusercontent.com/AKSW/KBox/master/kns/2.0/";
	
	private CustomKNSServerList customKNSServerList = new CustomKNSServerList();
	
	public DefaultKNSServerList() throws MalformedURLException {
		super(new URL(DEFAULT_KNS_TABLE_URL));
	}

	
	@Override
	public boolean visit(KNSServerListVisitor visitor) throws Exception {
		boolean next = super.visit(visitor);
		if(next) {
			next = customKNSServerList.visit(visitor);
		}
		return next;
	}
}
