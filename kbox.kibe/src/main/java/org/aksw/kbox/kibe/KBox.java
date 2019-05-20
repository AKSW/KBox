package org.aksw.kbox.kibe;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.naming.spi.Resolver;

import org.aksw.kbox.InputStreamFactory;
import org.aksw.kbox.apple.AppInstall;
import org.aksw.kbox.apple.AppLocate;
import org.aksw.kbox.apple.Locate;
import org.aksw.kbox.apple.ResourceAppInstall;
import org.aksw.kbox.apple.ZipAppInstall;
import org.aksw.kbox.kibe.console.ConsoleInstallInputStreamFactory;
import org.aksw.kbox.kibe.exception.KBDereferencingException;
import org.aksw.kbox.kibe.exception.KBNotLocatedException;
import org.aksw.kbox.kibe.exception.KBNotResolvedException;
import org.aksw.kbox.kibe.tdb.TDB;
import org.aksw.kbox.kns.InstallFactory;
import org.aksw.kbox.kns.KN;
import org.aksw.kbox.kns.KNSServerList;
import org.aksw.kbox.kns.KNSServerListVisitor;
import org.aksw.kbox.kns.ServerAddress;
import org.aksw.kbox.kns.exception.ResourceDereferencingException;
import org.aksw.kbox.kns.exception.ResourceNotResolvedException;
import org.aksw.kbox.utils.URLUtils;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.zeroturnaround.zip.ZipUtil;

/**
 * 
 * @author {@linkplain http://emarx.org}
 *
 */
public class KBox extends org.aksw.kbox.kns.KBox {

	public final static String KIBE_FORMAT = "kibe";
	public final static String KIBE_VERSION = "0";
	
	/**
	 * Creates a mirror for the given file in a given {@link URL}. This function allows
	 * KBox to serve files to applications, acting as proxy to the mirrored
	 * file. The file that is published in a give {@link URL} will be located when the
	 * client execute the function {@link KBox#getResource(URL)}.
	 *
	 * @param knsServer the {@link URL} of the KNS server that will be used in resource resolving.
	 * @param url the {@link URL} that will be resolved.
	 * @param resolver the resolver of the given KNS and resource'S {@link URL}.
	 * @param install a customized method for installation.
	 * @param isFactory a {@link InputStreamFactory} to be used to create a stream connection with the resolved resource's {@link URL}.
	 * 
	 * @throws ResourceNotResolvedException if the given resource can not be resolved.
	 * @throws Exception if the resource does not exist or can not be copied or some
	 *             error occurs during the resource publication.
	 */
	public static void install(URL knsServer, URL resourceURL, InstallFactory install, InputStreamFactory isFactory)
			throws ResourceNotResolvedException, Exception {
		KN resolvedKN = resolve(knsServer, resourceURL, KBox.KIBE_FORMAT, KBox.KIBE_VERSION);
		notNull(new KBNotResolvedException(resourceURL.toString()), resolvedKN);
		install(resolvedKN.getTargets().get(0).getURL(),
				resourceURL,
				KBox.KIBE_FORMAT,
				KBox.KIBE_VERSION,
				new ZipAppInstall(),
				isFactory);
	}
	
	
	/**
	 * Creates a mirror for the given file in a given {@link URL}. This function allows
	 * KBox to serve files to applications, acting as proxy to the mirrored
	 * file. The file that is published in a give {@link URL} will be located when the
	 * client execute the function {@link KBox#getResource(URL)}.
	 * 
	 * @param knsServerURL a {@link KNService}'s {@link URL} that will be used to resolve the given resource's {@link URL}.
	 * @param resourceURL the {@link URL} that will be resolved.
	 * 
	 * @throws ResourceNotResolvedException if the given KB can not be resolved.
	 * @throws Exception if the resource does not exist or can not be copied or some
	 *             error occurs during the resource publication.
	 */
	public static void install(URL knsServerURL, URL resourceURL, InputStreamFactory isFactory)
			throws ResourceNotResolvedException, Exception {
		KN resolvedKN = resolve(knsServerURL, resourceURL, KBox.KIBE_FORMAT, KBox.KIBE_VERSION);
		notNull(new KBNotResolvedException(resourceURL.toString()), resolvedKN);
		install(resolvedKN.getTargets().get(0).getURL(),
				resourceURL,
				KBox.KIBE_FORMAT,
				KBox.KIBE_VERSION,
				new ZipAppInstall(),
				isFactory);
	}
	
