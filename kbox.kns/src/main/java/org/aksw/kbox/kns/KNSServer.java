package org.aksw.kbox.kns;

import java.io.IOException;
import java.net.URL;

public abstract class KNSServer {
	
	private URL serverURL = null;
	
	public KNSServer(URL url) throws Exception {
		this.serverURL = url;
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
	public abstract boolean visit(KNSVisitor visitor) throws Exception;
	
	public URL getURL() {
		return serverURL;
	}
}
