package org.aksw.kbox.kibe;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import kbox.apple.AppInstall;
import kbox.apple.AppLocate;
import kbox.apple.AppZipInstall;

import org.aksw.kbox.InputStreamFactory;
import org.aksw.kbox.kibe.exception.KBDereferencingException;
import org.aksw.kbox.kibe.exception.KBNotResolvedException;
import org.aksw.kbox.kibe.stream.DefaultInputStreamFactory;
import org.aksw.kbox.kibe.tdb.TDB;
import org.aksw.kbox.kibe.utils.ZIPUtil;
import org.aksw.kbox.kns.KBResolver;
import org.aksw.kbox.kns.KNSServerListVisitor;
import org.aksw.kbox.kns.Resolver;
import org.aksw.kbox.kns.ServerAddress;
import org.askw.kbox.kns.exception.ResourceNotResolvedException;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

/**
 * 
 * @author {@linkplain http://emarx.org}
 *
 */
public class KBox extends org.aksw.kbox.kns.KBox {
	
	private final static String DEFAULT_VERSION = "0";
	
	/**
	 * Install the given knowledge base.
	 * The installation try to resolve the knowledge base by the available KNS.
	 * 
	 * @param knowledgeBase the URL of the knowledge base.
	 * 
	 * @throws Exception if any error occurs during the operation. 
	 * @throws ResourceNotResolvedException if the given knowledge base can not be resolved.
	 */
	public static void installKB(URL knowledgeBase) throws Exception {
		installKB(knowledgeBase, new DefaultInputStreamFactory());
	}
	
	/**
	 * Install the given knowledge base.
	 * The installation try to resolve the knowledge base by the available KNS.
	 * 
	 * @param knowledgeBase the URL of the knowledge base.
	 * @param isFactory the InputStreamFactory that will be used to open stream with the target knowledge base.
	 * 
	 * @throws Exception if any error occurs during the operation. 
	 * @throws KBNotResolvedException if the given knowledge base can not be resolved.
	 */
	public static void installKB(URL knowledgeBase, InputStreamFactory isFactory) throws Exception {
		KBResolver resolver = new KBResolver();
		installKB(knowledgeBase, resolver, isFactory);
	}
	
	/**
	 * Install a given KB resolved by a given Resolver.
	 * 
	 * @param knowledgebase the KB Name that will be resolved and installed.
	 * @param resolver the Resolver that will resolve the KB Name.
	 * 
	 * @throws Exception if any error occurs during the indexing process.	 * 
	 * @throws KBNotResolvedException if the given knowledge base can not be resolved.
	 */
	public static void installKB(URL knowledgebase, Resolver resolver) throws KBNotResolvedException, Exception {
		KBKNSServerList knsServerList = new KBKNSServerList();
		URL resolvedURL = resolve(knsServerList, knowledgebase, resolver);
		assertNotNull(resolvedURL, new KBNotResolvedException(knowledgebase.toString()));
		install(resolvedURL, knowledgebase);
	}
	
	/**
	 * Install a given KB resolved by a given Resolver.
	 * 
	 * @param url the KB Name that will be resolved and installed.
	 * @param resolver the Resolver that will resolve the KB Name.
	 * @param isFactory the InputStreamFactory.
	 * 
	 * @throws Exception if any error occurs during the indexing process. 
	 * @throws KBNotResolvedException if the given knowledge base can not be resolved.
	 */
	public static void installKB(URL knowledgebase, Resolver resolver, InputStreamFactory isFactory) throws KBNotResolvedException, Exception {
		KBKNSServerList knsServerList = new KBKNSServerList();
		URL resolvedURL = resolve(knsServerList, knowledgebase, resolver);
		assertNotNull(resolvedURL, new KBNotResolvedException(knowledgebase.toString()));
		installKB(resolvedURL, knowledgebase, isFactory);
	}
	
	/**
	 * Install a given index file in the given knowledge base.
	 * 
	 * @param target the name of the target knowledge base whereas the index will be installed.
	 * @param source the InputStream index file to be installed.
	 * 
	 * @throws Exception if any error occurs during the indexing process.
	 */
	public static void installKB(InputStream source, URL target) throws Exception {
		install(source, target, new KBInstall());
	}

