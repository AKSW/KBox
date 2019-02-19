package org.aksw.kbox.kibe;

import java.util.ArrayList;
import java.util.List;

import org.aksw.kbox.kns.RN;
import org.aksw.kbox.kns.KNSVisitor;

public class MockKNSVisitor implements KNSVisitor {
	
	private List<RN> visited = new ArrayList<RN>();

	@Override
	public boolean visit(RN kn) {
		this.visited.add(kn);
		return true;
	}
	
	public List<RN> getKNSVisitedList() {
		return visited;
	}

}
