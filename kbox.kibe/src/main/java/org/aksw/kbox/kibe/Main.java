package org.aksw.kbox.kibe;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.aksw.kbox.fusca.Listener;
import org.aksw.kbox.fusca.Server;
import org.aksw.kbox.fusca.exception.ServerStartException;
import org.aksw.kbox.kibe.console.ConsoleIntallInputStreamFactory;
import org.aksw.kbox.kibe.exception.KBNotResolvedException;
import org.aksw.kbox.kibe.utils.ZIPUtil;
import org.aksw.kbox.kns.CustomParamKNSServerList;
import org.aksw.kbox.kns.KNSServer;
import org.aksw.kbox.kns.KNSServerListVisitor;
import org.aksw.kbox.kns.ServerAddress;
import org.aksw.kbox.utils.URLUtils;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;

public class Main {

	private final static Logger logger = Logger.getLogger(Main.class);

	// CONSTANTS
	private final static String VERSION = "v0.0.1-alpha3";
	
	public final static String KNS_FILE_NAME = "kbox.kibe.kns";
	public final static String CONTEXT_NAME = "kbox.kibe";

	public final static String FILE_SERVER_TABLE_FILE_NAME = "table.kns";
	public final static String KB_GRAPH_FILE_NAME = "kbox.kb";
	public final static String KB_KNS_NAME = "kns.kb";
	
	public final static String COMMAND_PRAGMA = "-";
	public final static String KB_COMMAND_SEPARATOR = ",";
	
	// COMMANDS	
	private final static String INSTALL_COMMAND = "-install";
	private final static String KB_COMMAND = "-kb";
	private final static String KNS_COMMAND = "-kns";
	private final static String REMOVE_COMMAND = "-remove";
	private final static String SERVER_COMMAND = "-server";
	private final static String SERVERIALIZE_COMMAND = "-serialize";
	private final static String LIST_COMMAND = "-list";
	private final static String SPARQL_QUERY_COMMAND = "-sparql";
	private final static String SPARQL_QUERY_JSON_OUTPUT_FORMAT_COMMAND = "-json";
	private final static String INDEX_COMMAND = "-index";
	private final static String INFO_COMMAND = "-info";
	private final static String SEARCH_COMMAND = "-search";
	private final static String CREATE_INDEX_COMMAND = "-createIndex";
	private final static String VERSION_COMMAND = "-version";
	private final static String RESOURCE_DIR_COMMAND = "-r-dir";
	private final static String SERVER_COMMAND_PORT = "-port";
	private final static String LOCATE_COMMAND = "-locate";
	private final static String SEVER_COMMAND_SUBDOMAIN = "-subDomain";
	private final static String FORMAT_COMMAND = "-format";

