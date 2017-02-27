package org.aksw.kbox.kns;

import java.io.IOException;
import java.net.URL;

public class KNSSever {
	
	private KNSTable table = null;
	
	public KNSSever(URL url) throws Exception {
		this.table = new KNSTable(url);
	}
	
	/**
	 * Iterate over all Knowledge Names (KNs).
	 * 
	 * @param visitor a KNSVisitor.
	 * @throws IOException if any error occurs during the operation.
	 */
	public void visit(KNSVisitor visitor) throws IOException {
		visit(table, visitor);
	}
	
	/**
	 * Iterate over all Knowledge Names (KNs) of a given Knowledge Name Service (KNS).
	 * 
	 * @param knsServerURL a KNSTable URL.
	 * @param visitor a KNSVisitor.
	 * 
	 * @throws IOException if any error occurs during the operation.
	 */
	public static void visit(URL knsServerURL, KNSVisitor visitor) throws IOException {
		KNSTable table = new KNSTable(knsServerURL);
		visit(table, visitor);
	}
	
	/**
	 * Iterate over all Knowledge Names (KNs) of a given Knowledge Name Service (KNS).
	 * 
	 * @param table a KNSTable.
	 * @param visitor a KNSVisitor.
	 * 
	 * @throws IOException if any error occurs during the operation.
	 */
	public static void visit(KNSTable table, KNSVisitor visitor) throws IOException {
		table.visit(visitor);
	}
}
