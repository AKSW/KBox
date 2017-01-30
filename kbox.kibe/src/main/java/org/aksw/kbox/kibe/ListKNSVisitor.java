package org.aksw.kbox.kibe;

import java.io.PrintStream;

public class ListKNSVisitor implements KNSVisitor {

	@Override
	public void visit(KN kn) {
		print(System.out, kn);
	}
	
	public void print(PrintStream out, KN kn) {
		out.println("*****************************************************");
		out.println("KNS:" + kn.getKNS());
		out.println("KB:" + kn.getName());
		out.println("DESC:" + kn.getDesc());
	}

}
