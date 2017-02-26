package org.aksw.kbox.kns;

import java.io.File;

import org.aksw.kbox.CustomParams;
import org.aksw.kbox.KBox;

public class KNSServerList extends CustomParams {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3697460272426263415L;
	
	public final static String CONTEXT_NAME = "kbox.kns";
	public final static String KNS_FILE_NAME = CONTEXT_NAME;	
	
	public KNSServerList(String path, String contex) {
		super(path 
				+ File.separator
				+ KNS_FILE_NAME, 
				CONTEXT_NAME);
	}
	
	public KNSServerList() {
		super(KBox.KBOX_DIR
				+ File.separator
				+ KNS_FILE_NAME,
				CONTEXT_NAME);
	}
	
	public void visit(KNSServerListVisitor visitor) throws Exception {
		super.visit(visitor);
	}
}
