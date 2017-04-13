package org.aksw.kbox.kibe;

import java.io.PrintStream;

import org.aksw.kbox.kns.AbstractKNSListVisitor;
import org.aksw.kbox.kns.KN;
import org.aksw.kbox.kns.KNSServer;
import org.aksw.kbox.kns.KNSServerListVisitor;
import org.aksw.kbox.kns.KNSSever;
import org.aksw.kbox.kns.KNSVisitor;

public class SystemOutKNSServerListVisitor extends AbstractKNSListVisitor {
	
	private KNSVisitor knServerListVisitor = null;
	
	public SystemOutKNSServerListVisitor () {
		this.knServerListVisitor = new KNServerListVisitor(); 
	}	
	
	public void print(PrintStream out, KN kn) {
		kn.printURL(out);
	}

	@Override
	public boolean visit(KNSServer server) throws Exception {
		KNSSever.visit(server.getURL(), knServerListVisitor);
		return true;
	}

	public boolean visit(KN kn) throws Exception {
		print(System.out, kn);
		return true;
	}
	
	private class KNServerListVisitor implements KNSVisitor {
		@Override
		public boolean visit(KN kn) throws Exception {			
			return visit(kn);
		}		
	}
}