	/**
	 * Install a given index file in the given knowledge base.
	 * 
	 * @param target the name of the target knowledge base whereas the index will be installed.
	 * @param source the InputStream index file to be installed.
	 * @param format the KB format.
	 * @param version the KB version.
	 * 
	 * @throws Exception if any error occurs during the indexing process.
	 */
	public static void installKB(InputStream source, URL target, String format, String version) throws Exception {
		install(source, target, new AppInstall(format, version));
	}
	
	/**
	 * Install a given index file in the given knowledge base.
	 * 
	 * @param source the URL of the index file to be installed.
	 * @param target the URL of the knowledge base whereas the index will be installed.
	 * @param isFactory the InputStreamFactory.
	 * 
	 * @throws Exception if any error occurs during the indexing process.
	 */
	public static void installKB(URL source, URL target, InputStreamFactory isFactory) throws Exception {
		install(source, target, new KBInstall(isFactory));
	}
	
	/**
	 * Install a given index file in the given knowledge base.
	 * 
	 * @param knowledgebase the URL of the index file to be installed.
	 * @param format the KB format.
	 * @param isFactory the InputStreamFactory.
	 * 
	 * @throws Exception if any error occurs during the indexing process.
	 * @throws KBNotResolvedException if the given knowledge base can not be resolved.
	 */
	public static void installKB(URL knowledgebase, String format, InputStreamFactory isFactory) throws KBNotResolvedException, Exception {
		KBResolver resolver = new KBResolver();
		URL resolvedURL = resolve(knowledgebase, format, null, resolver);
		assertNotNull(resolvedURL, new KBNotResolvedException(knowledgebase.toString()));
		install(resolvedURL, knowledgebase, new AppZipInstall(format, DEFAULT_VERSION, isFactory));
	}
	
	/**
	 * Install a given index file in the given knowledge base.
	 * 
	 * @param knowledgebase the URL of the index file to be installed.
	 * @param format the KB format.
	 * @param version the KB version.
	 * @param isFactory the InputStreamFactory.
	 * 
	 * @throws Exception if any error occurs during the indexing process.
	 * @throws KBNotResolvedException if the given knowledge base can not be resolved.
	 */
	public static void installKB(URL knowledgebase, String format, String version, InputStreamFactory isFactory) throws KBNotResolvedException, Exception {
		KBResolver resolver = new KBResolver();
		URL resolvedURL = resolve(knowledgebase, format, version, resolver);
		assertNotNull(resolvedURL, new KBNotResolvedException(knowledgebase.toString()));
		install(resolvedURL, knowledgebase, new AppZipInstall(format, version, isFactory));
	}
	
	
	/**
	 * Install a given index file in the given knowledge base.
	 * 
	 * @param source the URL of the index file to be installed.
	 * @param target the URL of the knowledge base whereas the index will be installed.
	 * @param isFactory the InputStreamFactory.
	 * @param format the KB format.
	 * @throws Exception if any error occurs during the indexing process.
	 */
	public static void installKB(URL source, URL target, String format, InputStreamFactory isFactory) throws Exception {
		installKB(source, target, format, DEFAULT_VERSION, isFactory);
	}
	
	/**
	 * Install a given index file in the given knowledge base.
	 * 
	 * @param source the URL of the index file to be installed.
	 * @param target the URL of the knowledge base whereas the index will be installed.
	 * @param isFactory the InputStreamFactory.
	 * @param format the KB format.
	 * @param version the KB version.
	 * 
	 * @throws Exception if any error occurs during the indexing process.
	 */
	public static void installKB(URL source, URL target, String format, String version, InputStreamFactory isFactory) throws Exception {
		install(source, target, new AppZipInstall(format, version, isFactory));
	}

