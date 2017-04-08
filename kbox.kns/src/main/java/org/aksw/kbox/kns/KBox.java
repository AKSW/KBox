package org.aksw.kbox.kns;

import java.io.IOException;
import java.net.URL;

import org.aksw.kbox.Install;
import org.askw.kbox.kns.exception.ResourceNotResolvedException;

public class KBox extends org.aksw.kbox.KBox {

	/**
	 * Install the given URL in your personal Knowledge Name Service.
	 * This service will be used to Lookup Knowledge bases.
	 * 
	 * @param url the URL of the KNS (Knowledge Name Service)
	 */
	public static void installKNS(URL url) {
		KNSServerList knsServerList = new KNSServerList();
		knsServerList.add(url.toString());
	}
	
	/**
	 * Remove the given URL from your personal Knowledge Name Service.
	 * 
	 * @param url the URL of the KNS (Knowledge Name Service)
	 */
	public static void removeKNS(URL url) {
		KNSServerList knsServerList = new KNSServerList();
		knsServerList.remove(url.toString());
	}
		
	/**
	 * Resolve the given URL in the available KNS.
	 * The first KNS to be checked is the default KNS, 
	 * thereafter the user's KNS.
	 * 
	 * @param knsServerList list of KNS servers
	 * @param resourceURL the URL to be resolved by the KNS.
	 * 
	 * @return the resolved KN or NULL if it is not resolved.
	 * 
	 * @throws Exception if any error occurs during the operation.
	 */
	public static KN resolve(KNSServerList knsServerList, URL resourceURL) throws Exception {
		KBResolver resolver = new KBResolver();
		return resolve(knsServerList, resourceURL, resolver);
	}
	
	/**
	 * Resolve the given URL in the available KNS.
	 * The first KNS to be checked is the default KNS, 
	 * thereafter the user's KNS.
	 * 
	 * @param knsServerList list of KNS servers
	 * @param resourceURL the URL to be resolved by the KNS.
	 * 
	 * @return the resolved KN or NULL if it is not resolved.
	 * 
	 * @throws Exception if any error occurs during the operation.
	 */
	public static KN resolve(KNSServerList knsServerList, URL resourceURL, Resolver resolver) throws Exception {
		KNSResolverVisitor resolveVisitor = new KNSResolverVisitor(resourceURL, resolver);
		knsServerList.visit(resolveVisitor);
		KN resolvedKN = resolveVisitor.getResolvedKN();
		return resolvedKN;
	}
	
	/**
	 * Creates a mirror for the given file in a given URL. This function allows
	 * KBox to serve files to applications, acting as proxy to the mirrored
	 * file. The file that is published in a give URL u will be located when the
	 * client execute the function KBox.getResource(u).
	 * 
	 * @param url the URL that will be resolved.
	 * @param install a customized method for installation.
	 * 
	 * @throws Exception if the resource does not exist or can not be copied or some
	 *             error occurs during the resource publication.
	 * @throws ResourceNotResolvedException if the given resource can not be resolved.
	 */
	public static void install(URL knsServer, URL resourceURL, Resolver resolver, Install install)
			throws ResourceNotResolvedException, Exception {		
		KN resolvedKN = resolver.resolve(knsServer, resourceURL);
		assertNotNull(new ResourceNotResolvedException(resourceURL.toString()), resolvedKN);
		install(resolvedKN.getTargetURL(), resourceURL, install);
	}
	
	/**
	 * Resolve the given URL in the available KNS.
	 * The first KNS to be checked is the default KNS, 
	 * thereafter the user's KNS.
	 * 
	 * @param knsServerList list of KNS servers.
	 * @param resourceURL the URL to be resolved by the KNS.
	 * 
	 * @return the resolved KN or NULL if it is not resolved.
	 * 
	 * @throws Exception if any error occurs during the operation.
	 */
	public static KN resolve(URL resourceURL, String format, String version) throws Exception {
		return resolve(new KNSServerList(), resourceURL, format, version);
	}
	
