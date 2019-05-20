package org.aksw.kbox.kns;

public abstract class AbstractKNSListVisitor implements KNSServerListVisitor {
	private KNSVisitor knServerListVisitor = null;
	
	public AbstractKNSListVisitor () {
		this.knServerListVisitor = new KNServerListVisitor();
	}
	
	public abstract boolean visit(KN kn) throws Exception;
	
	@Override
	public boolean visit(KNSServer server) throws Exception {
		return server.visit(knServerListVisitor);
	}
	
	private class KNServerListVisitor implements KNSVisitor {
		@Override
		public boolean visit(KN kn) throws Exception {			
			return AbstractKNSListVisitor.this.visit(kn);
		}		
	}
}
