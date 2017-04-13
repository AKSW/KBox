package org.aksw.kbox.kibe;

import org.aksw.kbox.kns.KNSServer;
import org.aksw.kbox.kns.KNSServerListVisitor;

public class MockKNSServerListVisitor implements KNSServerListVisitor {

	private int i = 0;
	
	@Override
	public boolean visit(KNSServer object) throws Exception {
		i++;
		return true;
	}
	
	public int getVisits() {
		return i;
	}

}