	/**
	 * Resolve the given URL in the available KNS.
	 * The first KNS to be checked is the default KNS, 
	 * thereafter the user's KNS.
	 * 
	 * @param knsServerList list of KNS servers.
	 * @param resourceURL the URL to be resolved by the KNS.
	 * 
	 * @return the resolved KN or NULL if it is not resolved.
	 * 
	 * @throws Exception if any error occurs during the operation.
	 */
	public static KN resolve(URL resourceURL, String format, String version, Resolver resolver) throws Exception {
		return resolve(new KNSServerList(), resourceURL, format, version, resolver);
	}
	
	/**
	 * Resolve the given URL in the available KNS.
	 * The first KNS to be checked is the default KNS, 
	 * thereafter the user's KNS.
	 * 
	 * @param knsServerList list of KNS servers.
	 * @param resourceURL the URL to be resolved by the KNS.	 
	 * @param format the KB format. 
	 * @param version the KB version.
	 * 
	 * @return the resolved KN or NULL if it is not resolved.
	 * 
	 * @throws Exception if any error occurs during the operation.
	 */
	public static KN resolve(KNSServerList knsServerList, URL resourceURL, String format, String version) throws Exception {
		KBResolver resolver = new KBResolver();
		return resolve(knsServerList, resourceURL, format, version, resolver);
	}
	
	/**
	 * Resolve the given URL in the available KNS.
	 * The first KNS to be checked is the default KNS, 
	 * thereafter the user's KNS.
	 * 
	 * @param knsServerList list of KNS servers.
	 * @param resourceURL the URL to be resolved by the KNS.	 
	 * @param format the KB format. 
	 * @param version the KB version. 
	 * @param resolver the resolver that will resolve the given URL.
	 * 
	 * @return the resolved KN or NULL if it is not resolved.
	 * 
	 * @throws Exception if any error occurs during the operation.
	 */
	public static KN resolve(KNSServerList knsServerList, URL resourceURL, String format, String version, Resolver resolver) throws Exception {
		KNSResolverVisitor resolveVisitor = new KNSResolverVisitor(resourceURL, format, version, resolver);
		knsServerList.visit(resolveVisitor);
		KN resolvedKN = resolveVisitor.getResolvedKN();
		return resolvedKN;
	}
	
	/**
	 * Resolve the given URL in the available KNS.
	 * The first KNS to be checked is the default KNS, 
	 * thereafter the user's KNS.
	 * 
	 * @param knsServerURL the URL of the KNS Server.
	 * @param resourceURL the URL to be resolved by the KNS.	 
	 * @param format the KB format.
	 * @param resolver the resolver that will resolve the given URL.
	 * 
	 * @return the resolved KN or NULL if it is not resolved.
	 * 
	 * @throws IOException if any error occurs during the operation.
	 */
	public static KN resolve(URL knsServerURL, URL resourceURL, String format, Resolver resolver) throws ResourceNotResolvedException, Exception {
		KN resolvedKN = resolver.resolve(resourceURL, knsServerURL, format);
		return resolvedKN;
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
	 * @param resolver the resolver that will resolve the given URL.
	 * 
	 * @return the resolved KN or NULL if it is not resolved.
	 * 
	 * @throws IOException if any error occurs during the operation.
	 */
	public static KN resolve(URL knsServerURL, URL resourceURL, String format, String version, Resolver resolver) throws ResourceNotResolvedException, Exception {
		KN resolvedKN = resolver.resolve(resourceURL, knsServerURL, format, version);
		return resolvedKN;
	}
	
	/**
	 * Resolve the given URL in the available KNS.
	 * The first KNS to be checked is the default KNS, 
	 * thereafter the user's KNS.
	 * 
	 * @param knsServerURL the URL of the KNS Server.
	 * @param resourceURL the URL to be resolved by the KNS.
	 * @param resolver the resolver that will resolve the given URL.
	 * 
	 * @return the resolved URL or NULL if it is not resolved.
	 * 
	 * @throws IOException if any error occurs during the operation.
	 */
	public static KN resolve(URL knsServerURL, URL resourceURL, Resolver resolver) throws ResourceNotResolvedException, Exception {
		KN resolvedKN = resolver.resolve(knsServerURL, resourceURL);
		return resolvedKN;
	}
	
	public static void assertNotNull(Exception exception, Object... object) throws Exception {
		if(object == null) {
			throw exception;
		}
	}
}
