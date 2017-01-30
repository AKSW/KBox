package org.aksw.kbox.kibe;

public class SearchKBKNSVisitor extends ListKNSVisitor {
	
	private String kbNamePattern = null;
	
	public SearchKBKNSVisitor(String kbNamePattern) {
		this.kbNamePattern = kbNamePattern;
	}

	@Override
	public void visit(KN kn) {
		if(kn.getName().contains(kbNamePattern)) {
			print(System.out, kn);
		}
	}

}