	/**
	 * Install a given index file in the given knowledge base.
	 * 
	 * @param source the URL of the index file to be installed.
	 * @param target the URL of the knowledge base whereas the index will be installed.
	 * 
	 * @throws Exception if any error occurs during the indexing process.
	 */
	public static void installKB(URL source, URL target) throws Exception {
		install(source, target, new KBInstall());
	}
	
	/**
	 * Install a given index file in the given knowledge base.
	 * 
	 * @param source the URL of the index file to be installed.
	 * @param target the URL of the knowledge base whereas the index will be installed.
	 * @param format the KB format.
	 * 
	 * @throws Exception if any error occurs during the indexing process.
	 */
	public static void installKB(URL source, URL target, String format) throws Exception {
		installKB(source, target, format, DEFAULT_VERSION);
	}
	
	/**
	 * Install a given index file in the given knowledge base.
	 * 
	 * @param source the URL of the index file to be installed.
	 * @param target the URL of the knowledge base whereas the index will be installed.
	 * @param format the KB format.
	 * @param version the KB version.
	 * 
	 * @throws Exception if any error occurs during the indexing process.
	 */
	public static void installKB(URL source, URL target, String format, String version) throws Exception {
		install(source, target, new AppZipInstall(format, version));
	}
	
	/**
	 * Install a given knowledge base resolved by a given KNS service.
	 * 
	 * @param knsServer the URL of the KNS service that will be used to lookup.
	 * @param knowledgebase the URL of the knowledge base that is going to be installed.
	 * @param streamListener the listener to be invoked when the knowledge base get dereferenced.
	 * 
	 * @throws Exception if any error occurs during the indexing process.
	 * @throws KBNotResolvedException if the given knowledge base can not be resolved.
	 */
	public static void installKBFromKNSServer(URL knsServer, URL knowledgebase) throws KBNotResolvedException, Exception {
		installKB(knsServer, knowledgebase, new DefaultInputStreamFactory());
	}
	
	/**
	 * Install a given knowledge base resolved by a given KNS service.
	 * 
	 * @param knsServer the URL of the KNS service that will be used to lookup.
	 * @param knowledgebase the URL of the knowledge base that is going to be installed.
	 * @param streamListener the listener to be invoked when the knowledge base get dereferenced.
	 * @param isFactory stream factory to be used to open stream with the source knowledge base.
	 * 
	 * @throws Exception if any error occurs during the indexing process.
	 * @throws KBNotResolvedException if the given knowledge base can not be resolved.
	 */
	public static void installKBFromKNSServer(URL knsServer, URL knowledgebase, InputStreamFactory isFactory) throws KBNotResolvedException, Exception {
		URL resolvedURL = resolve(knsServer, knowledgebase);
		assertNotNull(resolvedURL, new KBNotResolvedException(knowledgebase.toString()));
		installKB(knowledgebase, resolvedURL, isFactory);
	}
	
	/**
	 * Install a given knowledge base resolved by a given KNS service.
	 * 
	 * @param knsServer the URL of the KNS service that will be used to lookup.
	 * @param knowledgebase the URL of the knowledge base that is going to be installed. 
	 * @param streamListener the listener to be invoked when the knowledge base get dereferenced.
	 * @param format the KB format.
	 * 
	 * @throws Exception if any error occurs during the indexing process.
	 * @throws KBNotResolvedException if the given knowledge base can not be resolved.
	 */
	public static void installKBFromKNSServer(URL knsServer, URL knowledgebase, String format) throws KBNotResolvedException, Exception {
		installKBFromKNSServer(knsServer, knowledgebase, format, new DefaultInputStreamFactory());
	}
	
	/**
	 * Install a given knowledge base resolved by a given KNS service.
	 * 
	 * @param knsServer the URL of the KNS service that will be used to lookup.
	 * @param knowledgebase the URL of the knowledge base that is going to be installed. 
	 * @param streamListener the listener to be invoked when the knowledge base get dereferenced.
	 * @param format the KB format.
	 * @param version the KB version.
	 * 
	 * @throws Exception if any error occurs during the indexing process.
	 * @throws KBNotResolvedException if the given knowledge base can not be resolved.
	 */
	public static void installKBFromKNSServer(URL knsServer, URL knowledgebase, String format, String version) throws KBNotResolvedException, Exception {
		installKBFromKNSServer(knsServer, knowledgebase, format, version, new DefaultInputStreamFactory());
	}
	
