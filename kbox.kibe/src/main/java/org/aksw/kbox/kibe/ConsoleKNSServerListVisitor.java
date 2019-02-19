package org.aksw.kbox.kibe;

import java.io.PrintStream;

import org.aksw.kbox.kns.AbstractKNSListVisitor;
import org.aksw.kbox.kns.RN;
import org.aksw.kbox.utils.AssertionUtils;

public class ConsoleKNSServerListVisitor extends AbstractKNSListVisitor {
	
	private String[][] toCompare = null;
	private int lineNumber = 0;
	private boolean pagination = false;
	
	public ConsoleKNSServerListVisitor(boolean pagination) {
		this.pagination = pagination;
	}
	
	public ConsoleKNSServerListVisitor(String format, boolean pagination) {
		this(pagination);
		AssertionUtils.notNull(new IllegalArgumentException("format"), format);
		this.toCompare = new String[][]{{RN.FORMAT, format}};
	}
	
	public ConsoleKNSServerListVisitor(String format, String version, boolean pagination) {
		this(format, pagination);
		AssertionUtils.notNull(new IllegalArgumentException("version"), version);
		this.toCompare = new String[][]{{RN.FORMAT, format}, 
			{RN.VERSION, version}};
	}
	
	public void print(PrintStream out, RN kn) {
		AssertionUtils.notNull(new IllegalArgumentException("out"), out);
		AssertionUtils.notNull(new IllegalArgumentException("kn"), kn);
		if(lineNumber == 0) {
			out.println("KBox KNS Resource table list");
			out.println("##############################");
			out.println("name,format,version");
			out.println("##############################");
		}
		out.println(kn.getName() + "," + kn.getFormat() + "," + kn.getVersion());
		lineNumber++;
		if(pagination && (lineNumber % 40) == 0) {
			System.console().format("\nPress Enter to continue...");
			System.console().readLine();
		}
	}

	public boolean visit(RN kn) throws Exception {
		AssertionUtils.notNull(new IllegalArgumentException("kn"), kn);
		if(toCompare == null || RN.equals(kn, toCompare)) {
			print(System.out, kn);
		}
		return true;
	}
}
