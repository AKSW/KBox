package org.aksw.kbox.kibe;

import org.aksw.kbox.kns.AbstractKNSListVisitor;
import org.aksw.kbox.kns.KN;

public class InfoKBKNSVisitor extends AbstractKNSListVisitor {
	
	private String kbName = null;
	private String format = null;
	private String version = null;
	
	public InfoKBKNSVisitor(String kbName) {
		this.kbName = kbName;
	}
	
	public InfoKBKNSVisitor(String kbName, String format, String version) {
		this.kbName = kbName;
		this.format = format;
		this.version = version;
	}

	@Override
	public boolean visit(KN kn) {
		if(kn.equals(kbName, format, version)) {
			if (!JSONSerializer.getInstance().getIsJsonOutput()) {
				kn.print(System.out);
				System.out.println("__________________________________________");
			} else {
				JSONSerializer.getInstance().setVisitedKN(kn);
			}
		}
		return true;
	}

}