	/**
	 * Install a given knowledge base resolved by a given KNS service.
	 * 
	 * @param knowledge base the URL of the knowledge base that is going to be installed.
	 * @param knsServer the URL of the KNS service that will be used to lookup.
	 * @param streamListener the listener to be invoked when the knowledge base get dereferenced.
	 * @param format the KB format.
	 * @param isFactory stream factory to be used to open stream with the source knowledge base.
	 * 
	 * @throws KBNotResolvedException if the given knowledge base can not be resolved.
	 * @throws Exception if any error occurs during the indexing process.
	 */
	public static void installKBFromKNSServer(URL knsServer, URL knowledgebase, String format, InputStreamFactory isFactory) throws KBNotResolvedException, Exception {
		URL resolvedURL = resolve(knsServer, knowledgebase, format);
		assertNotNull(resolvedURL, new KBNotResolvedException(knowledgebase.toString()));
		installKB(knowledgebase, resolvedURL, format, isFactory);
	}
	
	/**
	 * Install a given knowledge base resolved by a given KNS service.
	 * 
	 * @param knowledge base the URL of the knowledge base that is going to be installed.
	 * @param knsServer the URL of the KNS service that will be used to lookup.
	 * @param streamListener the listener to be invoked when the knowledge base get dereferenced.
	 * @param format the KB format.
	 * @param version the KB version.
	 * @param isFactory stream factory to be used to open stream with the source knowledge base.
	 * 
	 * @throws KBNotResolvedException if the given knowledge base can not be resolved.
	 * @throws Exception if any error occurs during the indexing process.
	 */
	public static void installKBFromKNSServer(URL knsServer, URL knowledgebase, String format, String version, InputStreamFactory isFactory) throws KBNotResolvedException, Exception {
		URL resolvedURL = resolve(knsServer, knowledgebase, format, version);
		assertNotNull(resolvedURL, new KBNotResolvedException(knowledgebase.toString()));
		installKB(knowledgebase, resolvedURL, format, version, isFactory);
	}
	
	/**
	 * Create index on the given file with the given RDF files.
	 * 
	 * @param indexFile destiny file to store the index.
	 * @param filesToIndex the files containing the knowledge base to be indexed.
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
	 * Query the given knowledge bases.
	 * 
	 * @param sparql the SPARQL query.
	 * @param install when true the knowledge base is auto-dereferenced when not found or not otherwise.
	 * @param knowledgeNames the Knowledge base Names to be queried.
	 * @param streamListener the StreamListener to be invoked in case a Knowledge base is dereferenced.
	 * 
	 * @return a result set with the given query solution.
	 * @throws Exception if any of the given knowledge bases can not be found.
	 */
	public static ResultSet query(String sparql, InputStreamFactory factory, boolean install, URL... knowledgeNames) throws Exception {
		String[] knowledgeBasesPaths = getKowledgebases(factory, install, knowledgeNames);
		return TDB.query(sparql, knowledgeBasesPaths);
	}
	
	/**
	 * 
	 * Query the given knowledge bases.
	 * 
	 * @param isFactory a InputStreamFactory to stream the given KB in case they are not locally available.
	 * @param install when true the knowledge base is auto-dereferenced when not found or not otherwise.
	 * @param knowledgeNames the Knowledge base Names to be queried.
	 * 
	 * @return a model with the given RDF KB names.
	 * @throws Exception if any of the given knowledge bases can not be found.
	 */
	public static Model createModel(InputStreamFactory isFactory, boolean install, URL... knowledgeNames) throws Exception {
		String[] knowledgeBasesPaths = getKowledgebases(isFactory, install, knowledgeNames);
		return TDB.createModel(knowledgeBasesPaths);
	}
	
