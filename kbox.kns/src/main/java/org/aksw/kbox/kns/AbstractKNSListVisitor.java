package org.aksw.kbox.kns;

public abstract class AbstractKNSListVisitor implements RNSServerListVisitor {
	private KNSVisitor knServerListVisitor = null;
	
	public AbstractKNSListVisitor () {
		this.knServerListVisitor = new KNServerListVisitor();
	}
	
	public abstract boolean visit(RN kn) throws Exception;
	
	@Override
	public boolean visit(RNService server) throws Exception {
		return KNSSever.visit(server.getURL(), knServerListVisitor);
	}
	
	private class KNServerListVisitor implements KNSVisitor {
		@Override
		public boolean visit(RN kn) throws Exception {			
			return AbstractKNSListVisitor.this.visit(kn);
		}		
	}
}
