package org.aksw.kbox.kns;

import java.io.File;
import java.net.URL;

import org.aksw.kbox.CustomParamVisitor;
import org.aksw.kbox.CustomParams;
import org.aksw.kbox.KBox;

public class CustomRNSServerList extends CustomParams implements RNServerList {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3697460272426263415L;
	
	public final static String CONTEXT_NAME = "kbox.kns";
	public final static String KNS_FILE_NAME = CONTEXT_NAME;
	
	private RNSServerListVisitor visitor = null;
	private boolean next = false;
	private CustomParamKNSServerListVisitor customParamKNSServerListVisitor = null;
	
	public CustomRNSServerList(String path, String contex) {
		super(path 
				+ File.separator
				+ KNS_FILE_NAME, 
				CONTEXT_NAME);
		customParamKNSServerListVisitor = new CustomParamKNSServerListVisitor();
		
	}
	
	public CustomRNSServerList() {
		this(KBox.KBOX_DIR,
				CONTEXT_NAME);
	}
	
	public boolean visit(RNSServerListVisitor visitor) throws Exception {
		this.visitor = visitor;
		return super.visit(customParamKNSServerListVisitor);
	}
	
	private class CustomParamKNSServerListVisitor implements CustomParamVisitor {

		@Override
		public boolean visit(String param) throws Exception {
			RNService knsServer = new RNService(new URL(param));
			next = visitor.visit(knsServer);
			return next;
		}
		
	}
}
