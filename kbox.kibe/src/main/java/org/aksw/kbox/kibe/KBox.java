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
import org.aksw.kbox.kibe.exception.KBNotFoundException;
import org.aksw.kbox.kibe.exception.KBNotResolvedException;
import org.aksw.kbox.kibe.stream.DefaultInputStreamFactory;
import org.aksw.kbox.kibe.tdb.TDB;
import org.aksw.kbox.kibe.utils.ZIPUtil;
import org.aksw.kbox.kns.KNSServerListVisitor;
import org.aksw.kbox.kns.KNSTable;
import org.aksw.kbox.kns.ServerAddress;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

/**
 * 
 * @author {@linkplain http://emarx.org}
 *
 */
public class KBox extends org.aksw.kbox.kns.KBox {
	
	/**
	 * Install the given knowledge base.
	 * The installation try to resolve the knowledge base by the available KNS.
	 * 
	 * @param knowledgeBase the URL of the knowledge base.
	 * 
	 * @throws Exception if any error occurs during the operation.
	 */
	public static void installKB(URL knowledgeBase) throws Exception {
		URL sourceURL = resolve(knowledgeBase);
		if(sourceURL == null) {
			throw new KBNotFoundException("Knowledge base " + knowledgeBase.toString() + " can not be resolved.");
		}
		installKB(sourceURL, knowledgeBase);
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
	 * @param knowledge base the URL of the knowledge base that is going to be installed.
	 * @param knsServer the URL of the KNS service that will be used to lookup.
	 * @param streamListener the listener to be invoked when the knowledge base get dereferenced.
	 * 
	 * @throws Exception if any error occurs during the indexing process.
	 * @throws KBNotResolvedException if the given knowledge base can not be resolved.
	 */
	public static void installKBFromKNSServer(URL dataset, URL knsServer) throws KBNotFoundException, Exception {
		URL file = KNSTable.resolve(dataset, knsServer);
		if(file == null) {
			throw new KBNotResolvedException(dataset.toString());
		}
		installKB(dataset, file);
	}
	
	/**
	 * Install a given knowledge base resolved by a given KNS service.
	 * 
	 * @param knowledge base the URL of the knowledge base that is going to be installed.
	 * @param knsServer the URL of the KNS service that will be used to lookup.
	 * @param streamListener the listener to be invoked when the knowledge base get dereferenced.
	 * 
	 * @throws Exception if any error occurs during the indexing process.
	 * @throws KBNotResolvedException if the given knowledge base can not be resolved.
	 */
	public static void installKBFromKNSServer(URL dataset, URL knsServer, String format, String version) throws KBNotFoundException, Exception {
		URL file = KNSTable.resolve(dataset, knsServer, format, version);
		if(file == null) {
			throw new KBNotResolvedException(dataset.toString());
		}
		installKB(dataset, file, format, version);
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
		String[] knowledgeBasesPaths = getResources(factory, install, knowledgeNames);
		return TDB.query(sparql, knowledgeBasesPaths);
	}
	
	public static Model createModel(InputStreamFactory factory, boolean install, URL... knowledgeNames) throws Exception {
		String[] knowledgeBasesPaths = getResources(factory, install, knowledgeNames);
		return TDB.createModel(knowledgeBasesPaths);
	}
	
	private static String[] getResources(InputStreamFactory factory, boolean install, URL... urls) throws Exception {
		String[] knowledgeBasesPaths = new String[urls.length];
		int i = 0;
		for(URL knowledgeBase : urls) {
			File localDataset = locateKB(knowledgeBase);
			if(localDataset == null && install) {
				URL kbSource = resolve(knowledgeBase);
				if(kbSource == null) {
					KBNotFoundException e = new KBNotFoundException("Knowledge base " + knowledgeBase.toString() + 
							" could not be found in any of the availables KNS servers.");
					throw e;
				}
				try(InputStream is = factory.get(kbSource)) {
					installKB(is, knowledgeBase);
				} catch (Exception e) {
					throw new KBDereferencingException(knowledgeBase.toString());
				}
				localDataset = locateKB(knowledgeBase);
			} else if(localDataset == null){
				KBNotFoundException e = new KBNotFoundException("Knowledge base " + knowledgeBase.toString() + " is not installed."
						+ " You can install it using the command install.");
				throw e;
			}
			knowledgeBasesPaths[i] = localDataset.getAbsolutePath();
			i++;
		}
		return knowledgeBasesPaths;
	}
	
	/**
	 * Create a Model with a given Knowledge base Names.
	 * 
	 * @param knowledgeNames the Knowledge base Names to be queried.
	 *        Warning: This method automatically dereference Knowledge bases. 
	 * 
	 * @return a Model containing the Knowledge bases.
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
	 * @param knowledgeNames the Knowledge base Names to be queried.
	 *        Warning: This method automatically dereference Knowledge bases. 
	 * 
	 * @return a Model containing the Knowledge bases.
	 * 
	 * @throws Exception if some error occurs during the knowledge base 
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
	 * @return a ResultSet containing the result to a given query
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
	 * @param knowledgeNames the Knowledge base Names to be queried.
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
	 * @param knowledgeNames the Knowledge base Names to be queried.
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
	 * @param knowledgeNames the Knowledge base Names that are going to be sliced.
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
	 * @param format the format of the KB file.
	 * @param version the version of the KB file.
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
	 * @return the resolved URL.
	 * 
	 * @throws Exception if any error occurs during the operation.
	 */
	public static URL resolve(URL resourceURL) throws Exception {
		KibeKNSServerList kibeKNSServerList = new KibeKNSServerList();
		return resolve(kibeKNSServerList, resourceURL);
	}
		
	/**
	 * Iterate over all available KNS services with a given visitor.
	 * 
	 * @param KNSVisitor an implementation of KNSVisitor.
	 * 
	 * @throws IOException if any error occurs during the operation.
	 */
	public static void visit(KNSServerListVisitor visitor) throws Exception {
		KibeKNSServerList kibeKNSServerList = new KibeKNSServerList();
		kibeKNSServerList.visit(visitor);
	}
}
