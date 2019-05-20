package org.aksw.kbox.kibe;

import org.aksw.kbox.kns.KN;

public class SearchKBKNSVisitor extends ConsoleKNSServerListVisitor {
	
	private String kbNamePattern = null;
	private String format = null;
	private String version = null;
	
	public SearchKBKNSVisitor(String kbNamePattern, boolean pagination) {
		super(pagination);
		this.kbNamePattern = kbNamePattern;
	}
	
	public SearchKBKNSVisitor(String kbNamePattern, String format, String version, boolean pagination) {
		super(pagination);
		this.kbNamePattern = kbNamePattern;
		this.format = format;
		this.version = version;
	}

	@Override
	public boolean visit(KN kn) {
		if(kn.contains(kbNamePattern, format, version)) {
			print(System.out, kn);
		}
		return true;
	}
}
