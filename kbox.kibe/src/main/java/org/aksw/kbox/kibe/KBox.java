package org.aksw.kbox.kibe;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.aksw.kbox.InputStreamFactory;
import org.aksw.kbox.apple.Install;
import org.aksw.kbox.apple.Locate;
import org.aksw.kbox.apple.AppLocate;
import org.aksw.kbox.apple.ZipAppInstall;
import org.aksw.kbox.kibe.exception.KBDereferencingException;
import org.aksw.kbox.kibe.exception.KBNotResolvedException;
import org.aksw.kbox.kibe.stream.DefaultInputStreamFactory;
import org.aksw.kbox.kibe.tdb.TDB;
import org.aksw.kbox.kibe.utils.ZIPUtil;
import org.aksw.kbox.kns.CustomParamKNSServerList;
import org.aksw.kbox.kns.KBResolver;
import org.aksw.kbox.kns.KN;
import org.aksw.kbox.kns.KNSServer;
import org.aksw.kbox.kns.KNSServerList;
import org.aksw.kbox.kns.KNSServerListVisitor;
import org.aksw.kbox.kns.Resolver;
import org.aksw.kbox.kns.ServerAddress;
import org.apache.commons.lang.NullArgumentException;
import org.askw.kbox.kns.exception.ResourceNotResolvedException;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

/**
 * 
 * @author {@linkplain http://emarx.org}
 *
 */
public class KBox extends org.aksw.kbox.kns.KBox {

	public final static String DEFAULT_VERSION = "0";
	public final static String DEFAULT_FORMAT = "kibe";
	
	/**
	 * Install the given knowledge base.
	 * The installation try to resolve the knowledge base by the available KNS.
	 * 
	 * @param knowledgeBase the {@link URL} of the knowledge base.
	 * 
	 * @throws Exception if any error occurs during the operation. 
	 * @throws ResourceNotResolvedException if the given knowledge base can not be resolved.
	 */
	public static void install(URL knowledgeBase) throws Exception {
		install(knowledgeBase, new DefaultInputStreamFactory());
	}
	
