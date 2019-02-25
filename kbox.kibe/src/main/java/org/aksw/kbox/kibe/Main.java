package org.aksw.kbox.kibe;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.aksw.kbox.Install;
import org.aksw.kbox.ZipLocate;
import org.aksw.kbox.fusca.Listener;
import org.aksw.kbox.fusca.Server;
import org.aksw.kbox.fusca.exception.ServerStartException;
import org.aksw.kbox.kibe.console.ConsoleInstallInputStreamFactory;
import org.aksw.kbox.kibe.exception.KBDereferencingException;
import org.aksw.kbox.kibe.exception.KBNotLocatedException;
import org.aksw.kbox.kns.CustomRNSServerList;
import org.aksw.kbox.kns.RNSServerListVisitor;
import org.aksw.kbox.kns.RNService;
import org.aksw.kbox.kns.ServerAddress;
import org.aksw.kbox.kns.exception.ResourceNotResolvedException;
import org.aksw.kbox.utils.GzipUtils;
import org.aksw.kbox.utils.URLUtils;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;
import org.apache.log4j.Logger;
import org.zeroturnaround.zip.ZipUtil;

public class Main {

	private final static Logger logger = Logger.getLogger(Main.class);

	// CONSTANTS
	private final static String VERSION = "v0.0.2-alpha";

	public final static String KNS_FILE_NAME = "kbox.kibe.kns";
	public final static String CONTEXT_NAME = "kbox.kibe";

	public final static String FILE_SERVER_TABLE_FILE_NAME = "table.kns";
	public final static String KB_GRAPH_FILE_NAME = "kbox.kb";
	public final static String KB_KNS_NAME = "kns.kb";
	public final static String KB_EXTENSION = ".kb";
	public final static String ZIP_EXTENSION = ".zip";
	public final static String GZIP_EXTENSION = ".gzip";

	public final static String COMMAND_PRAGMA = "-";
	public final static String SUB_COMMAND_PRAGMA = "/";
	public final static String KB_COMMAND_SEPARATOR = ",";

	// COMMANDS
	private final static String INSTALL_COMMAND = "-install";
	private final static String KB_COMMAND = "-kb";
	private final static String RNS_COMMAND = "-rns";
	private final static String REMOVE_COMMAND = "-remove";
	private final static String SERVER_COMMAND = "-server";
	private final static String LIST_COMMAND = "-list";
	private final static String PAGINATION_COMMAND = "/p";
	private final static String SPARQL_QUERY_COMMAND = "-sparql";
	private final static String SPARQL_QUERY_JSON_OUTPUT_FORMAT_COMMAND = "-json";
	private final static String FILE_COMMAND = "-file";
	private final static String INFO_COMMAND = "-info";
	private final static String SEARCH_COMMAND = "-search";
	private final static String CONVERT_COMMAND = "-convert";
	private final static String ZIP_ENCODE_COMMAND = "-zip";
	private final static String GZIP_ENCODE_COMMAND = "-gzip";
	private final static String VERSION_COMMAND = "-version";
	private final static String RESOURCE_DIR_COMMAND = "-r-dir";
	private final static String SERVER_COMMAND_PORT = "-port";
	private final static String LOCATE_COMMAND = "-locate";
	private final static String SEVER_COMMAND_SUBDOMAIN = "-subDomain";
	private final static String FORMAT_COMMAND = "-format";

