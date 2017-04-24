package org.aksw.kbox.kns;

import java.io.File;
import java.net.URL;

import org.aksw.kbox.InputStreamFactory;
import org.aksw.kbox.apple.Install;
import org.aksw.kbox.apple.Locate;
import org.askw.kbox.kns.exception.ResourceDereferencingException;
import org.askw.kbox.kns.exception.ResourceNotResolvedException;

public class KBox extends org.aksw.kbox.apple.KBox {	

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
	 * @return the resolved KN or {@link null} if it can not be resolved.
	 * 
	 * @throws {@link Exception} if any error occurs during the operation.
	 */
	public static KN resolve(KNSServerList knsServerList, URL resourceURL) throws Exception {
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
	 * @return the resolved {@link KN} or {@link null} if it can not be resolved.
	 * 
	 * @throws {@link Exception} if any error occurs during the operation.
	 */
	public static KN resolve(KNSServerList knsServerList, 
			URL resourceURL, 
			Resolver resolver) throws Exception {
		KNSResolverVisitor resolveVisitor = new KNSResolverVisitor(resourceURL, resolver);
		knsServerList.visit(resolveVisitor);
		KN resolvedKN = resolveVisitor.getResolvedKN();
		return resolvedKN;
	}
	
	/**
	 * Resolve the given {@link URL} in the available KNS.
	 * The first KNS to be checked is the default KNS, 
	 * thereafter the user's KNS.
	 * 
	 * @param knsServerList list of KNS servers.
	 * @param resourceURL the {@link URL} to be resolved by the KNS.
	 * 
	 * @return the resolved {@link KN} or {@link null} if it can not be resolved.
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
	 * Resolve the given {@link URL} in the available KNS.
	 * The first KNS to be checked is the default KNS, 
	 * thereafter the user's KNS.
	 * 
	 * @param knsServerList list of KNS servers.
	 * @param resourceURL the {@link URL} to be resolved by the KNS.
	 * 
	 * @return the resolved {@link KN} or {@link null} if it can not be resolved.
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
	 * @return the resolved {@link KN} or {@link null} if it can not be resolved.
	 * 
	 * @throws {@link Exception} if any error occurs during the operation.
	 * @throws {@link ResourceNotResolvedException} if the resource {@link URL} can not be resolved by the given KNSServer.
	 */
	public static KN resolve(KNSServerList knsServerList, 
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
	 * @return the resolved {@link KN} or {@link null} if it can not be resolved.
	 * 
	 * @throws Exception if any error occurs during the operation.
	 */
	public static KN resolve(KNSServerList knsServerList, 
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
	 * @return the resolved {@link KN}  or {@link null} if it can not be resolved.
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
	 * @return the resolved {@link KN}  or {@link null} if it can not be resolved.
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
	 * @return the resolved {@link URL} or {@link null} if it can not be resolved.
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
	
	public static File getResource(KNSServerList knsServerList, 
			URL resourceURL, 
			Locate locateMethod,
			String format,
			String version,
			Resolver resolver, 
			Install installMethod,
			InputStreamFactory isFactory) throws Exception {
		return getResource(knsServerList, 
				resourceURL, 
				locateMethod,
				format,
				version,
				resolver, 
				installMethod, 
				isFactory, 
				true);
   }
	
	public static File getResource(KNSServerList knsServerList, 
			URL resourceURL, 
			Locate locateMethod,
			String format,
			String version,
			Resolver resolver,
			Install installMethod,
			InputStreamFactory isFactory,
			boolean install)
			throws Exception {
		File localDataset = locate(resourceURL, format, version, locateMethod);
		if(localDataset == null && install) {
			KN resolvedKN = resolve(knsServerList, resourceURL, format, version, resolver);
			assertNotNull(new ResourceNotResolvedException(resourceURL.toString()), resolvedKN.getTargetURL());
			try {
				install(resolvedKN.getTargetURL(), resourceURL, format, version, installMethod, isFactory);
			} catch (Exception e) {
				throw new ResourceDereferencingException(resourceURL.toString(), e);
			}
			localDataset = locate(resourceURL, format, version, locateMethod);
		} else {
			assertNotNull(new ResourceNotResolvedException("Resource " + resourceURL.toString() + " is not installed."
					+ " You can install it using the command install."), localDataset);
		}
		return localDataset;
	}
	
}
