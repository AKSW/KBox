package org.aksw.kbox.kns;

import java.net.URL;

import org.aksw.kbox.utils.URLUtils;
import org.apache.log4j.Logger;

public class KNSResolverVisitor implements KNSVisitor {
	
	private final static Logger logger = Logger.getLogger(KNSResolverVisitor.class);

	private URL resourceURL = null;
	private KN resolvedKN = null;
	private String format = null;
	private String version = null;

	public KNSResolverVisitor(URL resourceURL) {
		this.resourceURL = resourceURL;
	}

	public KNSResolverVisitor(URL resourceURL, String format, String version) {
		this(resourceURL);
		this.format = format;
		this.version = version;
	}


	public KN getResolvedKN() {
		return resolvedKN;
	}

	@Override
	public boolean visit(KN kn) throws Exception {
		if(kn.equals(resourceURL.toString(), format, version)) {
			   URL target = kn.getTargets().get(0).getURL();
			   if(!URLUtils.checkStatus(target, 404)) {
				   resolvedKN = kn;
				   return false;
			   } else {
				   logger.warn("Invalid KNS entry: (source: " + kn.getName() + ", target: " + target + ")");
			   }
			}
		return true;
	}
}