	/**
	 * Creates a mirror for the given file in a given {@link URL}. This function allows
	 * KBox to serve files to applications, acting as proxy to the mirrored
	 * file. The file that is published in a give {@link URL} will be located when the
	 * client execute the function {@link KBox#getResource(URL)}.
	 * 
	 * @param knsServerURL a {@link RNService}'s {@link URL} that will be used to resolve the given resource's {@link URL}.
	 * @param resourceURL the {@link URL} that will be resolved.
	 * 
	 * @throws ResourceNotResolvedException if the given KB can not be resolved.
	 * @throws Exception if the resource does not exist or can not be copied or some
	 *             error occurs during the resource publication.
	 */
	public static void install(URL knsServerURL, URL resourceURL, InstallFactory installFactory)
			throws ResourceNotResolvedException, Exception {
		KN resolvedKN = resolve(knsServerURL, resourceURL, KBox.KIBE_FORMAT, KBox.KIBE_VERSION);
		notNull(new ResourceNotResolvedException(resourceURL.toString()), resolvedKN);
		install(resolvedKN.getTargets().get(0).getURL(),
				resourceURL,
				KBox.KIBE_FORMAT, 
				KBox.KIBE_VERSION,
				new ZipAppInstall(),
				new ConsoleInstallInputStreamFactory());
	}
	
	/**
	 * Creates a mirror for the given file in a given {@link URL}. This function allows
	 * KBox to serve files to applications, acting as proxy to the mirrored
	 * file. The file that is published in a give {@link URL} will be located when the
	 * client execute the function {@link KBox#getResource(URL)}.
	 * 
	 * @param source the {@link URL} of the file that is going to be published at the
	 *        given {@link URL}.
	 * @param dest the {@link URL} where the file is going to be published.
	 * @param install a customized {@link AppInstall} method for installation.
	 * 
	 * @throws Exception if the resource does not exist or can not be copied or some
	 *             error occurs during the resource publication.
	 */
	public static void install(URL source, URL dest, String format, AppInstall install)
			throws Exception {
		install.install(source, dest, format, null);
	}
	
	/**
	 * Resolve the given {@link URL} in the available KNS.
	 * The first KNS to be checked is the default KNS, 
	 * thereafter the user's KNS.
	 * 
	 * @param knsServerList list of KNS servers
	 * @param resourceURL the {@link URL} to be resolved by the KNS.
	 * @param resolver the resolver to resolve the given resourceURL in the {@link DefaultKNSServerList}.
	 * @param install a customized {@link AppInstall} method for installation.
	 * 
	 * @return the resolved {@link  KN} or {@link null} if it is not resolved.
	 * 
	 * @throws ResourceNotResolvedException if the given KB can not be resolved.
	 * @throws Exception if any error occurs during the operation.
	 */
	public static void install(KNSServerList knsServerList, 
			URL resourceURL, 
			InstallFactory installFactory) throws ResourceNotResolvedException, Exception {
		KN resolvedKN = resolve(knsServerList, resourceURL, KBox.KIBE_FORMAT, 
				KBox.KIBE_VERSION);
		notNull(new ResourceNotResolvedException(resourceURL.toString()), resolvedKN);
		install(resolvedKN.getTargets().get(0).getURL(),
				resourceURL,
				KBox.KIBE_FORMAT, 
				KBox.KIBE_VERSION,
				new ZipAppInstall(),
				new ConsoleInstallInputStreamFactory());
	}
	
	/**
	 * Resolve the given {@link URL} in the available KNS.
	 * The first KNS to be checked is the default KNS, 
	 * thereafter the user's KNS.
	 * 
	 * @param knsServerList list of KNS servers
	 * @param resourceURL the {@link URL} to be resolved by the KNS.
	 * @param resolver the resolver to resolve the given resourceURL in the {@link DefaultKNSServerList}.
	 * @param installFactory a {@link InstallFactory} method for installation.
	 * @param isFactory a {@link InputStreamFactory} to be used to create a stream connection with the resolved resource's {@link URL}.
	 * 
	 * @return the resolved {@link KN} or {@link null} if it is not resolved.
	 * 
	 * @throws ResourceNotResolvedException if the given KB can not be resolved.
	 * @throws Exception if any error occurs during the operation.
	 */
	public static void install(KNSServerList knsServerList, URL resourceURL, 
			InstallFactory installFactory, InputStreamFactory isFactory) throws ResourceNotResolvedException, Exception {
		KN resolvedKN = resolve(knsServerList, resourceURL, KBox.KIBE_FORMAT, 
				KBox.KIBE_VERSION);
		notNull(new ResourceNotResolvedException(resourceURL.toString()), resolvedKN);
		install(resolvedKN.getTargets().get(0).getURL(),
				resourceURL,
				KBox.KIBE_FORMAT, 
				KBox.KIBE_VERSION,
				new ZipAppInstall(),
				new ConsoleInstallInputStreamFactory());
	}
	
