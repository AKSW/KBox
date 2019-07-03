package org.aksw.kbox.kibe;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.aksw.kbox.kns.KN;
import org.aksw.kbox.kns.KNSServer;
import org.aksw.kbox.kns.KNSVisitor;
import org.aksw.kbox.kns.ServerAddress;
import org.aksw.kbox.kns.Source;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

/**
 * 
 * @author emarx
 * 
 * TODO: Implement multi-target sources
 *
 */
public class SPARQLKNSServer extends KNSServer {

	private Map<String, String> paramMap = null;
	private ServerAddress serverAddress = null;
	private String sparql = null;
	
	public SPARQLKNSServer(String endpoint, String sparql, Map<String, String> paramMap) throws Exception {
		super(new URL(endpoint));
		this.serverAddress = new ServerAddress(endpoint);
		this.paramMap = paramMap;
		this.sparql = sparql;
	}

	@Override
	public boolean visit(KNSVisitor knsVisitor) throws Exception {		
		ResultSet result = KBox.query(sparql, serverAddress);
		while(result.hasNext()) {
			KN kn = new KN();
			kn.setKNS(serverAddress.getURL());
			QuerySolution qe = result.next();
			for(String varMapping : paramMap.keySet()) {
				String var = paramMap.get(varMapping);
				String varValue = qe.get(var).toString();
				Source t = new Source();
				List<Source> targets = new ArrayList<Source>();
				if(KN.NAME.equals(varMapping)) {
					kn.setName(varValue);
				} else if(KN.LICENSE.equals(varMapping)) {
					kn.setLicense(varValue);
				} else if(KN.PUBLISHER.equals(varMapping)) {
					kn.setPublisher(varValue);
				} else if(KN.OWNER.equals(varMapping)) {
					kn.setOwner(varValue);
				} else if(KN.SOURCE.equals(varMapping)) {
					t.setURL(new URL(varValue));
				} else if(KN.FORMAT.equals(varMapping)) {
					kn.setFormat(varValue);
				} else if(KN.VERSION.equals(varMapping)) {
					kn.setVersion(varValue);
				} else if(KN.DESC.equals(varMapping)) {
					kn.setDesc(varValue);
				} else if(KN.INSTALL.equals(varMapping)) {
					t.setInstall(varValue);
				}
				targets.add(t);
				kn.setTargets(targets);
			}
			knsVisitor.visit(kn);
		}
		return false;
	}
	
	

}
