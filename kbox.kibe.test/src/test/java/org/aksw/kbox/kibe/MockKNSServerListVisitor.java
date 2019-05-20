package org.aksw.kbox.kibe;

import java.util.List;
import java.util.ArrayList;

import org.aksw.kbox.kns.KNSServer;
import org.aksw.kbox.kns.KNSServerListVisitor;

public class MockKNSServerListVisitor implements KNSServerListVisitor {

	private List<KNSServer> knsServers = new ArrayList<>();
	
	@Override
	public boolean visit(KNSServer object) throws Exception {
		knsServers.add(object);
		return true;
	}
	
	public int getVisits() {
		return knsServers.size();
	}
	
	public List<KNSServer> getServers() {
		return knsServers;
	}

}
