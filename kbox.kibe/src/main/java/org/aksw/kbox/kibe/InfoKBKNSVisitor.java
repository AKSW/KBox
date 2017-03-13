package org.aksw.kbox.kibe;

import org.aksw.kbox.kns.KN;

public class InfoKBKNSVisitor extends KNSListVisitor {
	
	private String kbName = null;
	
	public InfoKBKNSVisitor(String kbName) {
		this.kbName = kbName;
	}

	@Override
	public boolean visit(KN kn) {
		if(kn.getName().equals(kbName)) {
			kn.print(System.out);
		}
		return true;
	}

}
