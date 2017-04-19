package org.aksw.kbox.kibe;

import java.io.PrintStream;

import org.aksw.kbox.kns.AbstractKNSListVisitor;
import org.aksw.kbox.kns.KN;

public class SystemOutKNSServerListVisitor extends AbstractKNSListVisitor {
	
	public void print(PrintStream out, KN kn) {
		kn.printURL(out);
	}

	public boolean visit(KN kn) throws Exception {
		print(System.out, kn);
		return true;
	}
}