	/**
	 * Creates a mirror for the given file in a given {@link URL}. This function allows
	 * KBox to serve files to applications, acting as proxy to the mirrored
	 * file. The file that is published in a give {@link URL} will be located when the
	 * client execute the function {@link KBox#getResource(URL)}.
	 * 
	 * @param resource the {@link URL} representing the resource that will be resolved.
	 * @param installFactory a customized method for installation.
	 * @param isFactory an InputStreamFactory.
	 * 
	 * @throws ResourceNotResolvedException if the given resource can not be resolved.
	 * @throws Exception if the resource does not exist or can not be copied or some
	 *             error occurs during the resource publication.
	 */
	public static void install(URL resource, InstallFactory installFactory, InputStreamFactory isFactory)
			throws ResourceNotResolvedException, Exception {
		DefaultKNSServerList kibeKNSServerList = new DefaultKNSServerList();
		install(kibeKNSServerList,
				resource,
				installFactory,
				isFactory);
	}
	
	/**
	 * Creates a mirror for the given file in a given {@link URL}. This function allows
	 * KBox to serve files to applications, acting as proxy to the mirrored
	 * file. The file that is published in a give {@link URL} will be located when the
	 * client execute the function {@link KBox#getResource(URL)}.
	 * 
	 * @param url the {@link URL} that will be resolved.
	 * @param install a customized method for installation.
	 * @param format the format.
	 * @param version the version.
	 * 
	 * @throws {@link KBNotResolvedException} if the given resource can not be resolved.
	 * @throws {@link Exception} if the resource does not exist or can not be copied or some
	 *             error occurs during the resource publication.
	 */
	public static void install(URL knsServer, 
			URL resource, 
			String format, 
			String version,
			InstallFactory installFactory)
			throws KBNotResolvedException, Exception {		
		KN resolvedKN = resolve(knsServer, resource, format, version);
		notNull(new KBNotResolvedException(resource.toString()), resolvedKN);
		install(resolvedKN,
				resource,
				installFactory,
				new ConsoleInstallInputStreamFactory());
	}
	
	/**
	 * Install a given KB resolved by a given {@link Resolver} using the {@link DefaultInstallFactory}.
	 * 
	 * @param knowledgebase the KB Name that will be resolved and installed.
	 * 
	 * @throws ResourceNotResolvedException if the given knowledge base can not be resolved.
	 * @throws Exception if any error occurs during the installation process.
	 */
	public static void install(URL knowledgebase) throws ResourceNotResolvedException, Exception {
		DefaultKNSServerList knsServerList = new DefaultKNSServerList();
		InstallFactory installFactory = new DefaultInstallFactory();
		install(knsServerList, knowledgebase, installFactory);
	}

	/**
	 * Install a given KB resolved by a given {@link Resolver}.
	 * This method uses the default <b>kibe</b> format implemented by <i>Kibe library</i> and its
	 * default version <b>0<b>.
	 * 
	 * @param knowledgebase the KB Name that will be resolved and installed.
	 * @param isFactory the {@link InputStreamFactory}.
	 * 
	 * @throws ResourceNotResolvedException if the given knowledge base can not be resolved.
	 * @throws Exception if any error occurs during the installation process.
	 */
	public static void install(URL knowledgebase,
			InputStreamFactory isFactory) throws ResourceNotResolvedException, Exception {
		KNSServerList knsServerList = new DefaultKNSServerList();
		InstallFactory installFactory = new DefaultInstallFactory();
		install(knsServerList, knowledgebase, installFactory, isFactory);
	}
	
	/**
	 * Install a given RDF files in the given knowledge base.
	 * 
	 * @param sources the {@link URL} to the RDF resources to be installed.
	 * @param target the {@link URL} whereas the KB will be installed.
	 * @param isFactory the {@link InputStreamFactory}.
	 * 
	 * @throws Exception if any error occurs during the knowledge base installation process.
	 */
	public static void install(URL[] sources, URL target, InputStreamFactory isFactory) throws Exception {
		install(sources, target, KBox.KIBE_FORMAT, KBox.KIBE_VERSION, new ZipAppInstall(), isFactory);
	}
	
