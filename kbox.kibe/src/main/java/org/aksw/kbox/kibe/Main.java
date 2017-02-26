package org.aksw.kbox.kibe;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.aksw.kbox.fusca.Listener;
import org.aksw.kbox.fusca.Server;
import org.aksw.kbox.fusca.exception.ServerStartException;
import org.aksw.kbox.kibe.console.ConsoleIntallInputStreamFactory;
import org.aksw.kbox.kibe.exception.KBNotFoundException;
import org.aksw.kbox.kibe.exception.KBNotResolvedException;
import org.aksw.kbox.kibe.stream.DefaultInputStreamFactory;
import org.aksw.kbox.kns.KNSServerList;
import org.aksw.kbox.kns.KNSServerListVisitor;
import org.aksw.kbox.kns.ServerAddress;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;

public class Main {

	private final static Logger logger = Logger.getLogger(Main.class);
	
	// setting default log path	
	
	// CONSTANTS	
	private final static String VERSION = "v0.0.1-alpha3";
	
	public final static String KNS_FILE_NAME = "kbox.kibe.kns";
	public final static String CONTEXT_NAME = "kbox.kibe";

	public final static String FILE_SERVER_TABLE_FILE_NAME = "table.kns";
	public final static String KB_GRAPH_DIR_NAME = "kbox.kb";
	
	public final static String KB_COMMAND_SEPARATOR = ",";
	
	// COMMANDS	
	private final static String INSTALL_COMMAND = "-install";
	private final static String KB_COMMAND = "-kb";
	private final static String KNS_COMMAND = "-kns";
	private final static String REMOVE_COMMAND = "-remove";
	private final static String SERVER_COMMAND = "-server";
	private final static String LIST_COMMAND = "-list";
	private final static String SPARQL_QUERY_COMMAND = "-sparql";
	private final static String SPARQL_QUERY_JSON_OUTPUT_FORMAT_COMMAND = "-json";
	private final static String GRAPH_COMMAND = "-graph";
	private final static String INDEX_COMMAND = "-index";
	private final static String INFO_COMMAND = "-info";
	private final static String SEARCH_COMMAND = "-search";
	private final static String CREATE_INDEX_COMMAND = "-createIndex";
	private final static String VERSION_COMMAND = "-version";
	private final static String RESOURCE_DIR_COMMAND = "-r-dir";
	private final static String SERVER_COMMAND_PORT = "-port";
	private final static String LOCATE_COMMAND = "-locate";
	private final static String SEVER_COMMAND_SUBDOMAIN = "-subDomain";

