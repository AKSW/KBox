package org.aksw.kbox.kibe;

import org.aksw.kbox.kns.RNService;
import org.aksw.kbox.kns.RNSServerListVisitor;

public class MockKNSServerListVisitor implements RNSServerListVisitor {

	private int i = 0;
	
	@Override
	public boolean visit(RNService object) throws Exception {
		i++;
		return true;
	}
	
	public int getVisits() {
		return i;
	}

}
