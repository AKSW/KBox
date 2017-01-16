package org.aksw.kbox.kibe;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.aksw.kbox.CustomParams;
import org.aksw.kbox.ZipInstall;
import org.aksw.kbox.kibe.exception.KBNotFoundException;
import org.aksw.kbox.kibe.exception.KBNotResolvedException;
import org.aksw.kbox.kibe.stream.DefaultInputStreamFactory;
import org.aksw.kbox.kibe.stream.InputStreamFactory;
import org.aksw.kbox.kibe.tdb.TDB;
import org.aksw.kbox.kibe.utils.ZIPUtil;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

public class KBox extends org.aksw.kbox.KBox {

	private final static Logger logger = Logger.getLogger(KBox.class);
	
	public final static String KNS_FILE_NAME = "kbox.kibe.kns";
	public final static String CONTEXT_NAME = "kbox.kibe";

	public final static String FILE_SERVER_TABLE_FILE_NAME = "table.kns";
	public final static String KB_GRAPH_DIR_NAME = "kbox.kb";
	
	public final static String KB_COMMAND_SEPARATOR = ",";
	
	// Default KNS table URL
	private final static String DEFAULT_KNS_TABLE_URL = "https://raw.github.com/AKSW/kbox/master";	
	
	/**
	 * 
	 * List all available Knowledge Name Services (KNS).
	 * 
	 * @return a Iterable containing all installed Knowledge Name Services.
	 */
	public static Iterable<String> listAvailableKNS() {
		CustomParams cs = new CustomParams(KBox.KBOX_DIR 
				+ File.separator 
				+ KBox.KNS_FILE_NAME,
				KBox.CONTEXT_NAME);
		return cs.iterable();
	}
	
	/**
	 * Install the given URL in your personal Knowledge Name Service.
	 * This service will be used to Lookup Knowledge bases.
	 * 
	 * @param url the URL of the KNS (Knowledge Name Service)
	 */
	public static void installKNS(URL url) {
		CustomParams cs = new CustomParams(KBox.KBOX_DIR 
				+ File.separator 
				+ KBox.KNS_FILE_NAME, 
				KBox.CONTEXT_NAME);
		cs.add(url.toString());
	}
	
	/**
	 * Remove the given URL from your personal Knowledge Name Service.
	 */
	public static void removeKNS(URL url) {
		CustomParams cs = new CustomParams(KBox.KBOX_DIR 
				+ File.separator 
				+ KBox.KNS_FILE_NAME, 
				KBox.CONTEXT_NAME);
		cs.remove(url.toString());
	}
		
	/**
	 * Resolve the given URL in the available KNS.
	 * The first KNS to be checked is the default KNS, 
	 * thereafter the user's KNS.
	 * 
	 * @param resourceURL the URL to be resolved by the KNS.
	 * @return the resolved URL.
	 * @throws IOException if any error occurs during the operation.
	 */
	public static URL resolveURL(URL resourceURL) throws IOException {
		URL url = resolveURL(resourceURL, new URL(DEFAULT_KNS_TABLE_URL));
		if(url != null) {
			return url;
		}
		Iterable<String> userKNSTableList = listAvailableKNS();
		for(String knsServers : userKNSTableList) {
			url = resolveURL(new URL(knsServers), resourceURL);
			if(url != null) {
				return url;
			}
		}
		return null;
	}
	
	/**
	 * Resolve a given resource with by the given KNS service.
	 * 
	 * @param knsURL the URL of KNS server that will resolve the given URL.
	 * @param resourceURL the URL of the resource that will be resolved by the given KNS service.
	 * @return the resolved URL.
	 * @throws IOException if any error occurs during the operation.
	 */
	public static URL resolveURL(URL resourceURL, URL knsURL) throws IOException {
		URL tableURL = new URL(knsURL.toString() + "/" + FILE_SERVER_TABLE_FILE_NAME);
		InputStream is = tableURL.openStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		String line = null;
		while((line = in.readLine()) != null) {
		    KNS kns = null;
			try {
				if(!line.isEmpty()) {
					kns = KNS.parse(line);
					if(kns.getName().equals(resourceURL.toString())) {
					   return new URL(kns.getTarget());
					}
				}
			} catch (Exception e) {
				logger.error("KNS Table entry could not be parsed: " + line, e);
			}
		}
		return null;
	}
	
	/**
	 * Install the given knowledge base.
	 * The installation try to resolve the knowledge graph by the available KNS.
	 * 
	 * @param knowledgeGraph the URL of the knowledge graph.
	 * 
	 * @throws Exception if any error occurs during the operation.
	 */
	public static void installKB(URL knowledgeGraph) throws Exception {
		URL urlSource = resolveURL(knowledgeGraph);
		if(urlSource == null) {
			throw new KBNotFoundException("Knowledge base " + knowledgeGraph.toString() + " can not be resolved.");
		}
		installKB(knowledgeGraph, urlSource);
	}

	/**
	 * Install a given index file in the given knowledge graph.
	 * 
	 * @param target the name of the target knowledge graph whereas the index will be installed.
	 * @param source the InputStream index file to be installed.
	 * @throws IOException 
	 */
	public static void installKB(URL target, InputStream source) throws Exception {
		URL finalDest = new URL(target.toString() + "/" + KB_GRAPH_DIR_NAME);
		install(source, finalDest, new ZipInstall());
	}
	
	/**
	 * Install a given index file in the given knowledge graph.
	 * 
	 * @param target the URL of the knowledge graph whereas the index will be installed.
	 * @param source the URL of the index file to be installed.
	 * 
	 * @throws Exception if any error occurs during the indexing process.
	 */
	public static void installKB(URL target, URL source) throws Exception {
		installKB(target, source.openStream());
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
		URL file = resolveURL(dataset, knsServer);
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
			URL databaseURL = new URL(knowledgeGraph.toString() + "/" + KB_GRAPH_DIR_NAME);
			File localDataset = getResource(databaseURL);
			if(localDataset == null && install) {
				URL kbSource = resolveURL(knowledgeGraph);
				installKB(knowledgeGraph, factory.get(kbSource));
				localDataset = getResource(databaseURL);
			} else if(!install){
				Exception e = new KBNotFoundException("Knowledge graph " + knowledgeGraph.toString() + " does not exist."
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
	 *         dereference or model instatiation. 
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
}