	/**
	 * Install a given index file in the given knowledge base.
	 * 
	 * @param resource the {@link URL} representing the resource to be installed.
	 * @param format the resource format.
	 * @param isFactory a {@link InputStreamFactory} to be used to create a stream connection with the resolved resource's {@link URL}.
	 * 
	 * @throws ResourceNotResolvedException if the given knowledge base can not be resolved.
	 * @throws Exception if any error occurs during the installation process.
	 */
	public static void install(URL resource, 
			String format, 
			InputStreamFactory isFactory) throws ResourceNotResolvedException, Exception {
		InstallFactory installFactory = new DefaultInstallFactory();
		install(resource, installFactory, isFactory);
	}
	
	/**
	 * Install a given index file in the given knowledge base.
	 * 
	 * @param source the {@link URL} of the index file to be installed.
	 * @param target the {@link URL} of the knowledge base whereas the index will be installed.
	 * @param isFactory a {@link InputStreamFactory} to be used to create a stream connection with the resolved resource's {@link URL}.
	 * @param format the KB format.
	 * 
	 * @throws Exception if any error occurs during the installation process.
	 */
	public static void install(URL source, URL target, String format, InputStreamFactory isFactory) throws Exception {
		install(source, target, format, null, isFactory);
	}
	
	/**
	 * Install a given index file in the given knowledge base.
	 * This method uses the default install method {@link ResourceAppInstall}.
	 * 
	 * @param source the {@link URL} of the index file to be installed.
	 * @param target the {@link URL} of the knowledge base whereas the index will be installed.
	 * @param format the KB format.
	 * @param version the KB version.
	 * @param isFactory a {@link InputStreamFactory} to be used to create a stream connection with the resolved resource's {@link URL}.
	 * 
	 * @throws Exception if any error occurs during the installation process.
	 */
	public static void install(URL source, 
			URL target, 
			String format,
			String version, 
			InputStreamFactory isFactory) throws Exception {
		install(source, target, format, version, new ResourceAppInstall());
	}
	
	/**
	 * Install a given index file in the given knowledge base.
	 * This method uses the default install implemented by 
	 * {@link ResourceAppInstall} class, the default format <b>unknown</b> and 
	 * the default version <b>0</b>.
	 * 
	 * @param source the {@link URL} of the index file to be installed.
	 * @param target the {@link URL} of the knowledge base whereas the index will be installed.
	 * 
	 * @throws Exception if any error occurs during the indexing process.
	 */
	public static void installKB(URL source, URL target) throws Exception {
		install(source, target, KIBE_FORMAT, KIBE_VERSION, new ZipAppInstall());
	}
	
	/**
	 * Creates a mirror for the given file in a given {@link URL}. This function allows
	 * KBox to serve files to applications, acting as proxy to the mirrored
	 * file. The file that is published in a give {@link URL} will be located when the
	 * client execute the function {@link KBox#getResource(URL)}.
	 * 
	 * @param resource the {@link URL} of the resource to be installed.
	 * @param target the {@link URL} where the file is going to be published.
	 * @param install a customized {@link AppInstall} method for installation.
	 * 
	 * @throws Exception if the resource does not exist or can not be copied or some
	 *             error occurs during the resource publication.
	 */
	public static void install(URL resource, URL dest, AppInstall install)
			throws Exception {
		install(resource, dest, null, null, install);
	}

	/**
	 * Install a given resource in the target URL.
	 * This method uses the default install implemented by 
	 * {@link ResourceAppInstall} class, the default format <b>unknown</b> and 
	 * the default version <b>0</b>.
	 * 
	 * @param resource the {@link URL} of the resource to be installed.
	 * @param target the {@link URL} of the knowledge base whereas the index will be installed.
	 * 
	 * @throws Exception if any error occurs during the indexing process.
	 */
	public static void install(URL resource, URL target) throws Exception {
		install(resource, target, new ResourceAppInstall());
	}
	
	/**
	 * Install a given resource file in the given {@link URL} using default version <b>0<b>.
	 * This method uses the default version <b>0</b>.
	 * 
	 * @param source the {@link URL} of the resource to be installed.
	 * @param target the {@link URL} of whereas the resource will be installed.
	 * @param format the resource format.
	 * 
	 * @throws Exception if any error occurs during the installation process.
	 */
	public static void install(URL source, URL target, String format) throws Exception {
		install(source, target, format, new ResourceAppInstall());
	}
	
