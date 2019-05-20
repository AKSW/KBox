package org.aksw.kbox.kns;

import java.io.IOException;
import java.net.URL;

public class KBoxKNSServer extends KNSServer {
	
	private KNSTable table = null;
	
	public KBoxKNSServer(URL url) throws Exception {
		super(url);
		this.table = new KNSTable(url);
	}
	
	/**
	 * Iterate over all Knowledge Names ({@link KN}s).
	 * 
	 * @param visitor a {@link KNSVisitor}.
	 * 
	 * @return true if the {@link KNSVisitor#visit(KN)} method return true and false otherwise.
	 * 
	 * @throws IOException if any error occurs during the operation.
	 */
	public boolean visit(KNSVisitor visitor) throws IOException {
		return visit(table, visitor);
	}
	
	/**
	 * Iterate over all Knowledge Names ({@link KN}s) of a given Knowledge Name Service (KNS).
	 * 
	 * @param knsServerURL the {@link RNService}'s {@link URL}.
	 * @param visitor a {@link KNSVisitor}.
	 * 
	 * @return true if the {@link KNSVisitor#visit(KN)} method return true and false otherwise.
	 * 
	 * @throws {@link IOException} if any error occurs during the operation.
	 */
	public boolean visit(URL knsServerURL, KNSVisitor visitor) throws IOException {
		KNSTable table = new KNSTable(knsServerURL);
		return visit(table, visitor);
	}
	
	/**
	 * Iterate over all Knowledge Names ({@link KN}s) of a given Knowledge Name Service (KNS).
	 * 
	 * @param table a {@link KNSTable}.
	 * @param visitor a {@link KNSVisitor}.
	 * 
	 * @return true if the {@link KNSVisitor#visit(KN)} method return true and false otherwise.
	 * 
	 * @throws {@link IOException} if any error occurs during the operation.
	 */
	public boolean visit(KNSTable table, KNSVisitor visitor) throws IOException {
		return table.visit(visitor);
	}
}
