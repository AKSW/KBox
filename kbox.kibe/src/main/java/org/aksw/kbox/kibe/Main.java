package org.aksw.kbox.kibe;

import org.aksw.kbox.Install;
import org.aksw.kbox.ZipLocate;
import org.aksw.kbox.apple.AppInstall;
import org.aksw.kbox.fusca.Listener;
import org.aksw.kbox.fusca.Server;
import org.aksw.kbox.fusca.exception.ServerStartException;
import org.aksw.kbox.kibe.console.ConsoleInstallInputStreamFactory;
import org.aksw.kbox.kibe.exception.KBDereferencingException;
import org.aksw.kbox.kibe.exception.KBNotLocatedException;
import org.aksw.kbox.kns.CustomKNSServerList;
import org.aksw.kbox.kns.InstallFactory;
import org.aksw.kbox.kns.KNSServerListVisitor;
import org.aksw.kbox.kns.ServerAddress;
import org.aksw.kbox.kns.Source;
import org.aksw.kbox.kns.exception.ResourceNotResolvedException;
import org.aksw.kbox.utils.GzipUtils;
import org.aksw.kbox.utils.URLUtils;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Main {

	private final static Logger logger = Logger.getLogger(Main.class);

	// CONSTANTS
	private final static String VERSION = "v0.0.2-beta";

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

	private final static String KB_WEB_CLIENT = "http://kbox.aksw.org/webclient";

	// COMMANDS
	private final static String INSTALL_COMMAND = "install";
	private final static String KB_COMMAND = "kb";
	private final static String KN_COMMAND = "kn";
	private final static String KNS_COMMAND = "kns";
	private final static String REMOVE_COMMAND = "remove";
	private final static String SERVER_COMMAND = "server";
	private final static String LIST_COMMAND = "list";
	private final static String PAGINATION_COMMAND = "/p";
	private final static String SPARQL_QUERY_COMMAND = "sparql";
	private final static String SPARQL_QUERY_JSON_OUTPUT_FORMAT_COMMAND = "-json";
	private final static String FILE_COMMAND = "file";
	private final static String INFO_COMMAND = "info";
	private final static String SEARCH_COMMAND = "search";
	private final static String CONVERT_COMMAND = "convert";
	private final static String ZIP_ENCODE_COMMAND = "zip";
	private final static String GZIP_ENCODE_COMMAND = "gzip";
	private final static String VERSION_COMMAND = "-version";
	private final static String RESOURCE_DIR_COMMAND = "r-dir";
	private final static String SERVER_COMMAND_PORT = "port";
	private final static String LOCATE_COMMAND = "locate";
	private final static String SEVER_COMMAND_SUBDOMAIN = "subDomain";
	private final static String FORMAT_COMMAND = "-format";
	private final static String RDF_COMMAND = "rdf";
	private final static String TARGET_COMMAND = "target";

	public static void main(String[] args) {
		Map<String, String[]> commands = parse(args);
		ConsoleInstallInputStreamFactory inputStreamFactory = new ConsoleInstallInputStreamFactory();
		JSONSerializer jsonSerializer = JSONSerializer.getInstance();
		jsonSerializer.containsJsonOutputCommand(commands);
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
				jsonSerializer.printOutput("Installing resource " + resource);
				URL url = new URL(resource);
				org.aksw.kbox.KBox.install(url);
				String msg = "Resource installed.";
				jsonSerializer.printOutput(msg);
				jsonSerializer.printInstallCommandJsonResponse( msg);
			} catch (Exception e) {
				String message = "Error installing the resource " + resource + ": " + e.getMessage();
				jsonSerializer.printOutput(message);
				jsonSerializer.printErrorInJsonFormat(message, false);
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
				jsonSerializer.printOutput("Installing KB " + kb2Install);
				KBox.install(file, kbURL, inputStreamFactory);
				String msg = "KB installed.";
				jsonSerializer.printOutput(msg);
				jsonSerializer.printInstallCommandJsonResponse(msg);
			} catch (Exception e) {
				String message = "Error installing the knowledge base " + kb2Install + ": " + e.getMessage();
				jsonSerializer.printOutput(message);
				jsonSerializer.printErrorInJsonFormat(message, false);
				logger.error(message, e);
			}
		} else if (commands.containsKey(INSTALL_COMMAND) && commands.containsKey(KB_COMMAND)
				&& commands.containsKey(KNS_COMMAND)) {
			String knsServer = getSingleParam(commands, KNS_COMMAND);
			String kbURL = getSingleParam(commands, KB_COMMAND);
			String version = getSingleParam(commands, VERSION_COMMAND);
			try {
				jsonSerializer.printOutput("Installing KB " + kbURL + " from KNS " + knsServer);
				KBox.installFromKNSServer(new URL(kbURL), new URL(knsServer), KBox.KIBE_FORMAT, version, inputStreamFactory);
				String message = "KB installed.";
				jsonSerializer.printOutput(message);
				jsonSerializer.printInstallCommandJsonResponse(message);
			} catch (MalformedURLException e) {
				jsonSerializer.printOutput(e.getMessage());
				jsonSerializer.printErrorInJsonFormat(e.getMessage(), false);
				logger.error(e);
			} catch (KBNotLocatedException e) {
				String message = "The knowledge base " + kbURL + " is not available in " + knsServer + ".";
				jsonSerializer.printOutput(message);
				jsonSerializer.printErrorInJsonFormat(message, false);
			} catch (Exception e) {
				String message = "Error installing knowledge base " + kbURL + " from " + knsServer + ".";
				jsonSerializer.printOutput(message);
				jsonSerializer.printErrorInJsonFormat(message, false);
				logger.error(message, e);
			}
		} else if (commands.containsKey(INSTALL_COMMAND) && commands.containsKey(KN_COMMAND)
				&& commands.containsKey(KNS_COMMAND)) {
			String knsServer = getSingleParam(commands, KNS_COMMAND);
			String knURL = getSingleParam(commands, KN_COMMAND);
			String format = getSingleParam(commands, FORMAT_COMMAND);
			String version = getSingleParam(commands, VERSION_COMMAND);
			try {
				jsonSerializer.printOutput("Installing KN " + knURL + " from KNS " + knsServer);
				KBox.installFromKNSServer(new URL(knURL), new URL(knsServer), format, version, inputStreamFactory);
				String message = "KN installed.";
				jsonSerializer.printOutput(message);
				jsonSerializer.printInstallCommandJsonResponse(message);
			} catch (MalformedURLException e) {
				jsonSerializer.printOutput(e.getMessage());
				jsonSerializer.printErrorInJsonFormat(e.getMessage(), false);
				logger.error(e);
			} catch (KBNotLocatedException e) {
				String message = "The knowledge name " + knURL + " is not available in " + knsServer + ".";
				jsonSerializer.printOutput(message);
				jsonSerializer.printErrorInJsonFormat(message, false);
			} catch (Exception e) {
				String message = "Error installing knowledge name " + knURL + " from " + knsServer + ".";
				jsonSerializer.printOutput(message);
				jsonSerializer.printErrorInJsonFormat(message, false);
				logger.error(message, e);
			}
		} else if (commands.containsKey(INSTALL_COMMAND) && commands.containsKey(RDF_COMMAND)
				&& commands.containsKey(KB_COMMAND)) {
			String install = DefaultInstallFactory.RDF2KB;
			String source = getSingleParam(commands, RDF_COMMAND);
			if(commands.containsKey(INSTALL_COMMAND)) {
				install = getSingleParam(commands, INSTALL_COMMAND, install);
			}
			String kbURL = getSingleParam(commands, KB_COMMAND);
			String version = getSingleParam(commands, VERSION_COMMAND);
			try {
				String[] filePaths = source.split(KB_COMMAND_SEPARATOR);
				List<URL> inputRDFFiles = new java.util.ArrayList<URL>();
				for(String filePath : filePaths) {
					if(!filePath.startsWith("http")) {
						File sourceFile = new File(filePath);
						if(sourceFile != null && sourceFile.isDirectory()) {
							inputRDFFiles.addAll(Arrays.asList(URLUtils.fileToURL(sourceFile.listFiles())));
						} else {
							inputRDFFiles.add(sourceFile.toURI().toURL());
						}
					} else {
						URL fileURL = new URL(filePath);
						inputRDFFiles.add(fileURL);
					}
				}
				jsonSerializer.printOutput("Installing KB " + kbURL + " from files " + source);
				InstallFactory installFactory = new DefaultInstallFactory();
				AppInstall installMethod = installFactory.get(install);
				KBox.install(inputRDFFiles.toArray(new URL[inputRDFFiles.size()]), 
						new URL(kbURL),
						KBox.KIBE_FORMAT,
						version,
						installMethod,
						inputStreamFactory);
				String message = "KN installed.";
				jsonSerializer.printOutput(message);
				jsonSerializer.printInstallCommandJsonResponse(message);
			} catch (MalformedURLException e) {
				String message = e.getMessage();
				jsonSerializer.printOutput(message);
				jsonSerializer.printErrorInJsonFormat(message, false);
				logger.error(e);
			} catch (Exception e) {
				String message = "Error installing knowledge name " + kbURL + " from files " + source + ".";
				jsonSerializer.printOutput(message);
				jsonSerializer.printErrorInJsonFormat(message, false);
				logger.error(message, e);
			}
		} else if (!commands.containsKey(SPARQL_QUERY_COMMAND) && !commands.containsKey(SERVER_COMMAND)
				&& commands.containsKey(INSTALL_COMMAND) && commands.containsKey(KB_COMMAND)) {
			String graphNamesList = getSingleParam(commands, KB_COMMAND);
			String version = getSingleParam(commands, VERSION_COMMAND);
			String[] graphNames = graphNamesList.split(KB_COMMAND_SEPARATOR);
			URL[] urls;
			try {
				urls = URLUtils.stringToURL(graphNames);
				for (URL resourceName : urls) {
					try {
						File kbFile = KBox.getResource(resourceName, KBox.KIBE_FORMAT, version, true);
						if(kbFile != null) {
							String message = resourceName + " KB installed.";
							jsonSerializer.printOutput(message);
							jsonSerializer.printInstallCommandJsonResponse(message);
						} else {
							String message = resourceName + " KB could NOT be installed.";
							jsonSerializer.printOutput(message);
							jsonSerializer.printErrorInJsonFormat(message, false);
						}
					} catch (MalformedURLException e) {
						String message = e.getMessage();
						jsonSerializer.printOutput(message);
						jsonSerializer.printErrorInJsonFormat(message, false);
						logger.error(message, e);
					} catch (Exception e) {
						String message = "The knowledge base could not be found: URL:" + resourceName;
						if (version != null) {
							message += ", " + "version: " + version;
						}
						jsonSerializer.printOutput(message);
						jsonSerializer.printErrorInJsonFormat(message, false);
						logger.error(message, e);
					}
				}
			} catch (Exception e) {
				String message = "An error occurred while parsing the KB Name list \"" + graphNames + "\": "
						+ e.getMessage();
				jsonSerializer.printOutput(message);
				jsonSerializer.printErrorInJsonFormat(message, false);
				logger.error(message, e);
			}
		} else if (!commands.containsKey(SPARQL_QUERY_COMMAND) && !commands.containsKey(SERVER_COMMAND)
				&& commands.containsKey(INSTALL_COMMAND) && commands.containsKey(FORMAT_COMMAND)) {
			String format = getSingleParam(commands, FORMAT_COMMAND);
			String version = getSingleParam(commands, VERSION_COMMAND);
			String graphNames = getSingleParam(commands, INSTALL_COMMAND);
			URL[] urls;
			try {
				urls = URLUtils.stringToURL(graphNames);
				for (URL resourceName : urls) {
					try {
						File kbFile = KBox.getResource(resourceName, format, version, true);
						String message;
						if (kbFile != null) {
							message = resourceName + " KB installed.";
							jsonSerializer.printOutput(message);
							jsonSerializer.printInstallCommandJsonResponse(message);
						} else {
							message = resourceName + " KB could NOT be installed.";
							jsonSerializer.printOutput(message);
							jsonSerializer.printErrorInJsonFormat(message, false);
						}
					} catch (MalformedURLException e) {
						String message = e.getMessage();
						jsonSerializer.printOutput(message);
						jsonSerializer.printErrorInJsonFormat(message, false);
						logger.error(message, e);
					} catch (Exception e) {
						String message = "The knowledge base could not be found: URL:" + resourceName;
						if (version != null) {
							message += ", " + "vesion: " + version;
						}
						jsonSerializer.printOutput(message);
						jsonSerializer.printErrorInJsonFormat(message, false);
						logger.error(message, e);
					}
				}
			} catch (Exception e) {
				String message = "An error occurred while parsing the KB Name list \"" + graphNames + "\": "
						+ e.getMessage();
				jsonSerializer.printOutput(message);
				jsonSerializer.printErrorInJsonFormat(message, false);
				logger.error(message, e);
			}
		} else if (commands.containsKey(INSTALL_COMMAND) && commands.containsKey(KNS_COMMAND)) {
			String url = getSingleParam(commands, KNS_COMMAND);
			try {
				URL knsURL = new URL(url);
				jsonSerializer.printOutput("Installing KNS " + url);
				KBox.installKNS(knsURL);
				String message = "KNS installed.";
				jsonSerializer.printOutput(message);
				jsonSerializer.printInstallCommandJsonResponse(message);
			} catch (MalformedURLException e) {
				String message = e.getMessage();
				jsonSerializer.printOutput(message);
				jsonSerializer.printErrorInJsonFormat(message, false);
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
					String message = "Error executing query '" + sparql + "': " + e.getMessage();
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
		} else if (commands.containsKey(LIST_COMMAND) && commands.containsKey(KNS_COMMAND)) {
			jsonSerializer.printOutput("KNS table list");
			CustomKNSServerList knsServerList = new CustomKNSServerList();
			List<String> knsList = new ArrayList<>();
			try {
				knsServerList.visit((KNSServerListVisitor) knsServer -> {
					knsList.add(knsServer.getURL().toString());
					jsonSerializer.printOutput("\t - " + knsServer.getURL());
					return true;
				});
				jsonSerializer.printKnsListJsonFormat(knsList, "KNS table list");
			} catch (Exception e) {
				String message = "An error occurred while listing the available knowledge bases: "
						+ e.getCause().getMessage();
				jsonSerializer.printOutput(message);
				jsonSerializer.printErrorInJsonFormat(message, true);
				logger.error(message, e);
			}
		} else if (commands.containsKey(LIST_COMMAND) && !commands.containsKey(KNS_COMMAND)) {
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
				jsonSerializer.printVisitedKNAsJsonResponse("visited all KNs.");
			} catch (UnknownHostException e) {
				String message = "An error occurred while listing the available resources. Check your connection.";
				jsonSerializer.printOutput(message);
				logger.error(message, e);
				jsonSerializer.printErrorInJsonFormat(message, true);
			} catch (Exception e) {
				String message = "An error occurred while listing the available resources: " + e.getMessage();
				jsonSerializer.printOutput(message);
				logger.error(message, e);
				jsonSerializer.printErrorInJsonFormat(message, true);
			}
		} else if (commands.containsKey(REMOVE_COMMAND) && commands.containsKey(KNS_COMMAND)) {
			String knsURL = getSingleParam(commands, KNS_COMMAND);
			try {
				jsonSerializer.printOutput("Removing KNS " + knsURL);
				KBox.removeKNS(new URL(knsURL));
				jsonSerializer.printOutput("KNS removed.");
				jsonSerializer.printRemoveCommandJsonResponse("KNS removed.");
			} catch (Exception e) {
				String message = "An error occurred while removing the KNS: " + knsURL + ".";
				jsonSerializer.printOutput(message);
				jsonSerializer.printErrorInJsonFormat(message, true);
				logger.error(message, e);
			}
		} else if (commands.containsKey(RESOURCE_DIR_COMMAND)) {
			String resourceDir = getSingleParam(commands, RESOURCE_DIR_COMMAND);
			if (resourceDir != null) {
				try {
					KBox.setResourceFolder(resourceDir);
					String message = "Resource directory redirected to " + resourceDir + ".";
					jsonSerializer.printOutput(message);
					jsonSerializer.printResourceDirectoryAsJsonResponse(message, resourceDir);
				} catch (Exception e) {
					String errorMessage = "Error changing KBox resource repository to " + resourceDir + ".";
					jsonSerializer.printOutput(errorMessage);
					jsonSerializer.printErrorInJsonFormat(errorMessage, true);
					logger.error(errorMessage, e);
				}
			} else {
				resourceDir = KBox.getResourceFolder();
				String message = "Your current resource directory is: " + resourceDir;
				jsonSerializer.printOutput(message);
				jsonSerializer.printResourceDirectoryAsJsonResponse(message, resourceDir);
			}
		} else if (commands.containsKey(SEARCH_COMMAND)) {
			String pattern = getSingleParam(commands, SEARCH_COMMAND);
			String format = getSingleParam(commands, FORMAT_COMMAND);
			String version = getSingleParam(commands, VERSION_COMMAND);
			SearchKBKNSVisitor searchVisitor = new SearchKBKNSVisitor(pattern, format, version,
					commands.containsKey(PAGINATION_COMMAND));
			try {
				KBox.visit(searchVisitor);
				jsonSerializer.printVisitedKNAsJsonResponse("searching completed.");
			} catch (Exception e) {
				String message = "An error occurred while enquiring search using the pattern: " + pattern;
				jsonSerializer.printOutput(message);
				logger.error(message, e);
				jsonSerializer.printErrorInJsonFormat(message, true);
			}
		} else if (commands.containsKey(INFO_COMMAND)) {
			String kn = getSingleParam(commands, INFO_COMMAND);
			String format = getSingleParam(commands, FORMAT_COMMAND);
			String version = getSingleParam(commands, VERSION_COMMAND);
			InfoKBKNSVisitor visitor = new InfoKBKNSVisitor(kn, format, version);
			try {
				KBox.visit(visitor);
				jsonSerializer.printKNInfoAsJsonResponse("Information has been fetched.");
			} catch (Exception e) {
				String message = "An error occurred while enquiring information from the KN " + kn;
				jsonSerializer.printOutput(message);
				jsonSerializer.printErrorInJsonFormat(message, true);
				logger.error(message, e);
			}
		} else if (commands.containsKey(LOCATE_COMMAND) && !commands.containsKey(KB_COMMAND)
				&& !commands.containsKey(FORMAT_COMMAND)) {
			String resourceURL = getSingleParam(commands, LOCATE_COMMAND);
			try {
				String path = org.aksw.kbox.KBox.locate(new URL(resourceURL)).getAbsolutePath();
				jsonSerializer.printOutput(path);
				jsonSerializer.printLocateCommandJsonResponse("Action Completed.",path);
			} catch (Exception e) {
				String message = "An error occurred while resolving the resource: " + resourceURL;
				jsonSerializer.printOutput(message);
				jsonSerializer.printErrorInJsonFormat(message, true);
				logger.error(message, e);
			}
		} else if (commands.containsKey(LOCATE_COMMAND) && commands.containsKey(KB_COMMAND)) {
			String kbURL = getSingleParam(commands, KB_COMMAND);
			String version = getSingleParam(commands, VERSION_COMMAND);
			try {
				String path = KBox.locate(new URL(kbURL), KBox.KIBE_FORMAT, version).getAbsolutePath();
				jsonSerializer.printOutput(path);
				jsonSerializer.printLocateCommandJsonResponse("Action Completed.", path);
			} catch (Exception e) {
				String message = "An error occurred while resolving the KB: " + kbURL;
				jsonSerializer.printOutput(message);
				jsonSerializer.printErrorInJsonFormat(message, true);
				logger.error(message, e);
			}
		} else if (commands.containsKey(LOCATE_COMMAND) && commands.containsKey(FORMAT_COMMAND)) {
			String kbURL = getSingleParam(commands, LOCATE_COMMAND);
			String format = getSingleParam(commands, FORMAT_COMMAND);
			String version = getSingleParam(commands, VERSION_COMMAND);
			try {
				String path = KBox.locate(new URL(kbURL), format, version).getAbsolutePath();
				jsonSerializer.printOutput(path);
				jsonSerializer.printLocateCommandJsonResponse("Action Completed.", path);
			} catch (Exception e) {
				String message = "An error occurred while resolving the KB: " + kbURL;
				jsonSerializer.printOutput(message);
				jsonSerializer.printErrorInJsonFormat(message, true);
				logger.error(message, e);
			}
		} else if (commands.containsKey(LOCATE_COMMAND) && commands.containsKey(KN_COMMAND)) {
			String kbURL = getSingleParam(commands, KN_COMMAND);
			String format = getSingleParam(commands, FORMAT_COMMAND);
			String version = getSingleParam(commands, VERSION_COMMAND);
			try {
				String path = KBox.locate(new URL(kbURL), format, version).getAbsolutePath();
				jsonSerializer.printOutput(path);
				jsonSerializer.printLocateCommandJsonResponse("Action Completed.", path);
			} catch (Exception e) {
				String message = "An error occurred while resolving the KN: " + kbURL;
				jsonSerializer.printOutput(message);
				jsonSerializer.printErrorInJsonFormat(message, true);
				logger.error(message, e);
			}
		} else if (commands.containsKey(SERVER_COMMAND)) {
			int port = 8080;
			String subDomain = "kbox";
			if (commands.containsKey(SERVER_COMMAND_PORT)) {
				port = Integer.parseInt(getSingleParam(commands, SERVER_COMMAND_PORT));
			}
			if (commands.containsKey(SEVER_COMMAND_SUBDOMAIN)) {
				subDomain = getSingleParam(commands, SEVER_COMMAND_SUBDOMAIN);
			}
			try {
				System.out.println("Loading Model...");
				Model model = null;
				if(commands.containsKey(KB_COMMAND)) {
					String graphNamesList = getSingleParam(commands, KB_COMMAND);
					String[] graphNames = graphNamesList.split(KB_COMMAND_SEPARATOR);
					boolean install = commands.containsKey(INSTALL_COMMAND);
					URL[] urls = new URL[graphNames.length];
					for (int i = 0; i < graphNames.length; i++) {
						urls[i] = new URL(graphNames[i]);
					}
					model = KBox.createModel(inputStreamFactory, install, urls);
				} if(commands.containsKey(TARGET_COMMAND)) {
					String targetArray = getSingleParam(commands, TARGET_COMMAND);
					List<Source> sources = Source.toTarget(targetArray);
					model = KBox.createTempModel(inputStreamFactory, sources.toArray(new Source[sources.size()]));
				}  else if (commands.containsKey(RDF_COMMAND)){
					String install = DefaultInstallFactory.RDF2KB;
					if(commands.containsKey(INSTALL_COMMAND)) {
						install = getSingleParam(commands, INSTALL_COMMAND, install);
					}
					String fileNamesList = getSingleParam(commands, RDF_COMMAND);
					String[] filePaths = fileNamesList.split(KB_COMMAND_SEPARATOR);
					List<URL> inputRDFFiles = new java.util.ArrayList<URL>();
					for(String filePath : filePaths) {
						if(!(filePath.startsWith("http") || filePath.startsWith("ftp"))) {
							File sourceFile = new File(filePath);
							if(sourceFile != null && sourceFile.isDirectory()) {
								inputRDFFiles.addAll(Arrays.asList(URLUtils.fileToURL(sourceFile.listFiles())));
							} else {
								inputRDFFiles.add(sourceFile.toURI().toURL());
							}
						} else {
							URL fileURL = new URL(filePath);
							inputRDFFiles.add(fileURL);
						}
					}
					model = KBox.createTempModel( 
							inputRDFFiles.toArray(new URL[inputRDFFiles.size()]),
							install,
							inputStreamFactory);
				}
				final int servicePort = port;
				final String serverAddress = "http://localhost:" + servicePort + "/" + subDomain + "/sparql";
				Listener serverListener = new Listener() {
					@Override
					public void starting() {
						System.out.println("Publishing service on " + serverAddress);
					}

					@Override
					public void started() {
						System.out.println("SPARQL client accessible at http://localhost:" + servicePort);
						System.out.println("Service up and running ;-) ...");
					}
				};
				URL webclient = Main.class.getResource("/web-client.zip");
				URL webClientURL = new URL(KB_WEB_CLIENT);
				Install webClientInstall = new WebAppInstall(serverAddress);
				File webInterfaceDir = KBox.getResource(webclient, webClientURL, new ZipLocate(), webClientInstall, true);
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
		if (!JSONSerializer.getInstance().getIsJsonOutput()) {
			System.out.println("KBox version " + VERSION);
		} else {
			JSONSerializer.getInstance().printKBoxVersionAsJsonResponse("KBox Version.", VERSION);
		}
	}

	public static void printHelp() {
		System.out.println("KBox.jar <command> [option]");
		System.out.println("Where [command] is:");
		System.out.println(
				"   * convert <directory|file> [<destFile>] [kb|zip]\t - convert the content of a directory (default kb).");
		System.out.println(
				"             kb\t - into a kb file. ps: the directory might contain only RDF compatible file formats.");
		System.out.println("             zip\t - into a zip file.");
		System.out.println("   * convert <file> [<destFile>] gzip\t - encode a given file.");
		System.out.println(
				"   * sparql <query> (kb <KB> | server <URL>) [install] [-json]\t - Query a given knowledge base (e.g. sparql \"Select ...\" kb \"KB1,KB2\")");
		System.out.println(
				"                                               \t - ps: use -install in case you want to enable the auto-dereference.");
		System.out.println(
				"   * server [port <port> (default 8080)] [subDomain <subDomain> (default kbox)] kb <kb-URL> [install] \t - Start an SPARQL endpoint in the given subDomain containing the given bases.");
		System.out.println(
				"   * server [port <port> (default 8080)] [subDomain <subDomain> (default kbox)] rdf <directories|files> [install [install]]\t - Start an SPARQL endpoint in the given subDomain containing the given RDF files.");
		System.out.println(
				"   * server [port <port> (default 8080)] [subDomain <subDomain> (default kbox)] target <target>\t - Start an SPARQL endpoint in the given subDomain containing the target RDF files.");
		System.out.println("   * list [/p]\t - List all available KNS services and knowledge bases.");
		System.out.println("   * list kns\t - List all available KNS services.");
		System.out.println("   * install <URL>\t - Install a given resource.");
		System.out.println("   * install kns <kns-URL>\t - Install a given KNS service.");
		System.out.println(
				"   * install kb <kb-URL> [-version <version>]\t - Install a given knowledge base using the available KNS services to resolve it.");
		System.out.println("   * install kb <kb-URL> file <kbFile>\t - Install a given kb file in a given Kb-URL.");
		System.out.println(
				"   * install kb <kb-URL> kns <kns-URL> [-version <version>]\t - Install a knowledge base from a a given KNS service with the specific version.");
		System.out.println(
				"   * install [install] kb <kb-URL> rdf <directories|files> [-version <version>]\t - Install a knowledge base from a a given RDF files with the specific version.");
		System.out.println(
				"   * install kn <kn-URL> [-format [-version <version>]]\t - Install a given knowledge base using the available KNS services to resolve it.");
		System.out.println("   * remove kns <kns-URL>\t - Remove a given KNS service.");
		System.out.println(
				"   * info <URL> -format <format> -version <version>]]\t - Gives the information about a specific KB.");
		System.out.println("   * locate <URL>\t - returns the local address of the given resource.");
		System.out.println(
				"   * locate kb <kb-URL> -version <version>]\t - returns the local address of the given KB.");
		System.out.println(
				"   * locate kn <kn-URL> -format -version <version>]]\t - returns the local address of the given KB.");
		System.out.println(
				"   * search <kn-URL-pattern> [-format <format> [-version <version>]] [/p]\t - Search for all kb-URL containing a given pattern.");
		System.out.println("   * r-dir\t - Show the path to the resource folder.");
		System.out.println("   * r-dir <resourceDir>\t - Change the path of the resource folder.");
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
	 * @param defaultValue
	 *            the defaultValue to return in case command value is null.
	 *            
	 * @return the value of the first command's parameter or defaultValue in case it is null.
	 */
	public static String getSingleParam(Map<String, String[]> commands, String command, String defaultValue) {
		String value = getSingleParam(commands, command);
		if (value == null) {
			return defaultValue;
		}
		return null;
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
	 * Retrieve the value of the first command's parameter.
	 * 
	 * @param commands
	 *            the parsed commands.
	 * @param command
	 *            the command to retrieve the parameter.
	 * 
	 * @return the value of the first command's parameter.
	 */
	public static String getParam(Map<String, String[]> commands, String command, int i) {
		if (commands.containsKey(command) && commands.get(command).length > i + 1) {
			return commands.get(command)[i];
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
		Set<String> commandList = new HashSet<>();
		addCommands(commandList);
		for (int i = 0; i < args.length; i++) {
			if (commandList.contains(args[i]) || args[i].startsWith(SUB_COMMAND_PRAGMA)) { // is
																								// it
																								// a
																								// command
				map.put(args[i], new String[] { null, null });
				int j = i + 1;
				while (j < args.length && !commandList.contains(args[j])) {
					map.get(args[i])[(j - i) - 1] = args[j];
					j++;
				}
			}
		}
		return map;
	}

	private static void addCommands(Set<String> commandList) {
		commandList.add(INSTALL_COMMAND);
		commandList.add(KB_COMMAND);
		commandList.add(KN_COMMAND);
		commandList.add(KNS_COMMAND);
		commandList.add(REMOVE_COMMAND);
		commandList.add(LIST_COMMAND);
		commandList.add(PAGINATION_COMMAND);
		commandList.add(SPARQL_QUERY_COMMAND);
		commandList.add(SPARQL_QUERY_JSON_OUTPUT_FORMAT_COMMAND);
		commandList.add(FILE_COMMAND);
		commandList.add(INFO_COMMAND);
		commandList.add(SEARCH_COMMAND);
		commandList.add(CONVERT_COMMAND);
		commandList.add(ZIP_ENCODE_COMMAND);
		commandList.add(GZIP_ENCODE_COMMAND);
		commandList.add(VERSION_COMMAND);
		commandList.add(RESOURCE_DIR_COMMAND);
		commandList.add(SERVER_COMMAND_PORT);
		commandList.add(LOCATE_COMMAND);
		commandList.add(SEVER_COMMAND_SUBDOMAIN);
		commandList.add(FORMAT_COMMAND);
		commandList.add(RDF_COMMAND);
		commandList.add(TARGET_COMMAND);
		commandList.add("-o");
	}

}
