package org.aksw.kbox.kibe;

public class InfoKBKNSVisitor extends ListKNSVisitor {
	
	private String kbName = null;
	
	public InfoKBKNSVisitor(String kbName) {
		this.kbName = kbName;
	}

	@Override
	public void visit(KN kn) {
		if(kn.getName().equals(kbName)) {
			print(System.out, kn);
		}
	}

}
