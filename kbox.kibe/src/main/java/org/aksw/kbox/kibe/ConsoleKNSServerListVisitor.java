package org.aksw.kbox.kibe;

import java.io.PrintStream;

import org.aksw.kbox.kns.AbstractKNSListVisitor;
import org.aksw.kbox.kns.KN;
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
		this.toCompare = new String[][]{{KN.FORMAT, format}};
	}
	
	public ConsoleKNSServerListVisitor(String format, String version, boolean pagination) {
		this(format, pagination);
		AssertionUtils.notNull(new IllegalArgumentException("version"), version);
		this.toCompare = new String[][]{{KN.FORMAT, format}, 
			{KN.VERSION, version}};
	}
	
	public void print(PrintStream out, KN kn) {
		AssertionUtils.notNull(new IllegalArgumentException("out"), out);
		AssertionUtils.notNull(new IllegalArgumentException("kn"), kn);
		if (!JSONSerializer.getInstance().getIsJsonOutput()) {
			if (lineNumber == 0) {
				out.println("KBox KNS Resource table list");
				out.println("##############################");
				out.println("name,format,version");
				out.println("##############################");
			}
			out.println(kn.getName() + "," + kn.getFormat() + "," + kn.getVersion());
			lineNumber++;
			if (pagination && (lineNumber % 40) == 0) {
				System.console().format("\nPress Enter to continue...");
				System.console().readLine();
			}
		} else {
			JSONSerializer.getInstance().addVisitedKN(kn.getName() + "," + kn.getFormat() + "," + kn.getVersion());
		}
	}

	public boolean visit(KN kn) throws Exception {
		AssertionUtils.notNull(new IllegalArgumentException("kn"), kn);
		if(toCompare == null || KN.equals(kn, toCompare)) {
			print(System.out, kn);
		}
		return true;
	}
}