	/**
	 * Install a given index file in the given knowledge base.
	 * This method uses the default install method implemented by
	 * {@link ZipAppInstall} class.
	 * 
	 * @param source the {@link URL} of the index file to be installed.
	 * @param target the {@link URL} of the knowledge base whereas the index will be installed.
	 * @param format the KB format.
	 * @param version the KB version.
	 * 
	 * @throws Exception if any error occurs during the installation process.
	 */
	public static void install(URL source, URL target, String format, String version) throws Exception {
		install(source, target, format, version, new ResourceAppInstall());
	}
	
	/**
	 * Install a given knowledge base resolved by a given KNS service.
	 * 
	 * @param knsServer the {@link URL} of the KNS service that will be used to lookup.
	 * @param knowledgebase the {@link URL} of the knowledge base that is going to be installed.
	 * @param streamListener the listener to be invoked when the knowledge base get dereferenced.
	 * 
	 * @throws KBNotResolvedException if the given knowledge base can not be resolved.
	 * @throws Exception if any error occurs during the installation process.
	 */
	public static void installKBFromKNSServer(URL knsServer, URL knowledgebase) throws KBNotLocatedException, Exception {
		install(knsServer, knowledgebase, new DefaultInstallFactory(), new ConsoleInstallInputStreamFactory());
	}
	
	public static void install(URL kbNameURL, String format, String version,
			InputStreamFactory inputStreamFactory) throws Exception {
		DefaultKNSServerList knsServerList = new DefaultKNSServerList();
		install(knsServerList, kbNameURL, format, version, new DefaultInstallFactory(), inputStreamFactory);
	}
	
	/**
	 * Install a given resource resolved by a given KNS service.
	 * This method uses the default <b>unknown</b> format and its default version <b>0</b>. 
	 * 
	 * @param knsServer the {@link URL} of the KNS service that will be used to lookup.
	 * @param knowledgebase the {@link URL} of the knowledge base that is going to be installed.
	 * @param isFactory a {@link InputStreamFactory} to be used to create a stream connection with the resolved resource's URL.
	 * 
	 * @throws ResourceNotResolvedException if the given knowledge base can not be resolved.
	 * @throws Exception if any error occurs during the indexing process.
	 */
	public static void installFromRNSServer(URL knsServer, 
			URL resource, 
			InputStreamFactory isFactory) throws ResourceNotResolvedException, Exception {
		KN resolvedKN = resolve(knsServer, resource);
		notNull(new ResourceNotResolvedException(resource.toString()), resolvedKN);
		install(resolvedKN.getTargets().get(0).getURL(),
				resource,
				getValue(resolvedKN.getFormat(), null),
				getValue(resolvedKN.getVersion(), null),
				isFactory);
	}
	
	/**
	 * Install a given resource resolved by a given KNS service.
	 * This method uses the default format version <b>0<b>.
	 * 
	 * @param knsServer the {@link URL} of the KNS service that will be used to lookup.
	 * @param resource the {@link URL} of the resource is going to be installed.
	 * @param format the KB format.
	 * 
	 * @throws ResourceNotResolvedException if the given knowledge base can not be resolved.
	 * @throws Exception if any error occurs during the indexing process.
	 */
	public static void installFromKNSServer(URL knsServer, 
			URL resource, 
			String format) throws ResourceNotResolvedException, Exception {
		installFromKNSServer(knsServer, 
				resource,
				format,
				new ConsoleInstallInputStreamFactory());
	}
	
	/**
	 * Install a given resource resolved by a given KNS service.
	 * 
	 * @param knsServer the {@link URL} of the KNS service that will be used to lookup.
	 * @param resource the {@link URL} of the knowledge base that is going to be installed.
	 * @param format the KB format.
	 * @param version the KB version.
	 *
	 * 
	 * @throws ResourceNotResolvedException if the given knowledge base can not be resolved.
	 * @throws Exception if any error occurs during the indexing process.
	 */
	public static void installFromKNSServer(URL knsServer,
			URL resource,
			String format,
			String version) throws ResourceNotResolvedException, Exception {
		installFromKNSServer(knsServer,
				resource,
				format,
				version,
				new ConsoleInstallInputStreamFactory());
	}
	
