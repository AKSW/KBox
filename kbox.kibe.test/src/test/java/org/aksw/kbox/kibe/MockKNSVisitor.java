package org.aksw.kbox.kibe;

import java.util.ArrayList;
import java.util.List;

public class MockKNSVisitor implements KNSVisitor {
	
	private List<KN> visited = new ArrayList<KN>();

	@Override
	public void visit(KN kn) {
		this.visited.add(kn);
	}
	
	public List<KN> getKNSVisitedList() {
		return visited;
	}

}