	private static String[] getKowledgebases(InputStreamFactory factory, boolean install, URL... urls) throws Exception {
		String[] knowledgeBasesPaths = new String[urls.length];
		int i = 0;
		for(URL knowledgeBase : urls) {
			File localDataset = locateKB(knowledgeBase);
			if(localDataset == null && install) {
				URL resolvedURL = resolve(knowledgeBase);
				assertNotNull(resolvedURL, new KBNotResolvedException(knowledgeBase.toString()));
				try(InputStream is = factory.get(resolvedURL)) {
					installKB(is, knowledgeBase);
				} catch (Exception e) {
					throw new KBDereferencingException(knowledgeBase.toString(), e);
				}
				localDataset = locateKB(knowledgeBase);
			} else {
				assertNotNull(localDataset, new KBNotResolvedException("Knowledge base " + knowledgeBase.toString() + " is not installed."
					+ " You can install it using the command install."));
			}
			knowledgeBasesPaths[i] = localDataset.getAbsolutePath();
			i++;
		}
		return knowledgeBasesPaths;
	}
	
	/**
	 * Create a Model with a given Knowledge base Names.
	 * Warning: This method automatically dereference the RDF KBs.
	 * 
	 * @param knowledgeNames the RDF KB Names to be queried.        
	 * 
	 * @return a Model containing the RDF KBs.
	 * 
	 * @throws Exception if some error occurs during the knowledge base 
	 *         dereference or model instantiation. 
	 */
	public static Model createModel(URL... knowledgeNames) throws Exception {
		return createModel(new DefaultInputStreamFactory(), true, knowledgeNames);
	}
	
	/**
	 * Create a Model with a given Knowledge base Names.
	 * 
	 * 
	 * @param install install a given knowledge base in case it does not exist.
	 * @param knowledgeNames the RDF KB Names to be queried.
	 * 
	 * @return a Model containing the RDF KBs.
	 * 
	 * @throws Exception if some error occurs during the KB 
	 *         dereference or model instantiation. 
	 */
	public static Model createModel(boolean install, URL... knowledgeNames) throws Exception {
		return createModel(new DefaultInputStreamFactory(), install, knowledgeNames);
	}
	
	/**
	 * Query a given model.
	 * 
	 * @param sparql the SPARQL query that will be used to query.
	 * @param model the Model that will be queried.
	 * 
	 * @return a ResultSet containing the result to a given query.	 
	 *  
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
	 * @return a ResultSet containing the result to a given query.
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
		Model model = createModel(new DefaultInputStreamFactory(), true, knowledgeNames);
		return query(sparql, model);
	}
	
	/**
	 * Query the given knowledge bases.
	 * 
	 * @param sparql the SPARQL query.
	 * @param install specify if the knowledge base should be installed (true) or not (false).
	 * @param knowledgeNames the KB names to be queried.
	 * 
	 * @return a result set with the given query solution.
	 * 
	 * @throws Exception if any of the given knowledge bases can not be found.
	 */
	public static ResultSet query(String sparql, boolean install, URL... knowledgeNames) throws Exception {
		Model model = createModel(new DefaultInputStreamFactory(), install, knowledgeNames);
		return query(sparql, model);
	}
	
	/**
	 * Returns a slice of the given knowledge base representing by the Construct or Describe query.
	 * 
	 * @param sparql a Construct or Describe query.
	 * @param knowledgeNames the KB Names that are going to be sliced.
	 * 
	 * @return a result set with the given query solution.
	 * 
	 * @throws Exception if any of the given knowledge bases can not be found.
	 */
	public static ResultSet slice(String sparql, URL... knowledgeNames) throws Exception {
		Model model = createModel(new DefaultInputStreamFactory(), true, knowledgeNames);
		return query(sparql, model);
	}

	/**
	 * Returns the local Knowledge base directory.
	 * 
	 * @param url that will be used to locate the Knowledge base
	 * 
	 * @return the local mirror directory of the knowledge base.
	 *  
	 * @throws Exception if any error occurs while locating the Knowledge base. 
	 */
	public static File locateKB(URL url) throws Exception {
		KBLocate kbLocate = new KBLocate();
		return locate(url, kbLocate);
	}
	
