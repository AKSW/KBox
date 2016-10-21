package org.aksw.kbox.kibe;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.aksw.kbox.CustomParams;
import org.aksw.kbox.kibe.exception.KBNotFoundException;
import org.aksw.kbox.kibe.tdb.TDB;
import org.aksw.kbox.kibe.utils.ZIPUtil;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;

public class KBox extends org.aksw.kbox.KBox {
	
	final static Logger logger = Logger.getLogger(KBox.class);
	
	// CONSTANTS	
	private final static String VERSION = "v0.0.1-alpha2";
	
	public final static String KNS_FILE_NAME = "kbox.kibe.kns";
	public final static String CONTEXT_NAME = "kbox.kibe";

	public final static String FILE_SERVER_TABLE_FILE_NAME = "table.kns";
	public final static String KB_GRAPH_DIR_NAME = "kbox.kb";
	
	public final static String KB_COMMAND_SEPARATOR = ",";
	
	// Default KNS table URL
	private final static String DEFAULT_KNS_TABLE_URL = "https://raw.github.com/AKSW/kbox/master";

	// COMMANDS	
	private final static String RESOURCE_INSTALL_COMMAND = "-r-install";
	private final static String KB_INSTALL_COMMAND = "-kb-install";
	private final static String KNS_INSTALL_COMMAND = "-kns-install";
	private final static String KNS_REMOVE_COMMAND = "-kns-rm";
	private final static String KNS_SERVER_COMMAND = "-kns-server";
	private final static String KNS_LIST_COMMAND = "-kns-list";
	private final static String KB_LIST_COMMAND = "-kb-list";
	private final static String SPARQL_QUERY_COMMAND = "-sparql";
	private final static String SPARQL_QUERY_JSON_OUTPUT_FORMAT_COMMAND = "-json";
	private final static String GRAPH_COMMAND = "-graph";
	private final static String INDEX_COMMAND = "-index";
	private final static String INSTALL_COMMAND = "-install";
	private final static String CREATE_INDEX_COMMAND = "-createIndex";
	private final static String VERSION_COMMAND = "-version";
	private final static String RESOURCE_DIR_COMMAND = "-r-dir";