	public static void main(String[] args) throws Exception {
		Map<String, String> commands = parse(args);
		ConsoleIntallInputStreamFactory inputStreamFactory = new ConsoleIntallInputStreamFactory();
		if(commands.containsKey(CREATE_INDEX_COMMAND)) {
			String files = commands.get(CREATE_INDEX_COMMAND);
			File indexFile = new File(KB_GRAPH_DIR_NAME);
			System.out.println("Creating index.");
			KBox.createIndex(indexFile, filePathsToURLs(new File(files).listFiles()));
			System.out.println("Index created.");
		} else if(commands.containsKey(INSTALL_COMMAND) &&
				commands.size() == 1) {
			String resource = commands.get(GRAPH_COMMAND);
			URL url = new URL(resource);
			System.out.println("Installing resource " + resource);
			KBox.install(url);
			System.out.println("Resource installed.");
		} else if(commands.containsKey(INSTALL_COMMAND) &&
				commands.containsKey(KB_COMMAND) && 
				commands.containsKey(INDEX_COMMAND)) {
			String kb2Install = commands.get(KB_COMMAND);
			URL kbURL = new URL(kb2Install);
			String resource = commands.get(INDEX_COMMAND);
			File resourceFile = new File(resource);
			URL file = resourceFile.toURI().toURL();
			System.out.println("Installing KB " + kb2Install);
			try(InputStream is = inputStreamFactory.get(file)) {
				KBox.installKB(is, kbURL);
			}
			System.out.println("KB installed.");
		}  else if(commands.containsKey(INSTALL_COMMAND) &&
				commands.containsKey(KB_COMMAND) &&
				commands.containsKey(KNS_COMMAND)) {
			String knsServer = commands.get(KNS_COMMAND);
			String kbURL = commands.get(KB_COMMAND);
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
		} else if(commands.containsKey(INSTALL_COMMAND) &&
				commands.containsKey(KB_COMMAND)) {
			String url = commands.get(KB_COMMAND);
			URL kbNameURL = new URL(url);
			URL kbURL = KBox.resolve(kbNameURL);
			if(kbURL == null) {
				System.out.println("KB could not be located in the available KNS services.");
			} else {
				System.out.println("Installing KB " + url);
				try(InputStream is = inputStreamFactory.get(kbURL)) {
					KBox.installKB(is, kbNameURL);
				}
				System.out.println("KB installed.");
			}
		} else if(commands.containsKey(INSTALL_COMMAND) && 
				commands.containsKey(KNS_COMMAND)) {
			String url = commands.get(KNS_COMMAND);
			URL knsURL = new URL(url);
			System.out.println("Installing KNS " + url);
			KBox.installKNS(knsURL);
			System.out.println("KNS installed.");
		} else if (commands.containsKey(SPARQL_QUERY_COMMAND) &&
				(commands.containsKey(GRAPH_COMMAND) ||
				commands.containsKey(SERVER_COMMAND))) {
			String sparql = commands.get(SPARQL_QUERY_COMMAND);
			String graphNamesList = commands.get(GRAPH_COMMAND);
			if(graphNamesList != null) {
				String[] graphNames = graphNamesList.split(KB_COMMAND_SEPARATOR);
				boolean install = commands.containsKey(INSTALL_COMMAND);
				URL[] urls = new URL[graphNames.length];
				for(int i=0; i < graphNames.length; i++) {
					urls[i]  = new URL(graphNames[i]);
				}
				try {
					ResultSet rs = KBox.query(sparql, install, inputStreamFactory, urls);
					out(commands, rs);
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
			} else {
				String url = commands.get(SERVER_COMMAND);
				ServerAddress serverURL = new ServerAddress(url);
				try {
					ResultSet rs = KBox.query(sparql, serverURL);
					out(commands, rs);
				} catch (QueryExceptionHTTP e) {
					String message = "An error occurs while trying to connect to server:" + url + "." +
							" Check the URL address and try again.";
					System.out.println(message);
					logger.error(message, e);
				}
			}
		} else if (commands.containsKey(LIST_COMMAND) &&
				commands.containsKey(KNS_COMMAND)) {
			System.out.println("KNS table list");
			KNSServerList knsServerList = new KNSServerList();
			knsServerList.visit(new KNSServerListVisitor() {
				@Override
				public boolean visit(String knsServerURL) throws Exception {
					System.out.println("\t - " + knsServerURL);
					return false;
				}
			});
		} else if (commands.containsKey(LIST_COMMAND) &&
				!commands.containsKey(KNS_COMMAND)) {
			System.out.println("Knowledge graphs table list");
			KNSListVisitor listAllVisitor = new KNSListVisitor();
			KBox.visitALLKNSServers(listAllVisitor);
		} else if (commands.containsKey(REMOVE_COMMAND) &&
				commands.containsKey(KNS_COMMAND)) {
			String knsURL = commands.get(REMOVE_COMMAND);
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
			KBox.visitALLKNSServers(searchVisitor);
		} else if (commands.containsKey(INFO_COMMAND)) {
			String graph = commands.get(INFO_COMMAND);
			InfoKBKNSVisitor visitor = new InfoKBKNSVisitor(graph);
			KBox.visitALLKNSServers(visitor);
		} else if (commands.containsKey(LOCATE_COMMAND) &&
				!commands.containsKey(KB_COMMAND)) {
			String resourceURI = commands.get(LOCATE_COMMAND);
			try {
				System.out.println(org.aksw.kbox.KBox.getResource(new URL(resourceURI)));
			} catch (Exception e) {
				String message = "An error occurred while resolving the resource: " + resourceURI;
				System.out.println(message);
				logger.error(message, e);
			}
		} else if(commands.containsKey(LOCATE_COMMAND) && 
				commands.containsKey(KB_COMMAND)) {			
			String kbURI = commands.get(KB_COMMAND);
			try {
				System.out.println(KBox.locateKB(new URL(kbURI)));
			} catch (Exception e) {
				String message = "An error occurred while resolving the KB: " + kbURI;
				System.out.println(message);
				logger.error(message, e);
			}		
		} else if (commands.containsKey(SERVER_COMMAND) &&
				commands.containsKey(GRAPH_COMMAND)) {
			int port = 8080;
			String subDomain = "kbox";
			if(commands.containsKey(SERVER_COMMAND_PORT)) {
				port = Integer.parseInt(commands.get(SERVER_COMMAND_PORT));
			} 
			if(commands.containsKey(SEVER_COMMAND_SUBDOMAIN)) {
				subDomain = commands.get(SEVER_COMMAND_SUBDOMAIN);
			}			
			String graphNamesList = commands.get(GRAPH_COMMAND);
			String[] graphNames = graphNamesList.split(KB_COMMAND_SEPARATOR);
			boolean install = commands.containsKey(INSTALL_COMMAND);
			URL[] urls = new URL[graphNames.length];
			for(int i=0; i < graphNames.length; i++) {
				urls[i]  = new URL(graphNames[i]);
			}
			try {
				System.out.println("Loading Model...");
				Model model = KBox.createModel(install, new DefaultInputStreamFactory(), urls);
				final String serverAddress = "http://localhost:" + port + "/" + subDomain + "/query";
				Listener serverListener = new Listener() {
					@Override
					public void starting() {
						System.out.println("Publishing service on " + serverAddress);
					}					
					@Override
					public void started() {
						System.out.println("Service running ;-) ...");
					}
				};
				Server server = new Server(port, KBox.getResourceFolder(), subDomain, model, serverListener);
				server.start();
			} catch (KBNotFoundException e) {
				System.out.println("Error installing Knowledge Graph: "
						+ "The Knowledge Graph could not be found.");				
				System.out.println("You can install it by executing the command -install -kb or "
						+ "execute the query command adding -install paramenter.");
				logger.error(e);
			} catch (ServerStartException e) {
				String message = "An error occurs while starting the server, "
						+ "check if the port is not being used by another "
						+ "application or if the parameters are valid.";
				System.out.println(message);
				logger.error(message, e);
			} catch (Exception e) {
				String message = "Error installing the Knowledge Graphs." ;
				System.out.println(message);
				logger.error(message, e);
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
		System.out.println("KBox.jar <command> [option]");
		System.out.println("Where [command] is:");
		System.out.println("   * -createIndex <directory> \t - Create an index with the files in a given directory.");
		System.out.println("                              \t ps: the directory might contain only RDF compatible file formats.");
		System.out.println("   * -sparql <query> (-graph <graph> | -server <URL>) [-install] [-json] \t - Query a given graph (e.g. -sparql \"Select ...\" -graph \"graph1,graph2\")");
		System.out.println("                                               \t - ps: use -install in case you want to enable the auto-dereference.");	
		System.out.println("   * -server [-port <port> (default 8080)] [-subDomain <subDomain> (default kbox)] -graph <graph> [-install] \t - Start an SPARQL enpoint in the given subDomain containing the given graphs.");
		System.out.println("   * -list \t - List all available KNS services and knowledge graphs.");
		System.out.println("   * -list -kns\t - List all availables KNS services.");		
		System.out.println("   * -install -kns <kns-URL> \t - Install a given KNS service.");
		System.out.println("   * -remove -kns <kns-URL> \t - Remove a given KNS service.");
		System.out.println("   * -install <URL> \t - Install a given resource in KBox.");
		System.out.println("   * -install -kb <kb-URL>\t - Install a given knowledge graph using the available KNS services to resolve it.");
		System.out.println("   * -install -kb <kb-URL> -index <indexFile> \t - Install a given index in a given knowledge graph URL.");
		System.out.println("   * -install -kb <kb-URL> -kns <kns-URL> \t - Install a knowledge graph from a a given KNS service.");
		System.out.println("   * -info <kb-URL> \t - Gives the information about a specific KB.");
		System.out.println("   * -locate <URL>\t - returns the local address of the given resource.");
		System.out.println("   * -locate -kb <kb-URL>\t - returns the local address of the given KB.");		
		System.out.println("   * -search <kb-URL-pattern> \t - Search for all kb-URL containing a given pattern.");
		System.out.println("   * -r-dir <resourceDir>\t - Change the current path of the KBox resource container.");
		System.out.println("   * -version \t - display KBox version.");
	}
	
	private static void out(Map<String, String> commands, ResultSet rs) {
		if(commands.containsKey(SPARQL_QUERY_JSON_OUTPUT_FORMAT_COMMAND)) {
			ResultSetFormatter.outputAsJSON(System.out, rs);
		} else {
			ResultSetFormatter.out(System.out, rs);
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
			if(args[i].startsWith("-")) { // is it a command				
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
