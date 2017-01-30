package org.aksw.kbox.kibe;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.aksw.kbox.kibe.console.ConsoleIntallInputStreamFactory;
import org.aksw.kbox.kibe.exception.KBNotFoundException;
import org.aksw.kbox.kibe.exception.KBNotResolvedException;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;

public class Main {
	private final static Logger logger = Logger.getLogger(Main.class);	
	
	// CONSTANTS	
	private final static String VERSION = "v0.0.1-alpha3";
	
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
	private final static String INFO_COMMAND = "-info";
	private final static String SEARCH_COMMAND = "-search";
	private final static String INSTALL_COMMAND = "-install";
	private final static String CREATE_INDEX_COMMAND = "-createIndex";
	private final static String VERSION_COMMAND = "-version";
	private final static String RESOURCE_DIR_COMMAND = "-r-dir";

	public static void main(String[] args) throws Exception {
		Map<String, String> commands = parse(args);
		ConsoleIntallInputStreamFactory inputStreamFactory = new ConsoleIntallInputStreamFactory();
		if(commands.containsKey(CREATE_INDEX_COMMAND)) {
			String files = commands.get(CREATE_INDEX_COMMAND);
			File indexFile = new File(KB_GRAPH_DIR_NAME);
			System.out.println("Creating index.");
			KBox.createIndex(indexFile, filePathsToURLs(new File(files).listFiles()));
			System.out.println("Index created.");
		} else if(commands.containsKey(RESOURCE_INSTALL_COMMAND) &&
				commands.containsKey(GRAPH_COMMAND)) {
			String url = commands.get(GRAPH_COMMAND);
			URL resourceURL = new URL(url);
			String resource = commands.get(RESOURCE_INSTALL_COMMAND);
			URL file = new URL(resource);
			System.out.println("Installing resource " + resource);
			KBox.install(resourceURL, file);
			System.out.println("Resource installed.");
		} else if(commands.containsKey(KB_INSTALL_COMMAND) && 
				commands.containsKey(INDEX_COMMAND)) {
			String kb2Install = commands.get(KB_INSTALL_COMMAND);
			URL kbURL = new URL(kb2Install);
			String resource = commands.get(INDEX_COMMAND);
			File resourceFile = new File(resource);
			URL file = resourceFile.toURI().toURL();
			System.out.println("Installing KB " + kb2Install);
			KBox.installKB(kbURL, inputStreamFactory.get(file));
			System.out.println("KB installed.");
		}  else if(commands.containsKey(KB_INSTALL_COMMAND)
				&& commands.containsKey(KNS_SERVER_COMMAND)) {
			String knsServer = commands.get(KNS_SERVER_COMMAND);
			String kbURL = commands.get(KB_INSTALL_COMMAND);
			System.out.println("Installing KB " + kbURL + " from KNS " + knsServer);
			try{
				KBox.installKBFromKNSServer(new URL(kbURL), new URL(knsServer));
			} catch (KBNotResolvedException e) {
				System.out.println("The knowledge base " + kbURL + " is not available in " + knsServer);								
			} catch (Exception e) {
				String message = "Error installing knowledge base " + kbURL + " from " + knsServer + ".";
				System.out.println(message);
				logger.error(message, e);
			}
			System.out.println("KB installed.");
		} else if(commands.containsKey(KB_INSTALL_COMMAND)) {
			String url = commands.get(KB_INSTALL_COMMAND);
			URL kbNameURL = new URL(url);
			URL kbURL = KBox.resolveURL(kbNameURL);
			if(kbURL == null) {
				System.out.println("KB could not be located in the available KNS services.");
			} else {
				System.out.println("Installing KB " + url);
				KBox.installKB(kbNameURL, inputStreamFactory.get(kbURL));
				System.out.println("KB installed.");
			}
		} else if(commands.containsKey(KNS_INSTALL_COMMAND)) {
			String url = commands.get(KNS_INSTALL_COMMAND);
			URL knsURL = new URL(url);
			System.out.println("Installing KNS " + url);
			KBox.installKNS(knsURL);
			System.out.println("KNS installed.");
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
				ResultSet rs = KBox.query(sparql, install, inputStreamFactory, urls);
				if(commands.containsKey(SPARQL_QUERY_JSON_OUTPUT_FORMAT_COMMAND)) {
					ResultSetFormatter.outputAsJSON(System.out, rs);
				} else {
					ResultSetFormatter.out(System.out, rs);
				}
			} catch (KBNotFoundException e) {
				System.out.println("Error exectuting query.");
				System.out.println("The knowledge graph could not be found.");
				System.out.println("You can install it by executing the command -kb-install or "
						+ "execute the query command adding -install paramenter.");			
			} catch (Exception e) {
				String message = "Error exectuting query: " + sparql ;
				System.out.println(message);
				logger.error(message, e);
			}
		} else if (commands.containsKey(KNS_LIST_COMMAND)) {
			System.out.println("KNS table list");
			Iterable<String> it = KBox.listAvailableKNS();
			for(String knsServer : it) {
				System.out.println("\t - " + knsServer);
			}
		} else if (commands.containsKey(KB_LIST_COMMAND)) {
			System.out.println("Knowledge graphs table list");
			ListKNSVisitor listAllVisitor = new ListKNSVisitor();
			visitALLKNS(listAllVisitor);
		} else if (commands.containsKey(KNS_REMOVE_COMMAND)) {
			String knsURL = commands.get(KNS_REMOVE_COMMAND);
			KBox.removeKNS(new URL(knsURL));
			System.out.println("KNS removed.");
		} else if (commands.containsKey(RESOURCE_DIR_COMMAND)) {
			String resourceDir = commands.get(RESOURCE_DIR_COMMAND);
			if(resourceDir != null) {
				try {
					KBox.setResourceFolder(resourceDir);
					System.out.println("Resource directory redirected to " + resourceDir + ".");
				} catch (Exception e) {
					String message = "Error changing KBox resource repository to " + resourceDir + ".";
					System.out.println(message);
					logger.error(message, e);
				}
			} else {
				resourceDir = KBox.getResourceFolder();
				System.out.println("Your current resource directory is: " + resourceDir);					
			}			
		} else if (commands.containsKey(SEARCH_COMMAND)) {
			String pattern = commands.get(SEARCH_COMMAND);
			SearchKBKNSVisitor searchVisitor = new SearchKBKNSVisitor(pattern);
			visitALLKNS(searchVisitor);
		} else if (commands.containsKey(INFO_COMMAND)) {
			String graph = commands.get(INFO_COMMAND);
			InfoKBKNSVisitor visitor = new InfoKBKNSVisitor(graph);
			visitALLKNS(visitor);
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
		System.out.println("   * -sparql <query> -graph <graph> [-install] [-json] \t - Query a given graph (e.g. -sparql \"Select ...\" -graph \"graph1,graph2\")");
		System.out.println("                                               \t - ps: use -install in case you want to enable the auto-dereference.");	
		System.out.println("   * -kns-list \t - List all availables KNS services.");
		System.out.println("   * -kns-install <kns-URL> \t - Install a given KNS service.");
		System.out.println("   * -kns-remove <kns-URL> \t - Remove a given KNS service.");	
		System.out.println("   * -r-install  <URL> \t - Install a given resource in KBox.");
		System.out.println("   * -kb-install  <kb-URL> \t - Install a given knowledge graph using the available KNS services to resolve it.");
		System.out.println("   * -kb-install  <kb-URL> -index <indexFile> \t - Install a given index in a given knowledge graph URL.");
		System.out.println("   * -kb-install  <kb-URL> -kns-server <kns-server-URL> \t - Install a knowledge graph from a a given KNS server.");
		System.out.println("   * -kb-list \t - List all available KNS services and knowledge graphs.");
		System.out.println("   * -info <kb-URL> \t - Gives the information about a specific KB.");
		System.out.println("   * -search <kb-URL-pattern> \t - Search for all kb-URL containing a given pattern.");
		System.out.println("   * -r-dir <resourceDir>\t - Change the current path of the KBox resource container.");
		System.out.println("   * -version \t - display KBox version.");
	}
	
	/**
	 * Iterate over all Knowledge Names (KNs) of a given Knowledge Name Service (KNS).
	 * 
	 * @param KNSVisitor an implementation of KNSVisitor.
	 * 
	 * @throws IOException if any error occurs during the operation.
	 */
	public static void visitKNS(URL knsURL, KNSVisitor visitor) throws IOException {
		URL tableURL = new URL(knsURL.toString() + "/" + FILE_SERVER_TABLE_FILE_NAME);
		KNSTable table = new KNSTable(tableURL);
		table.visit(visitor);
	}
	
	/**
	 * Iterate over all available KNS services with a given visitor.
	 * 
	 * @param KNSVisitor an implementation of KNSVisitor.
	 * 
	 * @throws IOException if any error occurs during the operation.
	 */
	public static void visitALLKNS(KNSVisitor visitor) throws IOException {
		Iterable<String> it = KBox.listAvailableKNS();
		visitKNS(new URL(DEFAULT_KNS_TABLE_URL), visitor);
		for(String knsServer : it) {
			visitKNS(new URL(knsServer), visitor);
		}
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
}