	public static void main(String[] args) throws Exception {
		Map<String, String> commands = parse(args);
		if(commands.containsKey(CREATE_INDEX_COMMAND)) {
			String files = commands.get(CREATE_INDEX_COMMAND);
			File indexFile = new File(KB_GRAPH_DIR_NAME);
			logger.info("Creating index.");
			createIndex(indexFile, filePathsToURLs(new File(files).listFiles()));
			logger.info("Index created.");
		} else if(commands.containsKey(RESOURCE_INSTALL_COMMAND) &&
				commands.containsKey(GRAPH_COMMAND)) {
			String url = commands.get(GRAPH_COMMAND);
			URL resourceURL = new URL(url);
			String resource = commands.get(RESOURCE_INSTALL_COMMAND);
			URL file = new URL(resource);
			logger.info("Installing resource " + resource);
			KBox.install(resourceURL, file);
			logger.info("Resource installed.");
		} else if(commands.containsKey(KB_INSTALL_COMMAND) && 
				commands.containsKey(INDEX_COMMAND)) {
			String kbURL2Install = commands.get(KB_INSTALL_COMMAND);
			URL kbURL = new URL(kbURL2Install);
			String resource = commands.get(INDEX_COMMAND);
			File resourceFile = new File(resource);
			URL file = resourceFile.toURI().toURL();
			logger.info("Installing KB " + kbURL2Install);
			KBox.installKB(kbURL, file);
			logger.info("KB installed.");
		}  else if(commands.containsKey(KB_INSTALL_COMMAND)
				&& commands.containsKey(KNS_SERVER_COMMAND)) {
			String knsServer = commands.get(KNS_SERVER_COMMAND);
			String kbURL = commands.get(KB_INSTALL_COMMAND);
			logger.info("Installing KB " + kbURL + " from KNS " + knsServer);
			KBox.installKBFromKNSServer(new URL(kbURL), new URL(knsServer));
			logger.info("KB installed.");
		} else if(commands.containsKey(KB_INSTALL_COMMAND)) {
			String url = commands.get(KB_INSTALL_COMMAND);
			URL kbURL = new URL(url);
			URL file = resolveURL(kbURL);
			if(file == null) {
				logger.info("KB could not be located in the available KNS services.");
			} else {
				logger.info("Installing KB " + url);
				KBox.installKB(kbURL, file);
				logger.info("KB installed.");
			}
		} else if(commands.containsKey(KNS_INSTALL_COMMAND)) {
			String url = commands.get(KNS_INSTALL_COMMAND);
			URL knsURL = new URL(url);
			logger.info("Installing KNS " + url);
			KBox.installKNS(knsURL);
			logger.info("KNS installed.");
		} else if (commands.containsKey(SPARQL_QUERY_COMMAND) && 
				commands.containsKey(GRAPH_COMMAND)){
			String sparql = commands.get(SPARQL_QUERY_COMMAND);
			String graphNamesList = commands.get(GRAPH_COMMAND);
			String[] graphNames = graphNamesList.split(KB_COMMAND_SEPARATOR);
			boolean install = commands.containsKey(INSTALL_COMMAND);
			URL[] urls = new URL[graphNames.length];
			for(int i=0; i < graphNames.length; i++) {
				urls[i]  = new URL(graphNames[i]);
			}
			try {
				ResultSet rs = query(sparql, install, urls);
				if(commands.containsKey(SPARQL_QUERY_JSON_OUTPUT_FORMAT_COMMAND)) {
					ResultSetFormatter.outputAsJSON(System.out, rs);
				} else {
					ResultSetFormatter.out(System.out, rs);
				}
			} catch (KBNotFoundException e) {
				logger.error("Knowledge graph not available", e);
				logger.info("You can install it by executing the command -kb-install or "
						+ "execute the query command adding -install paramenter.");
			} catch (Exception e) {
				logger.info("Error exectuting query.", e);
			}
		} else if (commands.containsKey(KNS_LIST_COMMAND)) {
			logger.info("KNS table list");
			Iterable<String> it = listAvailableKNS();
			for(String knsServer : it) {
				logger.info("\t - " + knsServer);
			}
		} else if (commands.containsKey(KB_LIST_COMMAND)) {
			logger.info("Knowledge graphs table list");
			Iterable<String> it = listAvailableKNS();
			printKB(new URL(DEFAULT_KNS_TABLE_URL));
			for(String knsServer : it) {
				printKB(new URL(knsServer));
			}
		} else if (commands.containsKey(KNS_REMOVE_COMMAND)) {
			String knsURL = commands.get(KNS_REMOVE_COMMAND);
			removeKNS(new URL(knsURL));
			logger.info("KNS removed.");
		} else if (commands.containsKey(RESOURCE_DIR_COMMAND)) {
			String resourceDir = commands.get(RESOURCE_DIR_COMMAND);
			if(resourceDir != null) {
				try {
					setResourceFolder(resourceDir);
					logger.info("Resource directory redirected to " + resourceDir + ".");
				} catch (Exception e) {
					logger.error("Error changing KBox resource repository." + resourceDir + ".", e);
				}
			} else {
				resourceDir = getResourceFolder();
				logger.info("Your current Resource directory is: " + resourceDir);					
			}			
		} else if (commands.containsKey(VERSION_COMMAND)) {
			printVersion();
		} else {
			printHelp();
		}
	}
	
	public static void printVersion() {
		System.out.println("KBox version " + VERSION);
	}
	
