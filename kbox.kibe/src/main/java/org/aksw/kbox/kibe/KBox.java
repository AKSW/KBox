package org.aksw.kbox.kibe;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.aksw.kbox.kibe.exception.KBNotFoundException;
import org.aksw.kbox.kibe.exception.KBNotResolvedException;
import org.aksw.kbox.kibe.stream.DefaultInputStreamFactory;
import org.aksw.kbox.kibe.stream.InputStreamFactory;
import org.aksw.kbox.kibe.tdb.TDB;
import org.aksw.kbox.kibe.utils.ZIPUtil;
import org.aksw.kbox.kns.KNSServerListVisitor;
import org.aksw.kbox.kns.KNSTable;
import org.aksw.kbox.kns.ServerAddress;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

/**
 * 
 * @author {@linkplain http://emarx.org}
 *
 */
public class KBox extends org.aksw.kbox.kns.KBox {
		
	public final static String KB_COMMAND_SEPARATOR = ",";
	
	/**
	 * Install the given knowledge base.
	 * The installation try to resolve the knowledge graph by the available KNS.
	 * 
	 * @param knowledgeGraph the URL of the knowledge graph.
	 * 
	 * @throws Exception if any error occurs during the operation.
	 */
	public static void installKB(URL knowledgeGraph) throws Exception {
		URL sourceURL = resolve(knowledgeGraph);
		if(sourceURL == null) {
			throw new KBNotFoundException("Knowledge base " + knowledgeGraph.toString() + " can not be resolved.");
		}
		installKB(sourceURL, knowledgeGraph);
	}

	/**
	 * Install a given index file in the given knowledge graph.
	 * 
	 * @param target the name of the target knowledge graph whereas the index will be installed.
	 * @param source the InputStream index file to be installed.
	 * @throws IOException 
	 */
	public static void installKB(InputStream source, URL target) throws Exception {
		install(source, target, new KBInstall());
	}

	/**
	 * Install a given index file in the given knowledge graph.
	 * 
	 * @param source the URL of the index file to be installed.
	 * @param target the URL of the knowledge graph whereas the index will be installed.
	 * 
	 * @throws Exception if any error occurs during the indexing process.
	 */
	public static void installKB(URL source, URL target) throws Exception {
		install(source, target, new KBInstall());
	}
	
	/**
	 * Install a given knowledge graph resolved by a given KNS service.
	 * 
	 * @param knowledge graph the URL of the knowledge graph that is going to be installed.
	 * @param knsServer the URL of the KNS service that will be used to lookup.
	 * @param streamListener the listener to be invoked when the knowledge graph get dereferenced.
	 * 
	 * @throws Exception if any error occurs during the indexing process.
	 * @throws KBNotResolvedException if the given knowledge base can not be resolved.
	 */
	public static void installKBFromKNSServer(URL dataset, URL knsServer) throws KBNotFoundException, Exception {
		URL file = KNSTable.resolve(dataset, knsServer);
		if(file == null) {
			throw new KBNotResolvedException(dataset.toString());
		}
		installKB(dataset, file);
	}
	
	/**
	 * Create index on the given file with the given RDF files.
	 * 
	 * @param indexFile destiny file to store the index.
	 * @param filesToIndex the files containing the knowledge graph to be indexed.
	 * 
	 * @throws Exception if any error occurs during the indexing process.
	 */
	public static void createIndex(File indexFile, URL[] filesToIndex) throws Exception {
		Path indexDirPath = Files.createTempDirectory("kb");
		File indexDirFile = indexDirPath.toFile();
		TDB.bulkload(indexDirFile.getPath(), filesToIndex);
		ZIPUtil.zip(indexDirFile.getAbsolutePath(), indexFile.getAbsolutePath());
	}

	/**
	 * Query the given knowledge graphs.
	 * 
	 * @param sparql the SPARQL query.
	 * @param install if true the knowledge graph is auto-dereferenced when not found or not otherwise.
	 * @param knowledgeGraphNames the Knowledge Graph Names to be queried.
	 * @param streamListener the StreamListener to be invoked in case a Knowledge graph is dereferenced.
	 * 
	 * @return a result set with the given query solution.
	 * @throws Exception if any of the given knowledge graphs can not be found.
	 */
	public static ResultSet query(String sparql, boolean install, InputStreamFactory factory, URL... knowledgeGraphNames) throws Exception {
		String[] knowledgeGraphsPaths = getResources(install, factory, knowledgeGraphNames);		
		return TDB.query(sparql, knowledgeGraphsPaths);
	}
	
	public static Model createModel(boolean install, InputStreamFactory factory, URL... knowledgeGraphNames) throws Exception {
		String[] knowledgeGraphsPaths = getResources(install, factory, knowledgeGraphNames);
		return TDB.createModel(knowledgeGraphsPaths);
	}
	