	public static void main(String[] args) {
		Map<String, String[]> commands = parse(args);
		ConsoleInstallInputStreamFactory inputStreamFactory = new ConsoleInstallInputStreamFactory();
		if (commands.containsKey(CONVERT_COMMAND) && !commands.containsKey(ZIP_ENCODE_COMMAND)
				&& !commands.containsKey(GZIP_ENCODE_COMMAND)) {
			String directoryParam = commands.get(CONVERT_COMMAND)[0];
			String destFileParam = commands.get(CONVERT_COMMAND)[1];
			File source = new File(directoryParam);
			File destFile = null;
			if (destFileParam == null) {
				destFile = new File(source.getName() + KB_EXTENSION);
			} else {
				destFile = new File(destFileParam);
			}
			try {
				if (source.isDirectory()) {
					System.out.println("Converting content inside the directory " + source);
					KBox.dirToKB(destFile, source);
				} else {
					System.out.println("Converting RDF file " + source);
					KBox.RDFToKB(destFile, source);
				}
				System.out.println("Converting completed.");
			} catch (Exception e) {
				String message = "An error occurred while creating the index: " + e.getMessage();
				System.out.println(message);
				logger.error(message, e);
			}
		} else if (commands.containsKey(CONVERT_COMMAND) && commands.containsKey(ZIP_ENCODE_COMMAND)) {
			String directoryParam = commands.get(CONVERT_COMMAND)[0];
			String destFileParam = commands.get(CONVERT_COMMAND)[1];
			File source = new File(directoryParam);
			try {
				File destFile = null;
				if (destFileParam == null) {
					destFile = new File(source.getName() + ZIP_EXTENSION);
				} else {
					destFile = new File(destFileParam);
				}
				if (source.isDirectory()) {
					System.out.println("Converting content inside the directory: " + source);
					ZipUtil.pack(source, destFile);
				} else {
					System.out.println("Converting file: " + source);
					ZipUtil.packEntries(new File[] { source }, destFile);
				}

				System.out.println("Converting completed.");
			} catch (Exception e) {
				String message = "An error occurred while converting: " + e.getMessage();
				System.out.println(message);
				logger.error(message, e);
			}
		} else if (commands.containsKey(CONVERT_COMMAND) && commands.containsKey(GZIP_ENCODE_COMMAND)) {
			String fileParam = commands.get(CONVERT_COMMAND)[0];
			String destFileParam = commands.get(CONVERT_COMMAND)[1];
			File targetFile = new File(fileParam);
			if (targetFile.isDirectory()) {
				System.out.println("The target file cannot be a directory.");
				return;
			}
			try {
				System.out.println("Converting file: " + targetFile);
				File destFile = null;
				if (destFileParam == null) {
					destFile = new File(targetFile.getName() + GZIP_EXTENSION);
				} else {
					destFile = new File(destFileParam);
				}
				GzipUtils.pack(targetFile, destFile);
				System.out.println("Converting completed.");
			} catch (Exception e) {
				String message = "An error occurred while converting: " + e.getMessage();
				System.out.println(message);
				logger.error(message, e);
			}
		} else if (commands.containsKey(INSTALL_COMMAND) && commands.size() == 1) {
			String resource = getSingleParam(commands, INSTALL_COMMAND);
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
		} else if (commands.containsKey(INSTALL_COMMAND) && commands.containsKey(KB_COMMAND)
				&& commands.containsKey(FILE_COMMAND)) {
			String kb2Install = getSingleParam(commands, KB_COMMAND);
			try {
				URL kbURL = new URL(kb2Install);
				String resource = getSingleParam(commands, FILE_COMMAND);
				File resourceFile = new File(resource);
				URL file = resourceFile.toURI().toURL();
				System.out.println("Installing KB " + kb2Install);
				KBox.install(file, kbURL, inputStreamFactory);
				System.out.println("KB installed.");
			} catch (Exception e) {
				String message = "Error installing the knowledge base " + kb2Install + ": " + e.getMessage();
				System.out.println(message);
				logger.error(message, e);
			}
		} else if (commands.containsKey(INSTALL_COMMAND) && commands.containsKey(KB_COMMAND)
				&& commands.containsKey(RNS_COMMAND)) {
			String rnsServer = getSingleParam(commands, RNS_COMMAND);
			String kbURL = getSingleParam(commands, KB_COMMAND);
			String format = getSingleParam(commands, FORMAT_COMMAND);
			String version = getSingleParam(commands, VERSION_COMMAND);
			try {
				System.out.println("Installing KB " + kbURL + " from RNS " + rnsServer);
				if (format == null && version == null) {
					KBox.installFromRNSServer(new URL(kbURL), new URL(rnsServer), inputStreamFactory);
				}
				if (format != null) {
					KBox.installFromRNSServer(new URL(kbURL), new URL(rnsServer), format, inputStreamFactory);
				} else {
					KBox.installFromRNSServer(new URL(kbURL), new URL(rnsServer), format, version, inputStreamFactory);
				}
				System.out.println("KB installed.");
			} catch (MalformedURLException e) {
				System.out.println(e.getMessage());
				logger.error(e);
			} catch (KBNotLocatedException e) {
				System.out.println("The knowledge base " + kbURL + " is not available in " + rnsServer + ".");
			} catch (Exception e) {
				String message = "Error installing knowledge base " + kbURL + " from " + rnsServer + ".";
				System.out.println(message);
				logger.error(message, e);
			}
		} else if (!commands.containsKey(SPARQL_QUERY_COMMAND) && !commands.containsKey(SERVER_COMMAND)
				&& commands.containsKey(INSTALL_COMMAND) && commands.containsKey(KB_COMMAND)) {
			String graphNamesList = getSingleParam(commands, KB_COMMAND);
			String format = getSingleParam(commands, FORMAT_COMMAND);
			String version = getSingleParam(commands, VERSION_COMMAND);
			String[] graphNames = graphNamesList.split(KB_COMMAND_SEPARATOR);
			URL[] urls;
			try {
				urls = URLUtils.stringToURL(graphNames);
				for (URL resourceName : urls) {
					try {
						if (format != null && version != null) {
							File kbFile = KBox.locate(resourceName, format, version);
							if (kbFile == null) {
								KBox.install(resourceName, format, version, inputStreamFactory);
							}
						} else if (format != null) {
							File kbFile = KBox.locate(resourceName, format);
							if (kbFile == null) {
								KBox.install(resourceName, format, inputStreamFactory);
							}
						} else {
							File kbFile = KBox.locate(resourceName, KBox.KIBE_FORMAT);
							if (kbFile == null) {
								KBox.install(resourceName, KBox.KIBE_FORMAT, inputStreamFactory);
							}
						}
						System.out.println(resourceName + " KB installed.");
					} catch (MalformedURLException e) {
						String message = e.getMessage();
						System.out.println(message);
						logger.error(message, e);
					} catch (Exception e) {
						String message = "The knowledge base could not be found: URL:" + resourceName;
						if (format != null) {
							message += ", format: " + format;
						}
						if (version != null) {
							message += ", " + "vesion: " + version;
						}
						System.out.println(message);
						logger.error(message, e);
					}
				}
			} catch (Exception e) {
				String message = "An error occurred while parsing the KB Name list \"" + graphNames + "\": "
						+ e.getMessage();
				System.out.println(message);
				logger.error(message, e);
			}
		} else if (commands.containsKey(INSTALL_COMMAND) && commands.containsKey(RNS_COMMAND)) {
			String url = getSingleParam(commands, RNS_COMMAND);
			try {
				URL knsURL = new URL(url);
				System.out.println("Installing RNS " + url);
				KBox.installRNS(knsURL);
				System.out.println("RNS installed.");
			} catch (MalformedURLException e) {
				System.out.println(e.getMessage());
				logger.error(e);
			}
		} else if (commands.containsKey(SPARQL_QUERY_COMMAND)
				&& (commands.containsKey(KB_COMMAND) || commands.containsKey(SERVER_COMMAND))) {
			String sparql = getSingleParam(commands, SPARQL_QUERY_COMMAND);
			String graphNamesList = getSingleParam(commands, KB_COMMAND);
			if (graphNamesList != null) {
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
				String url = getSingleParam(commands, SERVER_COMMAND);
				ServerAddress serverURL = new ServerAddress(url);
				try {
					ResultSet rs = KBox.query(sparql, serverURL);
					out(commands, rs);
				} catch (QueryExceptionHTTP e) {
					String message = "An error occurred while trying to connect to server: " + url + "."
							+ " The server seems to be unavailable, check the server address and try again.";
					System.out.println(message);
					logger.error(message, e);
				} catch (Exception e) {
					String message = "An error occurred while querying the server: " + url;
					System.out.println(message);
					logger.error(message, e);
				}
			}
		} else if (commands.containsKey(LIST_COMMAND) && commands.containsKey(RNS_COMMAND)) {
			System.out.println("DataNS table list");
			CustomRNSServerList knsServerList = new CustomRNSServerList();
			try {
				knsServerList.visit(new RNSServerListVisitor() {
					@Override
					public boolean visit(RNService knsServer) throws Exception {
						System.out.println("\t - " + knsServer.getURL());
						return false;
					}
				});
			} catch (Exception e) {
				String message = "An error occurred while listing the available knowledge bases: "
						+ e.getCause().getMessage();
				System.out.println(message);
				logger.error(message, e);
			}
		} else if (commands.containsKey(LIST_COMMAND) && !commands.containsKey(RNS_COMMAND)) {
			try {
				String format = getSingleParam(commands, FORMAT_COMMAND);
				boolean pagination = commands.containsKey(PAGINATION_COMMAND);
				ConsoleKNSServerListVisitor listAllVisitor = null;
				if (format != null) {
					listAllVisitor = new ConsoleKNSServerListVisitor(format, pagination);
				} else {
					listAllVisitor = new ConsoleKNSServerListVisitor(pagination);
				}
				KBox.visit(listAllVisitor);
			} catch (UnknownHostException e) {
				String message = "An error occurred while listing the available resources. Check your connection.";
				System.out.println(message);
				logger.error(message, e);
			} catch (Exception e) {
				String message = "An error occurred while listing the available resources: " + e.getMessage();
				System.out.println(message);
				logger.error(message, e);
			}
		} else if (commands.containsKey(REMOVE_COMMAND) && commands.containsKey(RNS_COMMAND)) {
			String knsURL = getSingleParam(commands, REMOVE_COMMAND);
			try {
				System.out.println("Removing KNS " + knsURL);
				KBox.removeRNS(new URL(knsURL));
				System.out.println("KNS removed.");
			} catch (Exception e) {
				String message = "An error occurred while removing the KNS: " + knsURL + ".";
				System.out.println(message);
				logger.error(message, e);
			}
		} else if (commands.containsKey(RESOURCE_DIR_COMMAND)) {
			String resourceDir = getSingleParam(commands, RESOURCE_DIR_COMMAND);
			if (resourceDir != null) {
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
			String pattern = getSingleParam(commands, SEARCH_COMMAND);
			String format = getSingleParam(commands, FORMAT_COMMAND);
			String version = getSingleParam(commands, VERSION_COMMAND);
			SearchKBKNSVisitor searchVisitor = new SearchKBKNSVisitor(pattern, format, version,
					commands.containsKey(PAGINATION_COMMAND));
			try {
				KBox.visit(searchVisitor);
			} catch (Exception e) {
				String message = "An error occurred while enquiring searching using the pattern: " + pattern;
				System.out.println(message);
				logger.error(message, e);
			}
		} else if (commands.containsKey(INFO_COMMAND)) {
			String graph = getSingleParam(commands, INFO_COMMAND);
			String format = getSingleParam(commands, FORMAT_COMMAND);
			String version = getSingleParam(commands, VERSION_COMMAND);
			InfoKBKNSVisitor visitor = new InfoKBKNSVisitor(graph, format, version);
			try {
				KBox.visit(visitor);
			} catch (Exception e) {
				String message = "An error occurred while enquiring information from the graph " + graph;
				System.out.println(message);
				logger.error(message, e);
			}
		} else if (commands.containsKey(LOCATE_COMMAND) && !commands.containsKey(KB_COMMAND)) {
			String resourceURL = getSingleParam(commands, LOCATE_COMMAND);
			try {
				System.out.println(org.aksw.kbox.KBox.locate(new URL(resourceURL)));
			} catch (Exception e) {
				String message = "An error occurred while resolving the resource: " + resourceURL;
				System.out.println(message);
				logger.error(message, e);
			}
		} else if (commands.containsKey(LOCATE_COMMAND) && commands.containsKey(KB_COMMAND)) {
			String kbURL = getSingleParam(commands, KB_COMMAND);
			String format = getSingleParam(commands, FORMAT_COMMAND);
			String version = getSingleParam(commands, VERSION_COMMAND);
			try {
				if (format != null && version != null) {
					System.out.println(KBox.locate(new URL(kbURL), format, version));
				}
				if (format != null) {
					System.out.println(KBox.locate(new URL(kbURL), format));
				} else {
					System.out.println(KBox.locate(new URL(kbURL)));
				}
			} catch (Exception e) {
				String message = "An error occurred while resolving the KB: " + kbURL;
				System.out.println(message);
				logger.error(message, e);
			}
		} else if (commands.containsKey(SERVER_COMMAND) && commands.containsKey(KB_COMMAND)) {
			int port = 8080;
			String subDomain = "kbox";
			if (commands.containsKey(SERVER_COMMAND_PORT)) {
				port = Integer.parseInt(getSingleParam(commands, SERVER_COMMAND_PORT));
			}
			if (commands.containsKey(SEVER_COMMAND_SUBDOMAIN)) {
				subDomain = getSingleParam(commands, SEVER_COMMAND_SUBDOMAIN);
			}
			String graphNamesList = getSingleParam(commands, KB_COMMAND);
			String[] graphNames = graphNamesList.split(KB_COMMAND_SEPARATOR);
			boolean install = commands.containsKey(INSTALL_COMMAND);
			URL[] urls = new URL[graphNames.length];
			try {
				for (int i = 0; i < graphNames.length; i++) {
					urls[i] = new URL(graphNames[i]);
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
				URL webclient = Main.class.getResource("/web-client.zip");
				Install webClientInstall = new WebAppInstall(serverAddress);
				File webInterfaceDir = KBox.getResource(webclient, new ZipLocate(), webClientInstall, true);
				Server server = new Server(port, webInterfaceDir.getAbsolutePath(), subDomain, model, serverListener);
				server.start();
			} catch (KBDereferencingException e) {
				System.out.println(
						"Error installing KB: " + "The knowledge base could not be found in any of the KNS servers.");
				System.out.println("Check if the servers are online or if the requested resource exist.");
				logger.error(e);
			} catch (KBNotLocatedException e) {
				System.out.println(
						"Error installing KB: " + "The knowledge base could not be located.");
				System.out.println("Try to install it adding the pragma -install to your command.");
				logger.error(e);
			} catch (ResourceNotResolvedException e) {
				System.out.println(
						"Error installing KB: " + "The knowledge base could not be resolved.");
				System.out.println("Check if the servers are online or if the requested resource exist.");
				logger.error(e);
			} catch (ServerStartException e) {
				String message = "An error occurred while starting the server, "
						+ "check if the port is not being used by another "
						+ "application or if the parameters are valid.";
				System.out.println(message);
				logger.error(message, e);
			} catch (MalformedURLException e) {
				System.out.println(e.getMessage());
				logger.error(e);
			} catch (Exception e) {
				System.out.println(e);
				String message = "Error installing the Knowledge Bases.";
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
		System.out.println(
				"   * -convert <directory|file> [<destFile>] [-kb|-zip]\t - convert the content of a directory (default -kb).");
		System.out.println(
				"             -kb\t - into a kb file. ps: the directory might contain only RDF compatible file formats.");
		System.out.println("             -zip\t - into a zip file.");
		System.out.println("   * -convert <file> [<destFile>] -gzip\t - encode a given file.");
		System.out.println(
				"   * -sparql <query> (-kb <KB> | -server <URL>) [-install] [-json]\t - Query a given base (e.g. -sparql \"Select ...\" -kb \"KB1,KB2\")");
		System.out.println(
				"                                               \t - ps: use -install in case you want to enable the auto-dereference.");
		System.out.println(
				"   * -server [-port <port> (default 8080)] [-subDomain <subDomain> (default kbox)] -kb <KB> [-install] \t - Start an SPARQL endpoint in the given subDomain containing the given bases.");
		System.out.println("   * -list [/p]\t - List all available KNS services and knowledge bases.");
		System.out.println("   * -list -kns\t - List all availables KNS services.");
		System.out.println("   * -install <URL>\t - Install a given resource.");
		System.out.println("   * -install -kns <kns-URL>\t - Install a given KNS service.");
		System.out.println(
				"   * -install -kb <kb-URL> [-format <format> [-version <version>]]\t - Install a given knowledge base using the available KNS services to resolve it.");
		System.out.println("   * -install -kb <kb-URL> -file <kbFile>\t - Install a given kb file in a given URL.");
		System.out.println(
				"   * -install -kb <kb-URL> -kns <kns-URL> [-format <format> [-version <version>]]\t - Install a knowledge base from a a given KNS service with the specific format and version.");
		System.out.println("   * -remove -kns <kns-URL>\t - Remove a given KNS service.");
		System.out.println(
				"   * -info <kb-URL> [-format <format> [-version <version>]]\t - Gives the information about a specific KB.");
		System.out.println("   * -locate <URL>\t - returns the local address of the given resource.");
		System.out.println(
				"   * -locate -kb <kb-URL> [-format <format> [-version <version>]]\t - returns the local address of the given KB.");
		System.out.println(
				"   * -search <kb-URL-pattern> [-format <format> [-version <version>]] [/p]\t - Search for all kb-URL containing a given pattern.");
		System.out.println("   * -r-dir\t - Show the path to the resource folder.");
		System.out.println("   * -r-dir <resourceDir>\t - Change the path of the resource folder.");
		System.out.println("   * -version \t - display KBox version.");
	}

	private static void out(Map<String, String[]> commands, ResultSet rs) {
		if (commands.containsKey(SPARQL_QUERY_JSON_OUTPUT_FORMAT_COMMAND)) {
			ResultSetFormatter.outputAsJSON(System.out, rs);
		} else {
			ResultSetFormatter.out(System.out, rs);
		}
	}

	/**
	 * Retrieve the value of the first command's parameter.
	 * 
	 * @param commands
	 *            the parsed commands.
	 * @param command
	 *            the command to retrieve the parameter.
	 * 
	 * @return the value of the first command's parameter.
	 */
	public static String getSingleParam(Map<String, String[]> commands, String command) {
		if (commands.containsKey(command)) {
			return commands.get(command)[0];
		}
		return null;
	}

	/**
	 * Command line parser.
	 * 
	 * @param args
	 *            a set o arguments received by command line.
	 * 
	 * @return a Map containing the parsed arguments.
	 */
	public static Map<String, String[]> parse(String[] args) {
		Map<String, String[]> map = new HashMap<String, String[]>();
		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith(COMMAND_PRAGMA) || args[i].startsWith(SUB_COMMAND_PRAGMA)) { // is
																								// it
																								// a
																								// command
				map.put(args[i], new String[] { null, null });
				int j = i + 1;
				while (j < args.length && !args[j].startsWith(COMMAND_PRAGMA)) {
					map.get(args[i])[(j - i) - 1] = args[j];
					j++;
				}
			}
		}
		return map;
	}
}