	/**
	 * Install a given resource resolved by a given KNS service.
	 * 
	 * @param knsServer the {@link URL} of the KNS service that will be used to lookup.
	 * @param resource the {@link URL} of the resource that is going to be installed.
	 * @param format the KB format.
	 * @param isFactory stream factory to be used to open stream with the source knowledge base.
	 * 
	 * @throws ResourceNotResolvedException if the given knowledge base can not be resolved.
	 * @throws Exception if any error occurs during the indexing process.
	 */
	public static void installFromKNSServer(URL knsServer,
			URL resource,
			String format,
			InputStreamFactory isFactory) throws ResourceNotResolvedException, Exception {
		KN resolvedKN = resolve(knsServer, resource, format);
		notNull(new ResourceNotResolvedException(resource.toString()), resolvedKN);
		install(resolvedKN.getTargets().get(0).getURL(), 
				resource, getValue(format, null),
				getValue(resolvedKN.getVersion(), null), isFactory);
	}
	
	/**
	 * Install a given knowledge base resolved by a given KNS service.
	 * This method uses {@link ZipAppInstall} as install method.
	 * 
	 * @param knsServer the {@link URL} of the KNS service that will be used to lookup.
	 * @param resource the {@link URL} of the resource that is going to be installed.
	 * @param format the KB format.
	 * @param version the KB version.
	 * @param isFactory stream factory to be used to open stream with the source knowledge base.
	 * 
	 * @throws ResourceNotResolvedException if the given knowledge base can not be resolved.
	 * @throws Exception if any error occurs during the indexing process.
	 */
	public static void installFromKNSServer(URL knsServer, 
			URL resource, 
			String format, 
			String version, 
			InputStreamFactory isFactory) throws ResourceNotResolvedException, Exception {
		KN resolvedKN = resolve(knsServer, resource, format, version);
		notNull(new ResourceNotResolvedException(resource.toString()), resolvedKN);
		install(resolvedKN.getTargets().get(0).getURL(), resource, format, version, isFactory);
	}
	
	/**
	 * Create index on the given file with the given RDF files.
	 * 
	 * @param destFile destiny file to store the index.
	 * @param rdfDir the directory containing the RDF file(s) to be encoded in a KB.
	 * 
	 * @throws Exception if any error occurs during the encoding process.
	 * All the files in the provided directory should be in an RDF 
	 * compatible format (e.g. turtle, n3, etc).
	 */
	public static void dirToKB(File destFile, File rdfDir) throws Exception {
		RDFToKB(destFile, rdfDir.listFiles());
	}
	
	/**
	 * Encode the given RDF files in a KB file format.
	 * 
	 * @param destFile destiny KB file.
	 * @param rdfFiles the RDF file(s) to be encoded in a KB.
	 * 
	 * @throws Exception if any error occurs during the encoding process.
	 */
	public static void RDFToKB(File destFile, URL... rdfFiles) throws Exception {
		Path indexDirPath = Files.createTempDirectory("kb");
		File indexDirFile = indexDirPath.toFile();
		TDB.bulkload(indexDirFile.getPath(), rdfFiles);
		ZipUtil.pack(indexDirFile, destFile, false);
	}
	
	/**
	 * Encode the given RDF files in a KB file format.
	 * 
	 * @param destFile destiny KB file.
	 * @param rdfFiles the RDF file(s) to be encoded in a KB.
	 * 
	 * @throws Exception if any error occurs during the encoding process.
	 */
	public static void RDFToKB(File destFile, File... rdfFiles) throws Exception {
		RDFToKB(destFile, URLUtils.fileToURL(rdfFiles));
	}

	/**
	 * Query the given knowledge bases.
	 * 
	 * @param sparql the SPARQL query.
	 * @param isFactory a {@link InputStreamFactory} to be invoked in case a knowledge base is dereferenced.
	 * @param install when true the knowledge base is auto-dereferenced when not found or not otherwise.
	 * @param knowledgeNames the Knowledge base Names to be queried.
	 * 
	 * @return a result set with the given query solution.
	 * 
	 * @throws Exception if any of the given knowledge bases can not be found.
	 */
	public static ResultSet query(String sparql, 
			InputStreamFactory isFactory, 
			boolean install, 
			URL... knowledgeNames) throws Exception {
		String[] knowledgeBasesPaths = get(isFactory, install, knowledgeNames);
		return TDB.query(sparql, knowledgeBasesPaths);
	}
	