	public static void main(String[] args) {
		Map<String, String> commands = parse(args);
		ConsoleIntallInputStreamFactory inputStreamFactory = new ConsoleIntallInputStreamFactory();
		if(commands.containsKey(CREATE_INDEX_COMMAND)) {
			String directory = commands.get(CREATE_INDEX_COMMAND);
			File indexFile = new File(KB_GRAPH_FILE_NAME);
			try {
				System.out.println("Creating index.");
				KBox.createIndex(indexFile, URLUtils.fileToURL(new File(directory).listFiles()));
				System.out.println("Index created.");
			} catch (Exception e) {
				String message = "An error occurred while creating the index: " + e.getMessage();
				System.out.println(message);
				logger.error(message, e);
			}
		} else if(commands.containsKey(SERVERIALIZE_COMMAND)) {
			String directory = commands.get(SERVERIALIZE_COMMAND);
			File indexFile = new File(KB_KNS_NAME);
			try {
				System.out.println("Serializing content inside the directory " + directory);
				ZIPUtil.zip(new File(directory).getAbsolutePath(), indexFile.getAbsolutePath());
				System.out.println("Process finished.");
			} catch (Exception e) {
				String message = "An error occurred while serializing: " + e.getMessage();
				System.out.println(message);
				logger.error(message, e);
			}
		} else if(commands.containsKey(INSTALL_COMMAND) &&
				commands.size() == 1) {
			String resource = commands.get(INSTALL_COMMAND);
			try {				
				System.out.println("Installing resource " + resource);
				URL url = new URL(resource);
				KBox.install(url);
				System.out.println("Resource installed.");
			} catch (Exception e) {
				String message = "Error installing the resource " + resource + ": " + e.getMessage();
				System.out.println(message);
				logger.error(message, e);
			}
		} else if(commands.containsKey(INSTALL_COMMAND) &&
				commands.containsKey(KB_COMMAND) &&
				commands.containsKey(INDEX_COMMAND)) {
			String kb2Install = commands.get(KB_COMMAND);
			try {
				URL kbURL = new URL(kb2Install);
				String resource = commands.get(INDEX_COMMAND);
				File resourceFile = new File(resource);
				URL file = resourceFile.toURI().toURL();
				System.out.println("Installing KB " + kb2Install);
				KBox.install(file, kbURL, inputStreamFactory);
				System.out.println("KB installed.");
			} catch (Exception e) {
				String message = "Error installing the knowledge base " + kb2Install +": " + e.getMessage();
				System.out.println(message);
				logger.error(message, e);
			}
		} else if(commands.containsKey(INSTALL_COMMAND) &&
				commands.containsKey(KB_COMMAND) &&
				commands.containsKey(KNS_COMMAND)) {
			String knsServer = commands.get(KNS_COMMAND);
			String kbURL = commands.get(KB_COMMAND);
			String format = commands.get(FORMAT_COMMAND);
			String version = commands.get(VERSION_COMMAND);
			try{
				System.out.println("Installing KB " + kbURL + " from KNS " + knsServer);
				if(format == null && version == null) {
					KBox.installFromKNSServer(new URL(kbURL), new URL(knsServer), inputStreamFactory);					
				} if(format != null) {
					KBox.installFromKNSServer(new URL(kbURL), new URL(knsServer), format, inputStreamFactory);					
				}
				else {
					KBox.installFromKNSServer(new URL(kbURL), new URL(knsServer), format, version, inputStreamFactory);
				}
				System.out.println("KB installed.");
			} catch (MalformedURLException e){
				System.out.println(e.getMessage());
				logger.error(e);
			} catch (KBNotResolvedException e) {
				System.out.println("The knowledge base " + kbURL + " is not available in " + knsServer);								
			} catch (Exception e) {
				String message = "Error installing knowledge base " + kbURL + " from " + knsServer + ".";
				System.out.println(message);
				logger.error(message, e);
			}
		} else if(!commands.containsKey(SPARQL_QUERY_COMMAND) &&
				!commands.containsKey(SERVER_COMMAND) &&
				commands.containsKey(INSTALL_COMMAND) &&
				commands.containsKey(KB_COMMAND)) {
			String graphNamesList = commands.get(KB_COMMAND);
			String format = commands.get(FORMAT_COMMAND);
			String version = commands.get(VERSION_COMMAND);
			String[] graphNames = graphNamesList.split(KB_COMMAND_SEPARATOR);
			URL[] urls;
			try {
				urls = URLUtils.stringToURL(graphNames);
				for(URL kbNameURL : urls) {
					try {
						System.out.println("Installing KB " + kbNameURL);
						if(format != null && version != null) {
							File kbFile = KBox.locate(kbNameURL, format, version);
							if(kbFile == null) {								
								KBox.install(kbNameURL, format, version, inputStreamFactory);
							}
						} else if (format != null) { 
							File kbFile = KBox.locate(kbNameURL, format);
							if(kbFile == null) {
								KBox.install(kbNameURL, format, inputStreamFactory);
							}
						} else {
							File kbFile = KBox.locate(kbNameURL);
							if(kbFile == null) {
								KBox.install(kbNameURL, inputStreamFactory);
							}
						}
						System.out.println("KB installed.");
					} catch (MalformedURLException e){
						String message  = e.getMessage();
						System.out.println(message);
						logger.error(message, e);
					} catch (Exception e) {
						String message =  "The knowledge base could not be found: URL:" + kbNameURL;
						if(format != null) {
							message += ", format: " + format;
						}
						if(version != null) {
							message += ", " + "vesion: " + version;  
						}
						System.out.println(message);
						logger.error(message, e);
					}
				}
			} catch (Exception e) {
				String message =  "An error occurred while parsing the KB Name list \"" + graphNames + "\": " + e.getMessage();
				System.out.println(message);
				logger.error(message, e);
			}			
		} else if(commands.containsKey(INSTALL_COMMAND) && 
				commands.containsKey(KNS_COMMAND)) {
			String url = commands.get(KNS_COMMAND);
			try {
				URL knsURL = new URL(url);
				System.out.println("Installing KNS " + url);
				KBox.installKNS(knsURL);
				System.out.println("KNS installed.");
			} catch (MalformedURLException e){
				System.out.println(e.getMessage());
				logger.error(e);
			}
		} else if (commands.containsKey(SPARQL_QUERY_COMMAND) &&
				(commands.containsKey(KB_COMMAND) ||
				commands.containsKey(SERVER_COMMAND))) {
			String sparql = commands.get(SPARQL_QUERY_COMMAND);
			String graphNamesList = commands.get(KB_COMMAND);
			if(graphNamesList != null) {
				String[] graphNames = graphNamesList.split(KB_COMMAND_SEPARATOR);
				boolean install = commands.containsKey(INSTALL_COMMAND);				 
				try {
					URL[] urls = URLUtils.stringToURL(graphNames);
					ResultSet rs = KBox.query(sparql, inputStreamFactory, install, urls);
					out(commands, rs);
				} catch (Exception e) {
					String message = "Error exectuting query '" + sparql + "': " + e.getMessage();
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
					String message = "An error occurred while trying to connect to server: " + url + "." +
							" The server seems to be unavailable, check the URL and try again.";
					System.out.println(message);
					logger.error(message, e);
				} catch (Exception e) {
					String message = "An error occurred while querying the server: " + url;
					System.out.println(message);
					logger.error(message, e);
				}
			}
		} else if (commands.containsKey(LIST_COMMAND) &&
				commands.containsKey(KNS_COMMAND)) {
			System.out.println("KNS table list");
			CustomParamKNSServerList knsServerList = new CustomParamKNSServerList();
			try {
				knsServerList.visit(new KNSServerListVisitor() {
					@Override
					public boolean visit(KNSServer knsServer) throws Exception {
						System.out.println("\t - " + knsServer.getURL());
						return false;
					}
				});
			} catch (Exception e) {
				String message = "An error occurred while listing the available knowledge bases: " + e.getCause().getMessage();
				System.out.println(message);
				logger.error(message, e);
			}
		} else if (commands.containsKey(LIST_COMMAND) &&
				!commands.containsKey(KNS_COMMAND)) {			
			try {
				System.out.println("Knowledge base table list");
				SystemOutKNSServerListVisitor listAllVisitor = new SystemOutKNSServerListVisitor();
				KBox.visit(listAllVisitor);
			} catch (UnknownHostException e) {
				String message = "An error occurred while listing the available KBs. Check your connection.";
				System.out.println(message);
				logger.error(message, e);
			} catch (Exception e) {
				String message = "An error occurred while listing the available KBs: " + e.getMessage();
				System.out.println(message);
				logger.error(message, e);
			}
		} else if (commands.containsKey(REMOVE_COMMAND) &&
				commands.containsKey(KNS_COMMAND)) {
			String knsURL = commands.get(REMOVE_COMMAND);
			try {
				System.out.println("Removing KNS " + knsURL);
				KBox.removeKNS(new URL(knsURL));			
				System.out.println("KNS removed.");
			} catch (Exception e) {
				String message = "An error occurred while removing the KNS: " + knsURL + ".";
				System.out.println(message);
				logger.error(message, e);
			}
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
			String format = commands.get(FORMAT_COMMAND);
			String version = commands.get(VERSION_COMMAND);
			SearchKBKNSVisitor searchVisitor = new SearchKBKNSVisitor(pattern, format, version);
			try {
				KBox.visit(searchVisitor);
			} catch (Exception e) {
				String message = "An error occurred while enquiring searching using the pattern: " + pattern;
				System.out.println(message);
				logger.error(message, e);
			}
		} else if (commands.containsKey(INFO_COMMAND)) {
			String graph = commands.get(INFO_COMMAND);
			String format = commands.get(FORMAT_COMMAND);
			String version = commands.get(VERSION_COMMAND);
			InfoKBKNSVisitor visitor = new InfoKBKNSVisitor(graph, format, version);
			try {
				KBox.visit(visitor);
			} catch (Exception e) {
				String message = "An error occurred while enquiring information from the graph " + graph;
				System.out.println(message);
				logger.error(message, e);
			}
		} else if (commands.containsKey(LOCATE_COMMAND) &&
				!commands.containsKey(KB_COMMAND)) {
			String resourceURI = commands.get(LOCATE_COMMAND);
			try {
				System.out.println(org.aksw.kbox.KBox.locate(new URL(resourceURI)));
			} catch (Exception e) {
				String message = "An error occurred while resolving the resource: " + resourceURI;
				System.out.println(message);
				logger.error(message, e);
			}
		} else if(commands.containsKey(LOCATE_COMMAND) && 
				commands.containsKey(KB_COMMAND)) {
			String kbURI = commands.get(KB_COMMAND);
			String format = commands.get(FORMAT_COMMAND);
			String version = commands.get(VERSION_COMMAND);
			try {
				if(format != null && version != null) {
					System.out.println(KBox.locate(new URL(kbURI), format, version));
				} if(format != null) {
					System.out.println(KBox.locate(new URL(kbURI), format));
				} else {
					System.out.println(KBox.locate(new URL(kbURI)));
				}
			} catch (Exception e) {
				String message = "An error occurred while resolving the KB: " + kbURI;
				System.out.println(message);
				logger.error(message, e);
			}
		} else if (commands.containsKey(SERVER_COMMAND) &&
				commands.containsKey(KB_COMMAND)) {
			int port = 8080;
			String subDomain = "kbox";
			if(commands.containsKey(SERVER_COMMAND_PORT)) {
				port = Integer.parseInt(commands.get(SERVER_COMMAND_PORT));
			}
			if(commands.containsKey(SEVER_COMMAND_SUBDOMAIN)) {
				subDomain = commands.get(SEVER_COMMAND_SUBDOMAIN);
			}
			String graphNamesList = commands.get(KB_COMMAND);
			String[] graphNames = graphNamesList.split(KB_COMMAND_SEPARATOR);
			boolean install = commands.containsKey(INSTALL_COMMAND);
			URL[] urls = new URL[graphNames.length];
			try {
				for(int i=0; i < graphNames.length; i++) {
					urls[i]  = new URL(graphNames[i]);
				}
				System.out.println("Loading Model...");
				Model model = KBox.createModel(inputStreamFactory, install, urls);
				final String serverAddress = "http://localhost:" + port + "/" + subDomain + "/query";
				Listener serverListener = new Listener() {
					@Override
					public void starting() {
						System.out.println("Publishing service on " + serverAddress);
					}
					@Override
					public void started() {
						System.out.println("Service up and running ;-) ...");
					}
				};
				Server server = new Server(port, KBox.getResourceFolder(), subDomain, model, serverListener);
				server.start();
			} catch (KBNotResolvedException e) {
				System.out.println("Error installing KB: "
						+ "The knowledge base could not be found in any of the KNS servers.");
				System.out.println("Check if the servers are online or if the requested resource exist.");
				logger.error(e);
			} catch (ServerStartException e) {
				String message = "An error occurred while starting the server, "
						+ "check if the port is not being used by another "
						+ "application or if the parameters are valid.";
				System.out.println(message);
				logger.error(message, e);
			} catch (MalformedURLException e){
				System.out.println(e.getMessage());
				logger.error(e);
			} catch (Exception e) {
				String message = "Error installing the Knowledge Bases." ;
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
		System.out.println("   * -createIndex <directory>\t - Create an index with the files in a given directory.");
		System.out.println("                             \t ps: the directory might contain only RDF compatible file formats.");
		System.out.println("   * -serialize <directory>\t - Serialize the content of a directory to be served in a KNS system.");
		System.out.println("   * -sparql <query> (-kb <KB> | -server <URL>) [-install] [-json]\t - Query a given graph (e.g. -sparql \"Select ...\" -graph \"graph1,graph2\")");
		System.out.println("                                               \t - ps: use -install in case you want to enable the auto-dereference.");	
		System.out.println("   * -server [-port <port> (default 8080)] [-subDomain <subDomain> (default kbox)] -kb <KB> [-install] \t - Start an SPARQL enpoint in the given subDomain containing the given graphs.");
		System.out.println("   * -list\t - List all available KNS services and knowledge graphs.");
		System.out.println("   * -list -kns\t - List all availables KNS services.");
		System.out.println("   * -install <URL>\t - Install a given resource.");
		System.out.println("   * -install -kns <kns-URL>\t - Install a given KNS service.");
		System.out.println("   * -install -kb <kb-URL> [-format <format> -version <version>]\t - Install a given knowledge graph using the available KNS services to resolve it.");
		System.out.println("   * -install -kb <kb-URL> -index <indexFile>\t - Install a given index in a given knowledge graph URL.");
		System.out.println("   * -install -kb <kb-URL> -kns <kns-URL> [-format <format> -version <version>]\t - Install a knowledge graph from a a given KNS service with the specific format and version.");
		System.out.println("   * -remove -kns <kns-URL>\t - Remove a given KNS service.");
		System.out.println("   * -info <kb-URL> [-format <format> -version <version>]\t - Gives the information about a specific KB.");
		System.out.println("   * -locate <URL>\t - returns the local address of the given resource.");
		System.out.println("   * -locate -kb <kb-URL> [-format <format> -version <version>]\t - returns the local address of the given KB.");
		System.out.println("   * -search <kb-URL-pattern> [-format <format> -version <version>]\t - Search for all kb-URL containing a given pattern.");		
		System.out.println("   * -r-dir\t - Show the path to the resource folder.");
		System.out.println("   * -r-dir <resourceDir>\t - Change the path of the resource folder.");
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
			if(args[i].startsWith(COMMAND_PRAGMA)) { // is it a command				
				if(i+1 < args.length && !args[i+1].startsWith(COMMAND_PRAGMA)) {
					map.put(args[i], args[i+1]);
				} else {
					map.put(args[i], null);
				}
			}
		}
		return map;
	}
}