	/**
	 * Returns the local Knowledge base directory.
	 * 
	 * @param url that will be used to locate the Knowledge base.
	 * @param format the KB format.
	 * 
	 * @return the local mirror directory of the knowledge base.
	 *  
	 * @throws Exception if any error occurs while locating the Knowledge base. 
	 */
	public static File locateKB(URL url, String format) throws Exception {		
		return locateKB(url, format, DEFAULT_VERSION);
	}
	
	/**
	 * Returns the local Knowledge base directory.
	 * 
	 * @param url that will be used to locate the Knowledge base.
	 * @param format the KB format.
	 * @param version the KB version.
	 * 
	 * @return the local mirror directory of the knowledge base.
	 *  
	 * @throws Exception if any error occurs while locating the Knowledge base. 
	 */
	public static File locateKB(URL url, String format, String version) throws Exception {
		AppLocate kbLocate = new AppLocate(format, version);
		return locate(url, kbLocate);
	}
	
	/**
	 * Resolve the given URL in the available KNS.
	 * The first KNS to be checked is the default KNS, 
	 * thereafter the user's KNS.
	 * 
	 * @param resourceURL the URL to be resolved by the KNS.
	 * 
	 * @return the resolved URL or NULL if it is not resolved.
	 * 
	 * @throws Exception if any error occurs during the operation.
	 */
	public static URL resolve(URL resourceURL) throws Exception {
		KBKNSServerList kibeKNSServerList = new KBKNSServerList();
		return resolve(kibeKNSServerList, resourceURL);
	}
	
	/**
	 * Resolve the given URL in the available KNS.
	 * The first KNS to be checked is the default KNS, 
	 * thereafter the user's KNS.
	 * 
	 * @param knsServerURL the URL of the KNS Server.
	 * @param resourceURL the URL to be resolved by the KNS.
	 * 
	 * @return the resolved URL or NULL if it is not resolved.
	 * 
	 * @throws IOException if any error occurs during the operation.
	 */
	public static URL resolve(URL knsServerURL, URL resourceURL) throws Exception {
		KBResolver resolver = new KBResolver();
		return resolve(knsServerURL, resourceURL, resolver);
	}
	
	/**
	 * Resolve the given URL in the available KNS.
	 * The first KNS to be checked is the default KNS, 
	 * thereafter the user's KNS.
	 * 
	 * @param knsServerURL the URL of the KNS Server.
	 * @param resourceURL the URL to be resolved by the KNS.	 
	 * @param format the KB format.
	 * 
	 * @return the resolved URL or NULL if it is not resolved.
	 * 
	 * @throws Exception if any error occurs during the operation.
	 */
	public static URL resolve(URL knsServerURL, URL resourceURL, String format) throws Exception {
		KBResolver resolver = new KBResolver();
		return resolve(knsServerURL, resourceURL, format, resolver);
	}
	
	/**
	 * Resolve the given URL in the available KNS.
	 * The first KNS to be checked is the default KNS, 
	 * thereafter the user's KNS.
	 * 
	 * @param knsServerURL the URL of the KNS Server.
	 * @param resourceURL the URL to be resolved by the KNS.	 
	 * @param format the KB format. 
	 * @param version the KB version.
	 * 
	 * @return the resolved URL or NULL if it is not resolved.
	 * 
	 * @throws Exception if any error occurs during the operation.
	 */
	public static URL resolve(URL knsServerURL, URL resourceURL, String format, String version) throws Exception {
		KBResolver resolver = new KBResolver();
		return resolve(knsServerURL, resourceURL, format, version, resolver);
	}
		
	/**
	 * Iterate over all available KNS services with a given visitor.
	 * 
	 * @param KNSVisitor an implementation of KNSVisitor.
	 * 
	 * @throws Exception if any error occurs during the operation.
	 */
	public static void visit(KNSServerListVisitor visitor) throws Exception {
		KBKNSServerList kibeKNSServerList = new KBKNSServerList();
		kibeKNSServerList.visit(visitor);
	}
	
}