	/**
	 * 
	 * Query the given knowledge bases.
	 * 
	 * @param isFactory a {@link InputStreamFactory} to stream the given KB in case they are not locally available.
	 * @param install when true the knowledge base is auto-dereferenced when not found or not otherwise.
	 * @param resourceNames the knowledge base Names to be queried.
	 * 
	 * @return a model with the given RDF KB names.
	 * 
	 * @throws Exception if any of the given knowledge bases can not be found.
	 */
	public static Model createModel(InputStreamFactory isFactory,
			boolean install,
			URL... resourceNames) throws Exception {
		String[] knowledgeBasesPaths = get(isFactory, install, resourceNames);
		return TDB.createModel(knowledgeBasesPaths);
	}
	
	/**
	 * 
	 * @param isFactory a {@link InputStreamFactory} to stream the given KB in case they are not locally available.
	 * @param install flag indicating to install the KB in case it cannot be located.
	 * @param urls the KB(s) {@link URL}(s).
	 * @return An array containing the relative path to the KB(s).
	 * 
	 * @throws Exception if an error occurs during resolving or dereferencing any of the KBs.
	 */
	public static String[] get(InputStreamFactory isFactory, 
			boolean install, 
			URL... urls) throws Exception {
		String[] knowledgeBasesPaths = new String[urls.length];
		int i = 0;
		Locate kbLocate = new AppLocate();
		DefaultKNSServerList knsServerList = new DefaultKNSServerList();
		AppInstall installFactory = new ZipAppInstall();
		for(URL knowledgeBase : urls) {
			try {
				File kbDir = getResource(knsServerList,
						knowledgeBase,
						kbLocate,
						KIBE_FORMAT,
						KIBE_VERSION,
						installFactory,
						isFactory,
						install);
				knowledgeBasesPaths[i] = kbDir.getAbsolutePath();
				i++;
			} catch (ResourceNotResolvedException e) {
				throw new KBNotResolvedException("The Knowledge base " + knowledgeBase.toString() + " is not installed."
						+ " You can install it setting the flag install.");
			} catch (ResourceDereferencingException e) {
				throw new KBDereferencingException(knowledgeBase.toString(), e);
			}
			
		}
		return knowledgeBasesPaths;
	}
	
	
	
	/**
	 * Create a {@link Model} with a given RDF knowledge base Names.
	 * Warning: This method automatically dereference the RDF KBs.
	 * 
	 * @param knowledgeNames the RDF KB Names to be queried.        
	 * 
	 * @return a {@link Model} containing the RDF KBs.
	 * 
	 * @throws Exception if some error occurs during the knowledge base 
	 *         dereference or model instantiation. 
	 */
	public static Model createModel(URL... knowledgeNames) throws Exception {
		return createModel(new ConsoleInstallInputStreamFactory(), true, knowledgeNames);
	}
	
	/**
	 * Create a Model with a given Knowledge base Names.
	 * 
	 * 
	 * @param install install a given knowledge base in case it does not exist.
	 * @param knowledgeNames the RDF KB Names to be queried.
	 * 
	 * @return a {@link Model} containing the RDF KBs.
	 * 
	 * @throws Exception if some error occurs during the KB 
	 *         dereference or model instantiation. 
	 */
	public static Model createModel(boolean install, URL... knowledgeNames) throws Exception {
		return createModel(new ConsoleInstallInputStreamFactory(), install, knowledgeNames);
	}
	
	/**
	 * Query a given model.
	 * 
	 * @param sparql the SPARQL query that will be used to query.
	 * @param model the {@link  Model} that will be queried.
	 * 
	 * @return a {@link ResultSet} containing the result to a given query.	 
	 *  
	 * @throws Exception if some error occurs during the query execution.
	 */
	public static ResultSet query(String sparql, Model model) throws Exception {
		return TDB.query(sparql, model);
	}
	
	/**
	 * Executes a given query in a KBox service endpoints address.
	 * 
	 * @param sparql the SPARQL query that will be used to query.
	 * @param url the {@link URL} of the service.
	 * 
	 * @return a {@link ResultSet} containing the result to a given query.
	 * 
	 * @throws Exception if some error occurs during the query execution.
	 */
	public static ResultSet query(String sparql, ServerAddress service) throws Exception {
		return TDB.queryService(sparql, service);
	}

	/**
	 * Query the given knowledge bases.
	 * 
	 * @param sparql the SPARQL query.
	 * @param knowledgeNames the knowledge base names to be queried.
	 * 
	 * @return a result set with the given query solution.
	 * 
	 * @throws Exception if any of the given knowledge bases can not be found.
	 */
	public static ResultSet query(String sparql, URL... knowledgeNames) throws Exception {
		Model model = createModel(new ConsoleInstallInputStreamFactory(), true, knowledgeNames);
		return query(sparql, model);
	}
	
