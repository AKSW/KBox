package org.aksw.kbox.kibe;

import org.aksw.kbox.kns.RN;
import org.aksw.kbox.kns.RNSServerListVisitor;

public interface KNListVisitor extends RNSServerListVisitor {
	
	public boolean visit(RN kn);
	
}
