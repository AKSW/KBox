package org.aksw.kbox.kns;

import java.io.InputStream;
import java.net.URL;

import org.aksw.kbox.InputStreamFactory;
import org.askw.kbox.kns.exception.ResourceNotResolvedException;

public class KBox extends org.aksw.kbox.KBox {	

	/**
	 * Install the given {@link URL} in your personal Knowledge Name Service.
	 * This service will be used to Lookup Knowledge bases.
	 * 
	 * @param url the {@link URL} of the KNS (Knowledge Name Service)
	 */
	public static void installKNS(URL url) {
		CustomParamKNSServerList knsServerList = new CustomParamKNSServerList();
		knsServerList.add(url.toString());
	}
	
	/**
	 * Remove the given {@link URL} from your personal Knowledge Name Service.
	 * 
	 * @param url the {@link URL} of the KNS (Knowledge Name Service)
	 */
	public static void removeKNS(URL url) {
		CustomParamKNSServerList knsServerList = new CustomParamKNSServerList();
		knsServerList.remove(url.toString());
	}
		
	/**
	 * Resolve the given {@link URL} in the available KNS.
	 * The first KNS to be checked is the default KNS, 
	 * thereafter the user's KNS.
	 * 
	 * @param knsServerList list of KNS servers
	 * @param resourceURL the {@link URL} to be resolved by the KNS.
	 * 
	 * @return the resolved KN or <code>NULL</code> if it can not be resolved.
	 * 
	 * @throws {@link Exception} if any error occurs during the operation.
	 */
	public static KN resolve(CustomParamKNSServerList knsServerList, URL resourceURL) throws Exception {
		KBResolver resolver = new KBResolver();
		return resolve(knsServerList, resourceURL, resolver);
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
	 * @return the resolved {@link KN} or <code>NULL</code> if it can not be resolved.
	 * 
	 * @throws {@link Exception} if any error occurs during the operation.
	 */
	public static KN resolve(CustomParamKNSServerList knsServerList, 
			URL resourceURL, 
			Resolver resolver) throws Exception {
		KNSResolverVisitor resolveVisitor = new KNSResolverVisitor(resourceURL, resolver);
		knsServerList.visit(resolveVisitor);
		KN resolvedKN = resolveVisitor.getResolvedKN();
		return resolvedKN;
	}
	
	/**
	 * Creates a mirror for the given file in a given {@link URL}. This function allows
	 * KBox to serve files to applications, acting as proxy to the mirrored
	 * file. The file that is published in a give {@link URL} <code>u</code> will be located when the
	 * client execute the function <code>KBox.getResource(u)</code>.
	 * 
	 * @param source the {@link URL} of the file that is going to be published at the
	 *        given URL.
	 * @param dest the {@link URL} where the file is going to be published.
	 * @param install a customized method for installation.
	 * 
	 * @throws {@link Exception} if the resource does not exist or can not be copied or some
	 *             error occurs during the resource publication.
	 */
	public static void install(URL source, 
			URL dest, 
			String format, 
			String version, 
			AppInstall install)
			throws Exception {
		install.install(source, dest, format, version);
	}
	
	/**
	 * Creates a mirror for the given file in a given {@link URL}. This function allows
	 * KBox to serve files to applications, acting as proxy to the mirrored
	 * file. The file that is published in a give {@link URL} <code>u</code> will be located when the
	 * client execute the function <code>KBox.getResource(u)</code>.
	 * 
	 * @param source the {@link URL} of the file that is going to be published at the
	 *        given {@link URL}.
	 * @param dest the {@link URL} where the file is going to be published.
	 * @param install a customized method for installation.
	 * 
	 * @throws {@link Exception} if the resource does not exist or can not be copied or some
	 *             error occurs during the resource publication.
	 */
	public static void install(InputStream source, 
			URL dest, 
			String format, 
			String version, 
			AppInstall install)
			throws Exception {
		install.install(source, dest, format, version);
	}	
	
	/**
	 * Creates a mirror for the given file in a given {@link URL}. This function allows
	 * KBox to serve files to applications, acting as proxy to the mirrored
	 * file. The file that is published in a give {@link URL} <code>u</code> will be located when the
	 * client execute the function <code>KBox.getResource(u)</code>.
	 * 
	 * @param source the URL of the file that is going to be published at the
	 *        given {@link URL}.
	 * @param dest the {@link URL} where the file is going to be published.
	 * @param install a customized method for installation.
	 * 
	 * @throws {@link Exception} if the resource does not exist or can not be copied or some
	 *             error occurs during the resource publication.
	 */
	public static void install(URL source, 
			URL dest, 
			String format, 
			String version, 
			AppInstall install, 
			InputStreamFactory isFactory)
			throws Exception {
		install.install(source, dest, format, version, isFactory);
	}
	
	
	
	/**
	 * Resolve the given {@link URL} in the available KNS.
	 * The first KNS to be checked is the default KNS, 
	 * thereafter the user's KNS.
	 * 
	 * @param knsServerList list of KNS servers.
	 * @param resourceURL the {@link URL} to be resolved by the KNS.
	 * 
	 * @return the resolved {@link KN} or <code>NULL</code> if it can not be resolved.
	 * 
	 * @throws {@link Exception} if any error occurs during the operation.
	 * @throws {@link ResourceNotResolvedException} if the resource {@link URL} can not be resolved by the given KNSServer.
	 */
	public static KN resolve(URL resourceURL, 
			String format, 
			String version) throws Exception {
		return resolve(new CustomParamKNSServerList(), resourceURL, format, version);
	}
	
	/**
	 * Resolve the given URL in the available KNS.
	 * The first KNS to be checked is the default KNS, 
	 * thereafter the user's KNS.
	 * 
	 * @param knsServerList list of KNS servers.
	 * @param resourceURL the {@link URL} to be resolved by the KNS.
	 * 
	 * @return the resolved {@link KN} or <code>NULL</code> if it can not be resolved.
	 * 
	 * @throws {@link Exception} if any error occurs during the operation.
	 * @throws {@link ResourceNotResolvedException} if the resource {@link URL} can not be resolved by the given KNSServer.
	 */
	public static KN resolve(URL resourceURL, 
			String format, 
			String version, 
			Resolver resolver) throws Exception {
		return resolve(new CustomParamKNSServerList(), resourceURL, format, version, resolver);
	}
	
	/**
	 * Resolve the given {@link URL} in the available KNS.
	 * The first KNS to be checked is the default KNS, 
	 * thereafter the user's KNS.
	 * 
	 * @param knsServerList list of KNS servers.
	 * @param resourceURL the {@link URL} to be resolved by the KNS.	 
	 * @param format the KB format. 
	 * @param version the KB version.
	 * 
	 * @return the resolved {@link KN} or <code>NULL</code> if it can not be resolved.
	 * 
	 * @throws {@link Exception} if any error occurs during the operation.
	 * @throws {@link ResourceNotResolvedException} if the resource {@link URL} can not be resolved by the given KNSServer.
	 */
	public static KN resolve(CustomParamKNSServerList knsServerList, 
			URL resourceURL, 
			String format, 
			String version) throws Exception {
		KBResolver resolver = new KBResolver();
		return resolve(knsServerList, resourceURL, format, version, resolver);
	}
	
	/**
	 * Resolve the given {@link URL} in the available KNS.
	 * The first KNS to be checked is the default KNS, 
	 * thereafter the user's KNS.
	 * 
	 * @param knsServerList list of KNS servers.
	 * @param resourceURL the {@link URL} to be resolved by the KNS.	 
	 * @param format the KB format. 
	 * @param version the KB version. 
	 * @param resolver the resolver that will resolve the given {@link URL}.
	 * 
	 * @return the resolved {@link KN} or <code>NULL</code> if it can not be resolved.
	 * 
	 * @throws Exception if any error occurs during the operation.
	 */
	public static KN resolve(CustomParamKNSServerList knsServerList, 
			URL resourceURL, 
			String format, 
			String version, 
			Resolver resolver) throws Exception {
		KNSResolverVisitor resolveVisitor = new KNSResolverVisitor(resourceURL, format, version, resolver);
		knsServerList.visit(resolveVisitor);
		KN resolvedKN = resolveVisitor.getResolvedKN();
		return resolvedKN;
	}
	
	/**
	 * Resolve the given {@link URL} in the available KNS.
	 * The first KNS to be checked is the default KNS, 
	 * thereafter the user's KNS.
	 * 
	 * @param knsServerURL the {@link URL}  of the KNS Server.
	 * @param resourceURL the {@link URL}  to be resolved by the KNS.	 
	 * @param format the KB format.
	 * @param resolver the resolver that will resolve the given {@link URL}.
	 * 
	 * @return the resolved {@link KN}  or <code>NULL</code> if it can not be resolved.
	 * 
	 * @throws {@link Exception} if any error occurs during the operation.
	 * @throws {@link ResourceNotResolvedException} if the resource {@link URL} can not be resolved by the given KNSServer.
	 */
	public static KN resolve(URL knsServerURL, 
			URL resourceURL, 
			String format, 
			Resolver resolver) throws ResourceNotResolvedException, Exception {
		KN resolvedKN = resolver.resolve(resourceURL, knsServerURL, format);
		return resolvedKN;
	}
	
	/**
	 * Resolve the given {@link URL} in the available KNS.
	 * The first KNS to be checked is the default KNS, 
	 * thereafter the user's KNS.
	 * 
	 * @param knsServerURL the {@link URL}  of the KNS Server.
	 * @param resourceURL the {@link URL}  to be resolved by the KNS.	 
	 * @param format the KB format. 
	 * @param version the KB version. 
	 * @param resolver the resolver that will resolve the given {@link URL}.
	 * 
	 * @return the resolved {@link KN}  or <code>NULL</code> if it can not be resolved.
	 * 
	 * @throws {@link Exception} if any error occurs during the operation.
	 * @throws {@link ResourceNotResolvedException} if the resource {@link URL} can not be resolved by the given KNSServer.
	 */
	public static KN resolve(URL knsServerURL, 
			URL resourceURL, 
			String format, 
			String version, 
			Resolver resolver) throws ResourceNotResolvedException, Exception {
		KN resolvedKN = resolver.resolve(resourceURL, knsServerURL, format, version);
		return resolvedKN;
	}
	
	/**
	 * Resolve the given {@link URL} in the available KNS.
	 * The first KNS to be checked is the default KNS, 
	 * thereafter the user's KNS.
	 * 
	 * @param knsServerURL the {@link URL} of the KNS Server.
	 * @param resourceURL the {@link URL} to be resolved by the KNS.
	 * @param resolver the resolver that will resolve the given {@link URL}.
	 * 
	 * @return the resolved {@link URL} or <code>NULL</code> if it can not be resolved.
	 * 
	 * @throws {@link Exception} if any error occurs during the operation.
	 * @throws {@link ResourceNotResolvedException} if the resource {@link URL} can not be resolved by the given KNSServer.
	 */
	public static KN resolve(URL knsServerURL, 
			URL resourceURL, 
			Resolver resolver) throws ResourceNotResolvedException, Exception {
		KN resolvedKN = resolver.resolve(knsServerURL, resourceURL);
		return resolvedKN;
	}
	
	public static void assertNotNull(Exception exception, Object... object) throws Exception {
		if(object == null) {
			throw exception;
		}
	}
	
	public static String getValue(String value, String defaultValue) {
		if(value == null) {
			return defaultValue;
		}
		return value;
	}
}
