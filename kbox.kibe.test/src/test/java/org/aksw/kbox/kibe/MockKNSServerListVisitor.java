package org.aksw.kbox.kibe;

import org.aksw.kbox.appel.KNSServerListVisitor;

public class MockKNSServerListVisitor implements KNSServerListVisitor {

	private int i = 0;
	
	@Override
	public boolean visit(String param) throws Exception {
		i++;
		return true;
	}
	
	public int getVisits() {
		return i;
	}

}