	/**
	 * Install the given knowledge base.
	 * The installation try to resolve the knowledge base by the available KNS.
	 * 
	 * @param knowledgeBase the {@link URL} of the knowledge base.
	 * @param isFactory the {@link InputStreamFactory} that will be used to open stream with the target knowledge base.
	 * 
	 * @throws Exception if any error occurs during the operation. 
	 * @throws KBNotResolvedException if the given knowledge base can not be resolved.
	 */
	public static void install(URL knowledgeBase, InputStreamFactory isFactory) throws Exception {
		KBResolver resolver = new KBResolver();
		install(knowledgeBase, resolver, isFactory);
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
	 * @param install a customized {@link Install} method for installation.
	 * 
	 * @throws Exception if the resource does not exist or can not be copied or some
	 *             error occurs during the resource publication.
	 */
	public static void install(URL source, URL dest, Install install)
			throws Exception {
		install(source, dest, DEFAULT_FORMAT, DEFAULT_VERSION, install);
	}
	
	/**
	 * Creates a mirror for the given file in a given {@link URL}. This function allows
	 * KBox to serve files to applications, acting as proxy to the mirrored
	 * file. The file that is published in a give {@link URL} will be located when the
	 * client execute the function {@link KBox#getResource(URL)}.
	 * 
	 * @param url the {@link URL} that will be resolved.
	 * @param install a customized method for installation.
	 * @param resolver the resolver of the given KNS and resourceURL.
	 * @param isFactory a {@link InputStreamFactory} to be used to create a stream connection with the resolved resource's URL.
	 * 
	 * @throws ResourceNotResolvedException if the given resource can not be resolved.
	 * @throws Exception if the resource does not exist or can not be copied or some
	 *             error occurs during the resource publication.
	 */
	public static void install(URL knsServer, URL resourceURL, Resolver resolver, Install install, InputStreamFactory isFactory)
			throws ResourceNotResolvedException, Exception {
		KN resolvedKN = resolver.resolve(knsServer, resourceURL);
		assertNotNull(new KBNotResolvedException(resourceURL.toString()), resolvedKN);
		install(resolvedKN, 
				resourceURL,
				install,
				isFactory);
	}
	
	/**
	 * Creates a mirror for the given file in a given {@link URL}. This function allows
	 * KBox to serve files to applications, acting as proxy to the mirrored
	 * file. The file that is published in a give {@link URL} will be located when the
	 * client execute the function {@link KBox#getResource(URL)}.
	 * 
	 * @param url the {@link URL} that will be resolved.
	 * @param install an {@link Install} method to be used for installation.
	 * @param resolver the resolver of the given KNS and resourceURL.
	 * @param isFactory a {@link InputStreamFactory} to be used to create a stream connection with the resolved resource's URL.
	 * 
	 * @throws NullArgumentException if any of the arguments is {@link null}.
	 * @throws Exception if the resource does not exist or can not be copied or some
	 *             error occurs during the resource publication.
	 */
	private static void install(KN kn, URL resourceURL, Install install, InputStreamFactory isFactory)
			throws NullArgumentException, Exception {		
		assertNotNull(new NullArgumentException("kn"), kn);
		assertNotNull(new NullArgumentException("resourceURL"), resourceURL);
		assertNotNull(new NullArgumentException("install"), install);
		assertNotNull(new NullArgumentException("isFactory"), isFactory);
		install(kn.getTargetURL(),
				resourceURL,
				getValue(kn.getFormat(), DEFAULT_FORMAT),
				getValue(kn.getVersion(), DEFAULT_VERSION),
				install,
				isFactory);
	}
	
	/**
	 * Creates a mirror for the given file in a given {@link URL}. This function allows
	 * KBox to serve files to applications, acting as proxy to the mirrored
	 * file. The file that is published in a give {@link URL} will be located when the
	 * client execute the function {@link KBox#getResource(URL)}.
	 * 
	 * @param knsServerURL a {@link KNSServer}'s {@link URL} that will be used to resolve the given <code>resourceURL</code>.
	 * @param resourceURL the {@link URL} that will be resolved.
	 * @param install an {@link Install} method to be used for installation.
	 * @param resolver the {@link Resolver} of the given KNS and <code>resourceURL</code>.
	 * 
	 * @throws KBNotResolvedException if the given KB can not be resolved.
	 * @throws Exception if the resource does not exist or can not be copied or some
	 *             error occurs during the resource publication.
	 */
	public static void install(URL knsServerURL, URL resourceURL, Resolver resolver, Install install)
			throws KBNotResolvedException, Exception {
		KN resolvedKN = resolver.resolve(knsServerURL, resourceURL);
		assertNotNull(new KBNotResolvedException(resourceURL.toString()), resolvedKN);
		install(resolvedKN, 
				resourceURL,
				install,
				new DefaultInputStreamFactory());
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
	 * @param install a customized {@link Install} method for installation.
	 * 
	 * @throws Exception if the resource does not exist or can not be copied or some
	 *             error occurs during the resource publication.
	 */
	public static void install(URL source, URL dest, String format, Install install)
			throws Exception {
		install.install(source, dest, format, DEFAULT_VERSION);
	}
	
	/**
	 * Resolve the given {@link URL} in the available KNS.
	 * The first KNS to be checked is the default KNS, 
	 * thereafter the user's KNS.
	 * 
	 * @param knsServerList list of KNS servers
	 * @param resourceURL the {@link URL} to be resolved by the KNS.
	 * @param resolver the resolver to resolve the given resourceURL in the {@link CustomParamKNSServerList}.
	 * @param install a customized {@link Install} method for installation.
	 * 
	 * @return the resolved {@link  KN} or {@link null} if it is not resolved.
	 * 
	 * @throws KBNotResolvedException if the given KB can not be resolved.
	 * @throws Exception if any error occurs during the operation.
	 */
	public static void install(CustomParamKNSServerList knsServerList, URL resourceURL, Resolver resolver, Install install) throws KBNotResolvedException, Exception {
		KN resolvedKN = resolve(knsServerList, resourceURL, resolver);
		assertNotNull(new KBNotResolvedException(resourceURL.toString()), resolvedKN);
		install(resolvedKN, 
				resourceURL,
				install,
				new DefaultInputStreamFactory());
	}
	
	/**
	 * Resolve the given {@link URL} in the available KNS.
	 * The first KNS to be checked is the default KNS, 
	 * thereafter the user's KNS.
	 * 
	 * @param knsServerList list of KNS servers
	 * @param resourceURL the {@link URL} to be resolved by the KNS.
	 * @param resolver the resolver to resolve the given resourceURL in the {@link CustomParamKNSServerList}.
	 * @param install a customized {@link Install} method for installation.
	 * @param isFactory a {@link InputStreamFactory} to be used to create a stream connection with the resolved resource's URL.
	 * 
	 * @return the resolved {@link KN} or {@link null} if it is not resolved.
	 * 
	 * @throws KBNotResolvedException if the given KB can not be resolved.
	 * @throws Exception if any error occurs during the operation.
	 */
	public static void install(KNSServerList knsServerList, URL resourceURL, Resolver resolver, Install install, InputStreamFactory isFactory) throws KBNotResolvedException, Exception {
		KN resolvedKN = resolve(knsServerList, resourceURL, resolver);
		assertNotNull(new KBNotResolvedException(resourceURL.toString()), resolvedKN);
		install(resolvedKN, 
				resourceURL,
				install,
				isFactory);
	}
	
	/**
	 * Resolve the given {@link URL} in the available KNS.
	 * The first KNS to be checked is the default KNS, 
	 * thereafter the user's KNS.
	 * 
	 * @param knsServerList list of KNS servers
	 * @param resourceURL the {@link URL} to be resolved by the KNS.
	 * @param resolver the resolver to resolve the given resourceURL in the {@link CustomParamKNSServerList}.
	 * 
	 * @return the resolved {@link  KN} or {@link null} if it is not resolved.
	 * 
	 * @throws Exception if any error occurs during the operation.
	 */
	public static void install(KNSServerList knsServerList, URL resourceURL, String format, String version, Resolver resolver, Install install, InputStreamFactory isFactory) throws KBNotResolvedException, Exception {
		KN resolvedKN = resolve(knsServerList, resourceURL, format, version, resolver);
		assertNotNull(new KBNotResolvedException(resourceURL.toString()), resolvedKN);
		install(resolvedKN, 
				resourceURL,
				install,
				isFactory);
	}
	
	/**
	 * Resolve the given {@link URL} in the available KNS.
	 * The first KNS to be checked is the default KNS, 
	 * thereafter the user's KNS.
	 * 
	 * @param knsServerList list of KNS servers
	 * @param resourceURL the {@link URL} to be resolved by the KNS.
	 * @param resolver the resolver to resolve the given resourceURL in the {@link CustomParamKNSServerList}.
	 * 
	 * @return the resolved {@link  KN} or {@link null} if it is not resolved.
	 * 
	 * @throws Exception if any error occurs during the operation.
	 */
	public static void install(URL resourceURL, Resolver resolver, Install install) throws Exception {
		DefaultKNSServerList kibeKNSServerList = new DefaultKNSServerList();
		install(kibeKNSServerList, resourceURL, resolver, install);		
	}
	
	
	
	
	
	/**
	 * Creates a mirror for the given file in a given {@link URL}. This function allows
	 * KBox to serve files to applications, acting as proxy to the mirrored
	 * file. The file that is published in a give {@link URL} will be located when the
	 * client execute the function {@link KBox#getResource(URL)}.
	 * 
	 * @param url the {@link URL} that will be resolved.
	 * @param install a customized method for installation.
	 * @param resolver the resolver of the given KNS and resourceURL.
	 * 
	 * @throws ResourceNotResolvedException if the given resource can not be resolved.
	 * @throws Exception if the resource does not exist or can not be copied or some
	 *             error occurs during the resource publication.
	 */
	public static void install(URL resourceURL, Resolver resolver, Install install, InputStreamFactory isFactory)
			throws ResourceNotResolvedException, Exception {
		DefaultKNSServerList kibeKNSServerList = new DefaultKNSServerList();
		install(kibeKNSServerList, 
				resourceURL,
				resolver,
				install,
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
	 * @throws ResourceNotResolvedException if the given resource can not be resolved.
	 * @throws Exception if the resource does not exist or can not be copied or some
	 *             error occurs during the resource publication.
	 */
	public static void install(URL knsServer, URL resourceURL, String format, String version, Resolver resolver, Install install)
			throws KBNotResolvedException, Exception {		
		KN resolvedKN = resolver.resolve(knsServer, resourceURL, format, version);
		assertNotNull(new KBNotResolvedException(resourceURL.toString()), resolvedKN);
		install(resolvedKN, 
				resourceURL,
				install,
				new DefaultInputStreamFactory());
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
	 * @param install a customized method for installation.
	 * 
	 * @throws Exception if the resource does not exist or can not be copied or some
	 *             error occurs during the resource publication.
	 */
	public static void install(URL source, URL dest, Install install, InputStreamFactory isFactory)
			throws Exception {
		install(source, dest, DEFAULT_FORMAT, DEFAULT_VERSION, install, isFactory);
	}
	
	/**
	 * Install a given KB resolved by a given {@link Resolver}.
	 * 
	 * @param knowledgebase the KB Name that will be resolved and installed.
	 * @param resolver the {@link Resolver} that will resolve the KB Name.
	 * 
	 * @throws KBNotResolvedException if the given knowledge base can not be resolved.
	 * @throws Exception if any error occurs during the indexing process.
	 */
	public static void install(URL knowledgebase, Resolver resolver) throws KBNotResolvedException, Exception {
		DefaultKNSServerList knsServerList = new DefaultKNSServerList();
		ZipAppInstall install = new ZipAppInstall();
		install(knsServerList, knowledgebase, resolver, install);
	}
	
	/**
	 * Install a given KB resolved by a given {@link Resolver}.
	 * This method uses the default <b>kibe</b> format implemented by <i>Kibe library</i> and its
	 * default version <b>0<b>.
	 * 
	 * @param knowledgebase the KB Name that will be resolved and installed.
	 * @param resolver the {@link Resolver} that will resolve the KB Name.
	 * @param isFactory the {@link InputStreamFactory}.
	 * 
	 * @throws KBNotResolvedException if the given knowledge base can not be resolved.
	 * @throws Exception if any error occurs during the indexing process.
	 */
	public static void install(URL knowledgebase, 
			Resolver resolver, 
			InputStreamFactory isFactory) throws KBNotResolvedException, Exception {
		DefaultKNSServerList knsServerList = new DefaultKNSServerList();
		ZipAppInstall intall = new ZipAppInstall();
		install(knsServerList, knowledgebase, resolver, intall, isFactory);
	}
	
	/**
	 * Install a given index file in the given knowledge base.
	 * 
	 * @param target the name of the target knowledge base whereas the index will be installed.
	 * @param source the {@link InputStream} index file to be installed.
	 * @param format the KB format.
	 * @param version the KB version.
	 * 
	 * @throws Exception if any error occurs during the indexing process.
	 */
	public static void install(InputStream source, URL target, String format, String version) throws Exception {
		install(source, target, format, version, new ZipAppInstall());
	}
	
	/**
	 * Install a given index file in the given knowledge base.
	 * 
	 * @param source the {@link URL} of the index file to be installed.
	 * @param target the {@link URL} of the knowledge base whereas the index will be installed.
	 * @param isFactory the {@link InputStreamFactory}.
	 * 
	 * @throws Exception if any error occurs during the indexing process.
	 */
	public static void install(URL source, URL target, InputStreamFactory isFactory) throws Exception {
		install(source, target, new ZipAppInstall(), isFactory);
	}
	
	/**
	 * Install a given index file in the given knowledge base.
	 * 
	 * @param knowledgebase the {@link URL} of the index file to be installed.
	 * @param format the KB format.
	 * @param isFactory a {@link InputStreamFactory} to be used to create a stream connection with the resolved resource's URL.
	 * 
	 * @throws KBNotResolvedException if the given knowledge base can not be resolved.
	 * @throws Exception if any error occurs during the indexing process.
	 */
	public static void install(URL knowledgebase, 
			String format, 
			InputStreamFactory isFactory) throws KBNotResolvedException, Exception {
		KBResolver resolver = new KBResolver();
		ZipAppInstall install = new ZipAppInstall();
		install(knowledgebase, resolver, install, isFactory);
	}
	
	/**
	 * Install a given index file in the given knowledge base.
	 * 
	 * @param source the {@link URL} of the index file to be installed.
	 * @param target the {@link URL} of the knowledge base whereas the index will be installed.
	 * @param isFactory a {@link InputStreamFactory} to be used to create a stream connection with the resolved resource's URL.
	 * @param format the KB format.
	 * 
	 * @throws Exception if any error occurs during the indexing process.
	 */
	public static void install(URL source, URL target, String format, InputStreamFactory isFactory) throws Exception {
		install(source, target, format, DEFAULT_VERSION, isFactory);
	}
	
	/**
	 * Install a given index file in the given knowledge base.
	 * This method uses the default install method {@link ZipAppInstall}.
	 * 
	 * @param source the {@link URL} of the index file to be installed.
	 * @param target the {@link URL} of the knowledge base whereas the index will be installed.
	 * @param format the KB format.
	 * @param version the KB version.
	 * @param isFactory a {@link InputStreamFactory} to be used to create a stream connection with the resolved resource's URL.
	 * 
	 * @throws Exception if any error occurs during the indexing process.
	 */
	public static void install(URL source, 
			URL target, 
			String format,
			String version, 
			InputStreamFactory isFactory) throws Exception {
		install(source, target, format, version, new ZipAppInstall());
	}

	/**
	 * Install a given index file in the given knowledge base.
	 * This method uses the default install implemented by 
	 * {@link ZipAppInstall} class, the default format <b>kibe</b> and 
	 * the default version <b>0</b>. 
	 * 
	 * @param source the {@link URL} of the index file to be installed.
	 * @param target the {@link URL} of the knowledge base whereas the index will be installed.
	 * 
	 * @throws Exception if any error occurs during the indexing process.
	 */
	public static void install(URL source, URL target) throws Exception {
		install(source, target, new ZipAppInstall());
	}
	
	/**
	 * Install a given index file in the given knowledge base.
	 * This method uses the default version <b>0</b>.
	 * 
	 * @param source the {@link URL} of the index file to be installed.
	 * @param target the {@link URL} of the knowledge base whereas the index will be installed.
	 * @param format the KB format.
	 * 
	 * @throws Exception if any error occurs during the indexing process.
	 */
	public static void install(URL source, URL target, String format) throws Exception {
		install(source, target, format, DEFAULT_VERSION);
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
	 * @throws Exception if any error occurs during the indexing process.
	 */
	public static void install(URL source, URL target, String format, String version) throws Exception {
		install(source, target, format, version, new ZipAppInstall());
	}
	
	/**
	 * Install a given knowledge base resolved by a given KNS service.
	 * 
	 * @param knsServer the {@link URL} of the KNS service that will be used to lookup.
	 * @param knowledgebase the {@link URL} of the knowledge base that is going to be installed.
	 * @param streamListener the listener to be invoked when the knowledge base get dereferenced.
	 * 
	 * @throws KBNotResolvedException if the given knowledge base can not be resolved.
	 * @throws Exception if any error occurs during the indexing process.
	 */
	public static void installKBFromKNSServer(URL knsServer, URL knowledgebase) throws KBNotResolvedException, Exception {
		install(knsServer, knowledgebase, new DefaultInputStreamFactory());
	}
	
	public static void install(URL kbNameURL, String format, String version,
			InputStreamFactory inputStreamFactory) throws Exception {
		KBResolver resolver = new KBResolver();
		DefaultKNSServerList knsServerList = new DefaultKNSServerList();
		install(knsServerList, kbNameURL, format, version, resolver, new ZipAppInstall(), inputStreamFactory);
	}
	
	/**
	 * Install a given knowledge base resolved by a given KNS service.
	 * This method uses the default <b>kibe</b> format and its default version <b>0</b>. 
	 * 
	 * @param knsServer the {@link URL} of the KNS service that will be used to lookup.
	 * @param knowledgebase the {@link URL} of the knowledge base that is going to be installed.
	 * @param isFactory a {@link InputStreamFactory} to be used to create a stream connection with the resolved resource's URL.
	 * 
	 * @throws KBNotResolvedException if the given knowledge base can not be resolved.
	 * @throws Exception if any error occurs during the indexing process.
	 */
	public static void installFromKNSServer(URL knsServer, 
			URL knowledgebase, 
			InputStreamFactory isFactory) throws KBNotResolvedException, Exception {
		KN resolvedKN = resolve(knsServer, knowledgebase);
		assertNotNull(new KBNotResolvedException(knowledgebase.toString()), resolvedKN);
		install(resolvedKN.getTargetURL(),
				knowledgebase,
				getValue(resolvedKN.getFormat(), DEFAULT_FORMAT),
				getValue(resolvedKN.getVersion(), DEFAULT_VERSION), 
				isFactory);
	}
	
	/**
	 * Install a given knowledge base resolved by a given KNS service.
	 * This method uses the default version 0.
	 * 
	 * @param knsServer the {@link URL} of the KNS service that will be used to lookup.
	 * @param knowledgebase the {@link URL} of the knowledge base that is going to be installed.
	 * @param format the KB format.
	 * 
	 * @throws KBNotResolvedException if the given knowledge base can not be resolved.
	 * @throws Exception if any error occurs during the indexing process.
	 */
	public static void installFromKNSServer(URL knsServer, 
			URL knowledgebase, 
			String format) throws KBNotResolvedException, Exception {
		installFromKNSServer(knsServer, 
				knowledgebase,
				format,
				new DefaultInputStreamFactory());
	}
	
	/**
	 * Install a given knowledge base resolved by a given KNS service.
	 * 
	 * @param knsServer the {@link URL} of the KNS service that will be used to lookup.
	 * @param knowledgebase the {@link URL} of the knowledge base that is going to be installed.
	 * @param format the KB format.
	 * @param version the KB version.
	 *
	 * 
	 * @throws KBNotResolvedException if the given knowledge base can not be resolved.
	 * @throws Exception if any error occurs during the indexing process.
	 */
	public static void installFromKNSServer(URL knsServer, 
			URL knowledgebase, 
			String format, 
			String version) throws KBNotResolvedException, Exception {
		installFromKNSServer(knsServer, knowledgebase, format, version, new DefaultInputStreamFactory());
	}
	
	/**
	 * Install a given knowledge base resolved by a given KNS service.
	 * 
	 * @param knowledge base the {@link URL} of the knowledge base that is going to be installed.
	 * @param knsServer the {@link URL} of the KNS service that will be used to lookup.
	 * @param format the KB format.
	 * @param isFactory stream factory to be used to open stream with the source knowledge base.
	 * 
	 * @throws KBNotResolvedException if the given knowledge base can not be resolved.
	 * @throws Exception if any error occurs during the indexing process.
	 */
	public static void installFromKNSServer(URL knsServer, 
			URL knowledgebase, 
			String format, 
			InputStreamFactory isFactory) throws KBNotResolvedException, Exception {
		KN resolvedKN = resolve(knsServer, knowledgebase, format);
		assertNotNull(new KBNotResolvedException(knowledgebase.toString()), resolvedKN);
		install(resolvedKN.getTargetURL(), 
				knowledgebase, getValue(format, DEFAULT_FORMAT), 
				getValue(resolvedKN.getVersion(), DEFAULT_VERSION), isFactory);
	}
	
	/**
	 * Install a given knowledge base resolved by a given KNS service.
	 * This method uses {@link ZipAppInstall} as install method.
	 * 
	 * @param knowledge base the {@link URL} of the knowledge base that is going to be installed.
	 * @param knsServer the {@link URL} of the KNS service that will be used to lookup.
	 * @param format the KB format.
	 * @param version the KB version.
	 * @param isFactory stream factory to be used to open stream with the source knowledge base.
	 * 
	 * @throws KBNotResolvedException if the given knowledge base can not be resolved.
	 * @throws Exception if any error occurs during the indexing process.
	 */
	public static void installFromKNSServer(URL knsServer, 
			URL knowledgebase, 
			String format, 
			String version, 
			InputStreamFactory isFactory) throws KBNotResolvedException, Exception {
		KN resolvedKN = resolve(knsServer, knowledgebase, format, version);
		assertNotNull(new KBNotResolvedException(knowledgebase.toString()), resolvedKN);
		install(resolvedKN.getTargetURL(), knowledgebase, format, version, isFactory);
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
		String[] knowledgeBasesPaths = getKowledgebases(isFactory, install, knowledgeNames);
		return TDB.query(sparql, knowledgeBasesPaths);
	}
	
	/**
	 * 
	 * Query the given knowledge bases.
	 * 
	 * @param isFactory a {@link InputStreamFactory} to stream the given KB in case they are not locally available.
	 * @param install when true the knowledge base is auto-dereferenced when not found or not otherwise.
	 * @param knowledgeNames the knowledge base Names to be queried.
	 * 
	 * @return a model with the given RDF KB names.
	 * 
	 * @throws Exception if any of the given knowledge bases can not be found.
	 */
	public static Model createModel(InputStreamFactory isFactory, 
			boolean install, 
			URL... knowledgeNames) throws Exception {
		String[] knowledgeBasesPaths = getKowledgebases(isFactory, install, knowledgeNames);
		return TDB.createModel(knowledgeBasesPaths);
	}
	
	private static String[] getKowledgebases(InputStreamFactory isFactory, 
			boolean install, 
			URL... urls) throws Exception {
		String[] knowledgeBasesPaths = new String[urls.length];
		int i = 0;
		Locate kbLocate = new AppLocate();
		KBResolver kbResolver = new KBResolver();
		DefaultKNSServerList knsServerList = new DefaultKNSServerList();
		for(URL knowledgeBase : urls) {
			try {
				File kbDir = getResource(knsServerList, 
						knowledgeBase,
						kbLocate,
						DEFAULT_FORMAT,
						DEFAULT_VERSION,
						kbResolver,
						new ZipAppInstall(), 
						isFactory,
						install);
				knowledgeBasesPaths[i] = kbDir.getAbsolutePath();
				i++;
			} catch (ResourceNotResolvedException e) {
				throw new KBNotResolvedException("Knowledge base " + knowledgeBase.toString() + " is not installed."
						+ " You can install it using the command install.");
			} catch (KBDereferencingException e) {
				throw new KBDereferencingException(knowledgeBase.toString(), e);
			}
			
		}
		return knowledgeBasesPaths;
	}
	
	/**
	 * Create a {@link Model} with a given RDF Knowledge Base Names.
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
		return createModel(new DefaultInputStreamFactory(), true, knowledgeNames);
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
		return createModel(new DefaultInputStreamFactory(), install, knowledgeNames);
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
	 * Query a given Query a given KBox service endpoints.
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
		Model model = createModel(new DefaultInputStreamFactory(), true, knowledgeNames);
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
		Model model = createModel(new DefaultInputStreamFactory(), install, knowledgeNames);
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
		return locate(url, DEFAULT_FORMAT, DEFAULT_VERSION, kbLocate);
	}
	
	/**
	 * Returns the local Knowledge base directory.
	 * 
	 * @param url that will be used to locate the Knowledge base.
	 * @param format the KB format.
	 * 
	 * @return the local mirror directory of the knowledge base.
	 *  
	 * @throws Exception if any error occurs while locating the Knowledge Base. 
	 */
	public static File locate(URL url, String format) throws Exception {		
		return locate(url, format, DEFAULT_VERSION);
	}
	
	/**
	 * Returns the local Knowledge base directory.
	 * 
	 * @param url that will be used to locate the knowledge base.
	 * @param format the KB format.
	 * @param version the KB version.
	 * 
	 * @return the local mirror directory of the knowledge base.
	 *  
	 * @throws Exception if any error occurs while locating the Knowledge Base. 
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
	 * Resolve the given {@link URL} in the available KNS.
	 * The first KNS to be checked is the default KNS, 
	 * thereafter the user's KNS.
	 * 
	 * @param knsServerURL the {@link URL} of the KNS Server.
	 * @param resourceURL the {@link URL} to be resolved by the KNS.
	 * 
	 * @return the resolved {@link KN} or {@link null} if the given resource's {@link URL} can not be resolved.
	 * 
	 * @throws IOException if any error occurs during the operation.
	 */
	public static KN resolve(URL knsServerURL, URL resourceURL) throws Exception {
		KBResolver resolver = new KBResolver();
		return resolve(knsServerURL, resourceURL, resolver);
	}
	
	/**
	 * Resolve the given {@link URL} in the available KNS.
	 * The first KNS to be checked is the default KNS, 
	 * thereafter the user's KNS.
	 * 
	 * @param knsServerURL the {@link URL} of the KNS Server.
	 * @param resourceURL the {@link URL} to be resolved by the KNS.	 
	 * @param format the KB format.
	 * 
	 * @return the resolved {@link KN} or {@link null} if the given resource's {@link URL} can not be resolved.
	 * 
	 * @throws Exception if any error occurs during the operation.
	 */
	public static KN resolve(URL knsServerURL, URL resourceURL, String format) throws Exception {
		KBResolver resolver = new KBResolver();
		return resolve(knsServerURL, resourceURL, format, resolver);
	}
	
	/**
	 * Resolve the given {@link URL} in the available KNS.
	 * The first KNS to be checked is the default KNS, 
	 * thereafter the user's KNS.
	 * 
	 * @param knsServerURL the {@link URL} of the KNS Server.
	 * @param resourceURL the {@link URL} to be resolved by the KNS.	 
	 * @param format the KB format. 
	 * @param version the KB version.
	 * 
	 * @return the resolved {@link KN} or {@link null} if the given resource's {@link URL} can not be resolved.
	 * 
	 * @throws Exception if any error occurs during the operation.
	 */
	public static KN resolve(URL knsServerURL, URL resourceURL, String format, String version) throws Exception {
		KBResolver resolver = new KBResolver();
		return resolve(knsServerURL, resourceURL, format, version, resolver);
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
	
}