	private static String[] getResources(boolean install, InputStreamFactory factory, URL... urls) throws Exception {
		String[] knowledgeGraphsPaths = new String[urls.length];
		int i = 0;
		for(URL knowledgeGraph : urls) {
			File localDataset = locateKB(knowledgeGraph);
			if(localDataset == null && install) {
				URL kbSource = resolve(knowledgeGraph);
				try(InputStream is = factory.get(kbSource)) {
					installKB(is, knowledgeGraph);
				}
				localDataset = locateKB(knowledgeGraph);
			} else if(localDataset == null){
				KBNotFoundException e = new KBNotFoundException("Knowledge graph " + knowledgeGraph.toString() + " does not exist."
						+ " You can install it using the command install.");
				throw e;
			}
			knowledgeGraphsPaths[i] = localDataset.getAbsolutePath();
			i++;
		}
		return knowledgeGraphsPaths;
	}
	
	/**
	 * Create a Model with a given Knowledge Graph Names.
	 * 
	 * @param knowledgeGraphNames the Knowledge Graph Names to be queried.
	 *        Warning: This method automatically dereference Knowledge Graphs. 
	 * 
	 * @return a Model containing the Knowledge Graphs.
	 * 
	 * @throws Exception if some error occurs during the knowledge graph 
	 *         dereference or model instantiation. 
	 */
	public static Model createModel(URL... knowledgeGraphNames) throws Exception {
		return createModel(true, new DefaultInputStreamFactory(), knowledgeGraphNames);
	}
	
	/**
	 * Query a given model.
	 * 
	 * @param sparql the SPARQL query that will be used to query.
	 * @param model the Model that will be queried.
	 * 
	 * @return a ResultSet containing the result to a given query.	 * 
	 * @throws Exception if some error occurs during the query execution.
	 */
	public static ResultSet query(String sparql, Model model) throws Exception {
		return TDB.query(sparql, model);
	}
	
	/**
	 * Query a given Query a given KBox service endpoints.
	 * 
	 * @param sparql the SPARQL query that will be used to query.
	 * @param url the URL of the service.
	 * 
	 * @return a ResultSet containing the result to a given query
	 * @throws Exception if some error occurs during the query execution.
	 */
	public static ResultSet query(String sparql, ServerAddress service) throws Exception {
		return TDB.queryService(sparql, service);
	}

	/**
	 * Query the given knowledge graphs.
	 * 
	 * @param sparql the SPARQL query.
	 * @param knowledgeGraphNames the Knowledge Graph Names to be queried.
	 * 
	 * @return a result set with the given query solution. 
	 * @throws Exception if any of the given knowledge graphs can not be found.
	 */
	public static ResultSet query(String sparql, URL... knowledgeGraphNames) throws Exception {
		Model model = createModel(true, new DefaultInputStreamFactory(), knowledgeGraphNames);
		return query(sparql, model);
	}
	
	/**
	 * Returns a slice of the given knowledge graph representing by the Construct or Describe query.
	 * 
	 * @param sparql a Construct or Describe query.
	 * @param knowledgeGraphNames the Knowledge Graph Names that are going to be sliced.
	 * 
	 * @return a result set with the given query solution. 
	 * @throws Exception if any of the given knowledge graphs can not be found.
	 */
	public static ResultSet slice(String sparql, URL... knowledgeGraphNames) throws Exception {
		Model model = createModel(true, new DefaultInputStreamFactory(), knowledgeGraphNames);
		return query(sparql, model);
	}

	/**
	 * Returns the local Knowledge Graph directory.
	 * 
	 * @param url that will be used to locate the Knowledge Graph
	 * 
	 * @return the local mirror directory of the knowledge Graph.
	 *  
	 * @throws Exception if any error occurs while locating the Knowledge Graph. 
	 */
	public static File locateKB(URL url) throws Exception {
		KBLocate kbLocate = new KBLocate();
		return locate(url, kbLocate);
	}
	
	/**
	 * Resolve the given URL in the available KNS.
	 * The first KNS to be checked is the default KNS, 
	 * thereafter the user's KNS.
	 * 
	 * @param resourceURL the URL to be resolved by the KNS.
	 * 
	 * @return the resolved URL.
	 * 
	 * @throws IOException if any error occurs during the operation.
	 */
	public static URL resolve(URL resourceURL) throws Exception {
		KibeKNSServerList kibeKNSServerList = new KibeKNSServerList();
		return resolve(kibeKNSServerList, resourceURL);
	}
		
	/**
	 * Iterate over all available KNS services with a given visitor.
	 * 
	 * @param KNSVisitor an implementation of KNSVisitor.
	 * 
	 * @throws IOException if any error occurs during the operation.
	 */
	public static void visitALLKNSServers(KNSServerListVisitor visitor) throws Exception {
		KibeKNSServerList kibeKNSServerList = new KibeKNSServerList();
		kibeKNSServerList.visit(visitor);
	}
}
