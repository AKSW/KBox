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
	
	private final static String VERSION = "v0.0.1-alpha";
	
	public final static String KNS_FILE_NAME = "kbox.kibe.kns";
	public final static String CONTEXT_NAME = "kbox.kibe";

	public final static String FILE_SERVER_TABLE_FILE_NAME = "table.kns";
	public final static String KB_GRAPH_DIR_NAME = "kbox.kb";
	
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
	private final static String GRAPH_COMMAND = "-graph";
	private final static String INDEX_COMMAND = "-index";
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
			String graphName = commands.get(GRAPH_COMMAND);
			URL urlGraph = new URL(graphName);
			try {
				ResultSet rs = query(urlGraph, sparql);
				ResultSetFormatter.out(System.out, rs);
			} catch (KBNotFoundException e) {
				logger.info("The knowledgebase " + graphName + " is not available.");
				logger.info("You can install it by executing the command -kb-install.");
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
			logger.info("Knowledgebases table list");
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
		System.out.println("   * -sparql <query> -graph <graph> \t - Query a given graph.");		
		System.out.println("   * -kns-list \t - List all availables KNS services.");
		System.out.println("   * -kns-install <kns-URL> \t - Install a given KNS service.");
		System.out.println("   * -kns-remove <kns-URL> \t - Remove a given KNS service.");	
		System.out.println("   * -r-install  <URL> \t - Install a given resource in KBox.");
		System.out.println("   * -kb-install  <kb-URL> \t - Install a given knowledgebase using the available KNS services to resolve it.");
		System.out.println("   * -kb-install  <kb-URL> -index <indexFile> \t - Install a given index in a given knowledgebase URL.");
		System.out.println("   * -kb-install  <kb-URL> -kns-server <kns-server-URL> \t - Install a knowledgebase from a a given KNS server.");
		System.out.println("   * -kb-list \t - List all available KNS services and knowledgebases.");
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
	 * Install the given url in your personal Knowledge Name Service.
	 * This service will be used to Lookup Knowledge bases.
	 * 
	 * @param url the url of the KNS (Knwoledge Name Service)
	 */
	public static void installKNS(URL url) {
		CustomParams cs = new CustomParams(KBox.KBOX_DIR 
				+ File.separator 
				+ KBox.KNS_FILE_NAME, 
				KBox.CONTEXT_NAME);
		cs.add(url.toString());
	}
	
	/**
	 * Remove the given url from your personal Knowledge Name Service.
	 */
	public static void removeKNS(URL url) {
		CustomParams cs = new CustomParams(KBox.KBOX_DIR 
				+ File.separator 
				+ KBox.KNS_FILE_NAME, 
				KBox.CONTEXT_NAME);
		cs.remove(url.toString());
	}
	
	/**
	 * 
	 * @param args
	 * @return
	 */
	public static Map<String, String> parse(String[] args) {
		Map<String, String> map = new HashMap<String, String>();
		for(int i = 0; i < args.length ; i++) {
			if(args[i].contains("-")) { // is a command
				if(i+1 < args.length && !args[i+1].contains("-")) {
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
	 * The installation try to resolve the knowledgebase by the available KNS.
	 * 
	 * @param knowledgebase the URL of the knowledgebase
	 * @throws IOException if any error occurs during the operation.
	 */
	public static void installKB(URL knowledgebase) throws IOException {
		URL file = resolveURL(knowledgebase);
		KBox.installKB(knowledgebase, file);
	}

	/**
	 * Install a given index file in the given knowledgebase.
	 * 
	 * @param knowledgebase the URL of the knowledgebase whereas the index will be installed.
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
	 * Install a given knowledgebase resolved by a given KNS service.
	 * 
	 * @param  knowledgebase the URL of the knowledgebase that is going to be installed.
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
	 * @param filesToIndex the files containing the knowledge base to be indexed.
	 * @throws IOException if any error occurs during the indexing process.
	 */
	public static void createIndex(File indexFile, URL[] filesToIndex) throws IOException {
		Path indexDirPath = Files.createTempDirectory("kb");
		File indexDirFile = indexDirPath.toFile();
		TDB.bulkload(indexDirFile.getPath(), filesToIndex);
		ZIPUtil.zip(indexDirFile.getAbsolutePath(), indexFile.getAbsolutePath());
	}

	/**
	 * Query the given knowledgebase.
	 * 
	 * @param knowledgebase the knowledgebase to be queried.
	 * @param sparql the SPARQL query.
	 * @return a result set with the given query solution.
	 * @throws Exception if the knowledgebase can not be found.
	 */
	public static ResultSet query(URL knowledgebase, String sparql) throws Exception {
		File localDataset = org.aksw.kbox.KBox.getResource(knowledgebase);
		Exception e = new KBNotFoundException("Dataset " + knowledgebase.toString() + " does not exist. You can install it using the command install.");
		if(localDataset == null || !localDataset.exists()) {
			throw e;
		}
		File localDatasetGraph = new File(localDataset.getPath() + File.separator + KB_GRAPH_DIR_NAME);
		if(!localDatasetGraph.exists()) {
			throw e;
		}
		return TDB.query(sparql, localDatasetGraph.getPath());
	}
}
