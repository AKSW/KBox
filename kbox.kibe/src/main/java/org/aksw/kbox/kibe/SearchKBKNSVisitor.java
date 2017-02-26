package org.aksw.kbox.kibe;

import org.aksw.kbox.appel.KN;

public class SearchKBKNSVisitor extends KNSListVisitor {
	
	private String kbNamePattern = null;
	
	public SearchKBKNSVisitor(String kbNamePattern) {
		this.kbNamePattern = kbNamePattern;
	}

	@Override
	public boolean visit(KN kn) {
		if(kn.getName().contains(kbNamePattern)) {
			print(System.out, kn);
		}
		return true;
	}
}
