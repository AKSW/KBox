package org.aksw.kbox.kibe;

import java.io.PrintStream;
import java.net.URL;

import org.aksw.kbox.kns.KN;
import org.aksw.kbox.kns.KNSServerListVisitor;
import org.aksw.kbox.kns.KNSSever;
import org.aksw.kbox.kns.KNSVisitor;

public class KNSListVisitor implements KNSVisitor, KNSServerListVisitor {

	@Override
	public boolean visit(KN kn) {
		print(System.out, kn);
		return true;
	}
	
	public void print(PrintStream out, KN kn) {
		kn.print(out);
	}

	@Override
	public boolean visit(String param) throws Exception {
		KNSSever.visit(new URL(param), this);
		return true;
	}
}
