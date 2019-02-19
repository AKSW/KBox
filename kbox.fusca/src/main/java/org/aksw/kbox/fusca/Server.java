package org.aksw.kbox.fusca;

import org.aksw.kbox.fusca.exception.ServerStartException;
import org.apache.jena.fuseki.main.FusekiServer;
import org.apache.jena.query.ARQ;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.tdb.TDBFactory;

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
	private Listener listener = null;
	
	public Server(int port, String pagePath, String subDomain, long timeout, Model model, Listener listener) {
		this(port, pagePath, subDomain, model, listener);
		ARQ.getContext().set(ARQ.queryTimeout, Long.toString(timeout));
	}

	public Server(int port, String pagePath, String subDomain, Model model, Listener listener) {
		this.port = port;
		this.subDomain = subDomain;
		this.pagePath = pagePath;
		Dataset dataset = DatasetFactory.create(model);
		this.dsg = dataset.asDatasetGraph();
		this.listener = listener;
	}
	
	public Server(int port, String pagePath, String subDomain, String datasetPath) {
		this.port = port;
		this.subDomain = subDomain;
		this.pagePath = pagePath;
		this.dsg = TDBFactory.createDatasetGraph(datasetPath);
	}

	public void start() throws ServerStartException {
		try {
			listener.starting();			
			String staticContentDir = pagePath;
			FusekiServer server = FusekiServer.create()
					.add("/" + subDomain, dsg)
					.enablePing(true)
					.enableStats(true)
					.staticFileBase(staticContentDir)
					.port(port)
					.build();
			server.start();
			listener.started();
		} catch (Exception e) {
			throw new ServerStartException("Failed to start the server.", e);
		}
	}
}
