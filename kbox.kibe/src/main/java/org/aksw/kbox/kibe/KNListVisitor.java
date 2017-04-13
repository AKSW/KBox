package org.aksw.kbox.kibe;

import org.aksw.kbox.kns.KN;
import org.aksw.kbox.kns.KNSServerListVisitor;

public interface KNListVisitor extends KNSServerListVisitor {
	
	public boolean visit(KN kn);
	
}