	/**
	 * Query the given knowledge bases.
	 * 
	 * @param sparql the SPARQL query.
	 * @param install specify if the knowledge base should be installed (true) or not (false).
	 * @param knowledgeNames the RDF KB names to be queried.
	 * 
	 * @return a result set with the given query solution.
	 * 
	 * @throws Exception if any of the given knowledge bases can not be found.
	 */
	public static ResultSet query(String sparql, boolean install, URL... knowledgeNames) throws Exception {
		Model model = createModel(new ConsoleInstallInputStreamFactory(), install, knowledgeNames);
		return query(sparql, model);
	}

	/**
	 * Returns the local knowledge base directory.
	 * 
	 * @param url that will be used to locate the Knowledge base
	 * 
	 * @return the local mirror directory of the knowledge base.
	 *  
	 * @throws Exception if any error occurs while locating the Knowledge Base.
	 */
	public static File locate(URL url) throws Exception {
		Locate kbLocate = new AppLocate();
		return locate(url, null, null, kbLocate);
	}
	
	/**
	 * Returns the local resource directory.
	 * 
	 * @param url that will be used to locate the resource.
	 * @param format the resource format.
	 * 
	 * @return the local mirror directory of the resource.
	 *  
	 * @throws Exception if any error occurs while locating the Knowledge Base.
	 */
	public static File locate(URL url, String format) throws Exception {		
		return locate(url, format, null);
	}
	
	/**
	 * Returns the local resource directory.
	 * 
	 * @param url that will be used to locate the resource.
	 * @param format the resource format.
	 * @param version the resource version.
	 * 
	 * @return the local mirror directory of the knowledge base.
	 *  
	 * @throws Exception if any error occurs while locating the knowledge base. 
	 */
	public static File locate(URL url, String format, String version) throws Exception {
		Locate kbLocate = new AppLocate();
		return locate(url, format, version, kbLocate);
	}
	
	/**
	 * Resolve the given {@link URL} in the available KNS.
	 * The first KNS to be checked is the default KNS, 
	 * thereafter the user's KNS.
	 * 
	 * @param resourceURL the {@link URL} to be resolved by the KNS.
	 * 
	 * @return a resolved {@link KN}  or {@link null} if the given resource's {@link URL} can not be resolved.
	 * 
	 * @throws Exception if any error occurs during the operation.
	 */
	public static KN resolve(URL resourceURL) throws Exception {
		DefaultKNSServerList kibeKNSServerList = new DefaultKNSServerList();
		return resolve(kibeKNSServerList, resourceURL);
	}
	
	/**
	 * Get the resource from the given KNS Server {@link URL} with the given format using default version <b>0<b>.
	 * 
	 * @param knsServerList the {@link KNSServerList} to lookup for the give KB {@link URL}.
	 * @param resourceName the {@link URL} of the resource. 
	 * @param format the resource format.
	 * @param version the resource version.
	 * @param isFactory the {@link InputStreamFactory} to be used to open the KB file stream.
	 * 
	 * ps: This method automatically dereference the KB from the KNS Server in case it 
	 * can not be found locally.
	 * 
	 * @return the resource {@link File} or {@link null} if the resource does not exist or could not be located. 
	 * 
	 * @throws Exception if some error occurs during while getting the KB. 
	 */
	public static File getResource(KNSServerList knsServerList,
			URL resourceName,
			String format,
			String version,
			InputStreamFactory isFactory,
			boolean install
			) throws Exception {
		InstallFactory installFactory = new DefaultInstallFactory();
		return getResource(knsServerList, resourceName, format, version, isFactory, installFactory, install);		
	}
	
	/**
	 * Iterate over all available KNS services with a given visitor.
	 * 
	 * @param visitor an implementation of {@link KNSServerListVisitor}.
	 * 
	 * @throws Exception if any error occurs during the operation.
	 */
	public static void visit(KNSServerListVisitor visitor) throws Exception {
		DefaultKNSServerList kibeKNSServerList = new DefaultKNSServerList();
		kibeKNSServerList.visit(visitor);
	}

	public static File getResource(URL resourceName, String format, String version, boolean install) throws Exception {
		DefaultKNSServerList kibeKNSServerList = new DefaultKNSServerList();
		InputStreamFactory inputStreamFactory = new ConsoleInstallInputStreamFactory();
		return getResource(kibeKNSServerList, resourceName, format, version, inputStreamFactory, install);		
	}
}
