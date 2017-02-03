package org.aksw.kbox.fusca;

import org.aksw.kbox.fusca.exception.ServerStartException;
import org.apache.jena.fuseki.Fuseki;
import org.apache.jena.fuseki.FusekiException;
import org.apache.jena.fuseki.server.FusekiConfig;
import org.apache.jena.fuseki.server.SPARQLServer;
import org.apache.jena.fuseki.server.ServerConfig;

import com.hp.hpl.jena.query.ARQ;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.tdb.TDBFactory;

/**
 * 
 * @author @author {@linkplain http://emarx.org}
 *
 */
public class Server {
	
	private int port = 3030;
	private DatasetGraph dsg = null;
	private String subDomain = null;
	private String pagePath = null;
	
	public Server(int port, String pagePath, String subDomain, long timeout, Model model) {
		this(port, pagePath, subDomain, model);
		ARQ.getContext().set(ARQ.queryTimeout, Long.toString(timeout));
	}

	public Server(int port, String pagePath, String subDomain, Model model) {
		this.port = port;
		this.subDomain = subDomain;
		this.pagePath = pagePath;
		Dataset dataset = TDBFactory.createDataset();
		dataset.getDefaultModel().add(model);
		this.dsg = dataset.asDatasetGraph();
	}
	
	public Server(int port, String pagePath, String subDomain, String datasetPath) {
		this.port = port;
		this.subDomain = subDomain;
		this.pagePath = pagePath;
		this.dsg = TDBFactory.createDatasetGraph(datasetPath);		
	}

	public void start() throws ServerStartException {
		String staticContentDir = pagePath + Fuseki.PagesStatic;		
		ServerConfig serverConfig = FusekiConfig.defaultConfiguration(subDomain, dsg,
					false, false);
		
		serverConfig.port = port;
		serverConfig.pages = staticContentDir;
		serverConfig.mgtPort = 9090;
		serverConfig.pagesPort = port;
		serverConfig.loopback = false;
		serverConfig.enableCompression = true;
		serverConfig.jettyConfigFile = null;
		serverConfig.authConfigFile = null;
		serverConfig.verboseLogging = false;

		SPARQLServer server = new SPARQLServer(serverConfig);
		Fuseki.setServer(server);
		try {
			server.start();	        
		} catch (FusekiException e) {
			throw new ServerStartException("Failed to start the server.", e);
		}

		try {
			server.getServer().join();
		} catch (Exception e) {			
		}
		System.exit(0);
	}
}