	public static void printHelp() {
		System.out.println("knox.jar <command> [option]");
		System.out.println("Where [command] is:");
		System.out.println("   * -createIndex <directory> \t - Create an index with the files in a given directory.");
		System.out.println("                              \t ps: the directory might contain only RDF compatible file formats.");
		System.out.println("   * -sparql <query> -graph <graph> [-install] \t - Query a given graph (e.g. -sparql \"Select ...\" -graph \"graph1,graph2\")");
		System.out.println("                                               \t - ps: use -install in case you want to enable the auto-dereference.");	
		System.out.println("   * -kns-list \t - List all availables KNS services.");
		System.out.println("   * -kns-install <kns-URL> \t - Install a given KNS service.");
		System.out.println("   * -kns-remove <kns-URL> \t - Remove a given KNS service.");	
		System.out.println("   * -r-install  <URL> \t - Install a given resource in KBox.");
		System.out.println("   * -kb-install  <kb-URL> \t - Install a given knowledge graph using the available KNS services to resolve it.");
		System.out.println("   * -kb-install  <kb-URL> -index <indexFile> \t - Install a given index in a given knowledge graph URL.");
		System.out.println("   * -kb-install  <kb-URL> -kns-server <kns-server-URL> \t - Install a knowledge graph from a a given KNS server.");
		System.out.println("   * -kb-list \t - List all available KNS services and knowledge graphs.");
		System.out.println("   * -r-dir <resourceDir>\t - Change the current path of the KBox resource container.");
		System.out.println("   * -version \t - display KBox version.");
	}
	
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
	 * @param url the URL of the KNS (Knwoledge Name Service)
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
	 * Command line parser.
	 * 
	 * @param args a set o arguments received by command line
	 * @return a Map containing the parsed arguments
	 */
	public static Map<String, String> parse(String[] args) {
		Map<String, String> map = new HashMap<String, String>();
		for(int i = 0; i < args.length ; i++) {
			if(args[i].startsWith("-")) { // is a command				
				if(i+1 < args.length && !args[i+1].startsWith("-")) {
					map.put(args[i], args[i+1]);
				} else {
					map.put(args[i], null);
				}
			}
		}
		return map;
	}
	
	
	public static URL[] filePathsToURLs(File[] files) throws MalformedURLException {
		URL[] urls = new URL[files.length];
		int i = 0;
		for(File file : files) {
			urls[i] = file.toURI().toURL();
			i++;
		}		
		return urls;
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
	public static URL printKB(URL knsURL) throws IOException {
		URL tableURL = new URL(knsURL.toString() + "/" + FILE_SERVER_TABLE_FILE_NAME);
		InputStream is = tableURL.openStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		String line = null;
		while((line = in.readLine()) != null) {
		    KNS kns = null;
			try {
				kns = KNS.parse(line);
				System.out.println("*****************************************************");
				System.out.println("KNS:" + knsURL.toString());
				System.out.println("KB:" + kns.getName());
				System.out.println("DESC:" + kns.getDesc());
			} catch (Exception e) {
				logger.error("KNS Table entry could not be parsed: " + line, e);
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
	 * @param knowledge graph the URL of the knowledge graph
	 * @throws IOException if any error occurs during the operation.
	 */
	public static void installKB(URL knowledgeFraph) throws IOException {
		URL file = resolveURL(knowledgeFraph);
		KBox.installKB(knowledgeFraph, file);
	}

	/**
	 * Install a given index file in the given knowledge graph.
	 * 
	 * @param knowledge graph the URL of the knowledge graph whereas the index will be installed.
	 * @param indexFile the index file to be installed.
	 * @throws IOException
	 */
	public static void installKB(URL dest, URL indexFile) throws IOException {
		File urlDir = KBox.newDir(dest);
		File urlKBDir = new File(urlDir.getAbsoluteFile() + File.separator + KB_GRAPH_DIR_NAME);
		if(!urlKBDir.exists()) {
			urlKBDir.mkdir();
		}
		ZIPUtil.unzip(indexFile.openStream(), urlKBDir.getAbsolutePath());
	}
	
	/**
	 * Install a given knowledge graph resolved by a given KNS service.
	 * 
	 * @param  knowledge graph the URL of the knowledge graph that is going to be installed.
	 * @param knsServer the URL of the KNS service that will be used to lookup.
	 * @throws IOException
	 */
	public static void installKBFromKNSServer(URL dataset, URL knsServer) throws IOException {
		URL file = resolveURL(dataset, knsServer);
		installKB(dataset, file);		
	}
	
	/**
	 * Create index on the given file with the given RDF files.
	 * 
	 * @param indexFile destiny file to store the index.
	 * @param filesToIndex the files containing the knowledge graph to be indexed.
	 * @throws IOException if any error occurs during the indexing process.
	 */
	public static void createIndex(File indexFile, URL[] filesToIndex) throws IOException {
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
	 * * @param knowledgeGraphNames the Knowledge Graph Names to be queried.
	 * @return a result set with the given query solution.
	 * @throws Exception if any of the given knowledge graphs can not be found.
	 */
	public static ResultSet query(String sparql, boolean install, URL... knowledgeGraphNames) throws Exception {
		String[] knowledgeGraphsPaths = new String[knowledgeGraphNames.length];
		int i = 0;
		for(URL knowledgeGraph : knowledgeGraphNames) {
			File localDataset = getResource(knowledgeGraph);
			Exception e = new KBNotFoundException("Dataset " + knowledgeGraph.toString() + " does not exist. You can install it using the command install.");
			if((localDataset == null || !localDataset.exists()) && !install) {
				throw e;
			} else if (localDataset == null || !localDataset.exists()) {
				installKB(knowledgeGraph);
				localDataset = getResource(knowledgeGraph);
			}
			File localDatasetGraph = new File(localDataset.getPath() + File.separator + KB_GRAPH_DIR_NAME);
			if(!localDatasetGraph.exists() && !install) {
				throw e;
			}
			knowledgeGraphsPaths[i] = localDatasetGraph.getAbsolutePath();
			i++;
		}
		return TDB.query(sparql, knowledgeGraphsPaths);
	}
	
	/**
	 * Query the given knowledge graphs.
	 * 
	 * @param sparql the SPARQL query.
	 * @param knowledgeGraphNames the Knowledge Graph Names to be queried.
	 * @return a result set with the given query solution.
	 * @throws Exception if any of the given knowledge graphs can not be found.
	 */
	public static ResultSet query(String sparql, URL... knowledgeGraphNames) throws Exception {		
		return query(sparql, false, knowledgeGraphNames);
	}
}
