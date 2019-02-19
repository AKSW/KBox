package org.aksw.kbox.kns;

import java.io.File;
import java.net.URL;

import org.aksw.kbox.InputStreamFactory;
import org.aksw.kbox.apple.AppLocate;
import org.aksw.kbox.apple.Install;
import org.aksw.kbox.apple.Locate;
import org.aksw.kbox.kns.exception.ResourceDereferencingException;
import org.aksw.kbox.kns.exception.ResourceNotResolvedException;

public class KBox extends org.aksw.kbox.apple.KBox {	

	/**
	 * Install the given {@link URL} in your personal Resource Name Service.
	 * This service will be used to Lookup resources.
	 * 
	 * @param url the {@link URL} of the RNS (Resource Name Service)
	 */
	public static void installRNS(URL url) {
		CustomRNSServerList rnsServerList = new CustomRNSServerList();
		rnsServerList.add(url.toString());
	}
	
	/**
	 * Remove the given {@link URL} from your personal Resource Name Service.
	 * 
	 * @param url the {@link URL} of the RNS (Resource Name Service)
	 */
	public static void removeRNS(URL url) {
		CustomRNSServerList knsServerList = new CustomRNSServerList();
		knsServerList.remove(url.toString());
	}
		
	/**
	 * Resolve the given {@link URL} in the available RNS.
	 * The first RNS to be checked is the default RNS, 
	 * thereafter the user's RNS.
	 * 
	 * @param rnsServerList list of RNS servers
	 * @param resourceName the {@link URL} to be resolved by the RNS.
	 * 
	 * @return the resolved RN or {@link null} if it can not be resolved.
	 * 
	 * @throws {@link Exception} if any error occurs during the operation.
	 */
	public static RN resolve(RNServerList rnsServerList, URL resourceName) throws Exception {
		ResourceResolver resolver = new ResourceResolver();
		return resolve(rnsServerList, resourceName, resolver);
	}
	
	/**
	 * Resolve the given {@link URL} in the available RNS.
	 * The first RNS to be checked is the default RNS, 
	 * thereafter the user's RNS.
	 * 
	 * @param knsServerList list of RNS servers
	 * @param resourceName the {@link URL} to be resolved by the RNS.
	 * @param resolver the resolver to resolve the given resourceName in the {@link CustomRNSServerList}.
	 * 
	 * @return the resolved {@link RN} or {@link null} if it can not be resolved.
	 * 
	 * @throws {@link Exception} if any error occurs during the operation.
	 */
	public static RN resolve(RNServerList knsServerList, 
			URL resourceName, 
			Resolver resolver) throws Exception {
		RNSResolverVisitor resolveVisitor = new RNSResolverVisitor(resourceName, resolver);
		knsServerList.visit(resolveVisitor);
		RN resolvedKN = resolveVisitor.getResolvedKN();
		return resolvedKN;
	}
	
	/**
	 * Resolve the given {@link URL} in the available RNS.
	 * The first RNS to be checked is the default RNS, 
	 * thereafter the user's RNS.
	 * 
	 * @param resourceName the {@link URL} to be resolved by the RNS.	 
	 * @param format the resource format. 
	 * @param version the resource version.
	 * 
	 * @return the resolved {@link RN} or {@link null} if it can not be resolved.
	 * 
	 * @throws {@link Exception} if any error occurs during the operation.
	 * @throws {@link ResourceNotResolvedException} if the resource {@link URL} can not be resolved by the given RNSServer.
	 */
	public static RN resolve(URL resourceName, 
			String format, 
			String version) throws Exception {
		return resolve(new CustomRNSServerList(), resourceName, format, version);
	}
	
	/**
	 * Resolve the given {@link URL} in the available RNS.
	 * The first RNS to be checked is the default RNS, 
	 * thereafter the user's RNS.
	 * 
	 * @param resourceName the {@link URL} to be resolved by the RNS.	 
	 * @param format the resource format. 
	 * @param version the resource version.
 	 * @param resolver the resolver that will resolve the given {@link URL}.
	 * 
	 * @return the resolved {@link RN} or {@link null} if it can not be resolved.
	 * 
	 * @throws {@link Exception} if any error occurs during the operation.
	 * @throws {@link ResourceNotResolvedException} if the resource {@link URL} can not be resolved by the given RNSServer.
	 */
	public static RN resolve(URL resourceName, 
			String format, 
			String version, 
			Resolver resolver) throws Exception {
		return resolve(new CustomRNSServerList(), resourceName, format, version, resolver);
	}
	
	/**
	 * Resolve the given {@link URL} in the available RNS.
	 * The first RNS to be checked is the default RNS, 
	 * thereafter the user's RNS.
	 * 
	 * @param rnsServerList list of RN Services.
	 * @param resourceName the {@link URL} to be resolved by the RNS.	 
	 * @param format the resource format. 
	 * @param version the resource version.
	 * 
	 * @return the resolved {@link RN} or {@link null} if it can not be resolved.
	 * 
	 * @throws {@link Exception} if any error occurs during the operation.
	 * @throws {@link ResourceNotResolvedException} if the resource {@link URL} can not be resolved by the given RNSServer.
	 */
	public static RN resolve(RNServerList rnsServerList, 
			URL resourceName, 
			String format, 
			String version) throws Exception {
		ResourceResolver resolver = new ResourceResolver();
		return resolve(rnsServerList, resourceName, format, version, resolver);
	}
	
	/**
	 * Resolve the given {@link URL} in the available RNS.
	 * The first RNS to be checked is the default RNS, 
	 * thereafter the user's RNS.
	 * 
	 * @param knsServerList list of RN Services.
	 * @param resourceName the {@link URL} to be resolved by the RNS.	 
	 * @param format the resource format. 
	 * @param version the resource version. 
	 * @param resolver the resolver that will resolve the given {@link URL}.
	 * 
	 * @return the resolved {@link RN} or {@link null} if it can not be resolved.
	 * 
	 * @throws Exception if any error occurs during the operation.
	 */
	public static RN resolve(RNServerList knsServerList, 
			URL resourceName, 
			String format, 
			String version, 
			Resolver resolver) throws Exception {
		RNSResolverVisitor resolveVisitor = new RNSResolverVisitor(resourceName, format, version, resolver);
		knsServerList.visit(resolveVisitor);
		RN resolvedKN = resolveVisitor.getResolvedKN();
		return resolvedKN;
	}
	
	/**
	 * Resolve the given {@link URL} in the available RNS.
	 * The first RNS to be checked is the default RNS, 
	 * thereafter the user's RNS.
	 * 
	 * @param rnsServerURL the {@link URL}  of the RNS Server.
	 * @param resourceName the {@link URL}  to be resolved by the RNS.	 
	 * @param format the resource format.
	 * @param resolver the resolver that will resolve the given {@link URL}.
	 * 
	 * @return the resolved {@link RN}  or {@link null} if it can not be resolved.
	 * 
	 * @throws {@link Exception} if any error occurs during the operation.
	 * @throws {@link ResourceNotResolvedException} if the resource {@link URL} can not be resolved by the given RNSServer.
	 */
	public static RN resolve(URL rnsServerURL, 
			URL resourceName, 
			String format, 
			Resolver resolver) throws ResourceNotResolvedException, Exception {
		RN resolvedKN = resolver.resolve(resourceName, rnsServerURL, format);
		return resolvedKN;
	}
	
	/**
	 * Resolve the given {@link URL} in the available RNS.
	 * The first RNS to be checked is the default RNS, 
	 * thereafter the user's RNS.
	 * 
	 * @param rnsServerURL the {@link URL}  of the RNS Server.
	 * @param resourceName the {@link URL}  to be resolved by the RNS.	 
	 * @param format the resource format. 
	 * @param version the resource version. 
	 * @param resolver the resolver that will resolve the given {@link URL}.
	 * 
	 * @return the resolved {@link RN}  or {@link null} if it can not be resolved.
	 * 
	 * @throws {@link Exception} if any error occurs during the operation.
	 * @throws {@link ResourceNotResolvedException} if the resource {@link URL} can not be resolved by the given RNSServer.
	 */
	public static RN resolve(URL rnsServerURL, 
			URL resourceName, 
			String format, 
			String version, 
			Resolver resolver) throws ResourceNotResolvedException, Exception {
		RN resolvedKN = resolver.resolve(resourceName, rnsServerURL, format, version);
		return resolvedKN;
	}
	
	/**
	 * Resolve the given {@link URL} in the available RNS.
	 * The first RNS to be checked is the default RNS, 
	 * thereafter the user's RNS.
	 * 
	 * @param rnsServerURL the {@link URL} of the RNS Server.
	 * @param resourceName the {@link URL} to be resolved by the RNS.
	 * @param resolver the resolver that will resolve the given {@link URL}.
	 * 
	 * @return the resolved {@link URL} or {@link null} if it can not be resolved.
	 * 
	 * @throws {@link Exception} if any error occurs during the operation.
	 * @throws {@link ResourceNotResolvedException} if the resource {@link URL} can not be resolved by the given RNSServer.
	 */
	public static RN resolve(URL rnsServerURL, 
			URL resourceName, 
			Resolver resolver) throws ResourceNotResolvedException, Exception {
		RN resolvedKN = resolver.resolve(rnsServerURL, resourceName);
		return resolvedKN;
	}
	
	/**
	 * Get the resource from the given RNS Server {@link URL} with the given format and version.
	 * 
	 * @param knsServerList the {@link RNServerList} to lookup for the give resource {@link URL}.
	 * @param resourceName the {@link URL} of the resource. 
	 * @param format the resource format.
	 * @param version the resource version.
	 * @param isFactory the {@link InputStreamFactory} to be used to open the resource file stream.
	 * 
	 * ps: This method automatically dereference the resource from the RNS Server in case it 
	 * can not be found locally.
	 * 
	 * @return the resource {@link File} or {@link null} if the resource does not exist or could not be located. 
	 * 
	 * @throws Exception if some error occurs during while getting the resource. 
	 */
	public static File getResource(RNServerList knsServerList, 
			URL resourceName,
			String format,
			String version,
			InputStreamFactory isFactory
			) throws Exception {
		return getResource(knsServerList, resourceName, format, version, isFactory, true);
	}
	
	/**
	 * Get the resource from the given RNS Server {@link URL} with the given format and version.
	 * 
	 * @param knsServerList the {@link RNServerList} to lookup for the give resource {@link URL}.
	 * @param resourceName the {@link URL} of the resource. 
	 * @param format the resource format.
	 * @param version the resource version.
	 * @param isFactory the {@link InputStreamFactory} to be used to open the resource file stream.
	 * @param install true if the resource should be installed and false otherwise.
	 * 
	 * @return the resource {@link File} or {@link null} if the resource does not exist or could not be located. 
	 * 
	 * @throws Exception if some error occurs during while getting the resource. 
	 */
	public static File getResource(RNServerList knsServerList,
			URL resourceName,
			String format,
			String version,
			InputStreamFactory isFactory,
			boolean install
			) throws Exception {
		Locate resourceLocate = new AppLocate();
		InstallFactory installFactory = new DefaultInstallFactory();
		Resolver resourceResolver = new ResourceResolver();
			File resource = getResource(knsServerList,
					resourceName,
					resourceLocate,
					format,
					version,
					resourceResolver,
					installFactory, 
					isFactory,
					install);
			return resource;
	}
	
	/**
	 * Get the resource from the given RNS Server {@link URL} with the given format and version.
	 * 
	 * @param rnsServerURL the RNS Server {@link URL}.
	 * @param resourceName the {@link URL} of the resource. 
	 * @param format the resource format.
	 * @param version the resource version.
	 * @param isFactory the {@link InputStreamFactory} to open the resource file stream.
	 * 
	 * ps: This method automatically dereference the resource from the RNS Server in case it 
	 * can not be found locally.
	 * 
	 * @return the resource {@link File} or null if the resource does not exist or could not be located. 
	 * 
	 * @throws Exception if some error occurs during while getting the resource. 
	 */
	public static File getResource(URL rnsServerURL,
			URL resourceName,
			String format, 
			String version,
			InputStreamFactory isFactory
			) throws Exception {
		return getResource(rnsServerURL, resourceName, format, version, isFactory, true);
	}
	
	/**
	 * Get the resource from the given RNS Server {@link URL} with the given format and version.
	 * 
	 * @param rnsServerURL the RNS Server {@link URL}.
	 * @param resourceName the {@link URL} of the resource. 
	 * @param format the resource format.
	 * @param version the resource version.
	 * @param isFactory the {@link InputStreamFactory} to open the resource file stream.
	 * @param install true if the resource should be installed and false otherwise.
	 * 
	 * @return the resource {@link File} or null if the resource does not exist or could not be located. 
	 * 
	 * @throws Exception if some error occurs during while getting the resource. 
	 */
	public static File getResource(URL rnsServerURL, 
			URL resourceName, 
			String format, 
			String version, 
			InputStreamFactory isFactory,
			boolean install
			) throws Exception {
		final RNService knsServer = new RNService(rnsServerURL);
		RNServerList knsServerList = new RNServerList() {
			@Override
			public boolean visit(RNSServerListVisitor visitor) throws Exception {
				visitor.visit(knsServer);
				return false;
			}
		};
		return getResource(knsServerList, resourceName, format, version, isFactory, install);
	}
	
	public static File getResource(RNServerList knsServerList, 
			URL resourceName, 
			Locate locateMethod,
			String format,
			String version,
			Resolver resolver, 
			Install installMethod,
			InputStreamFactory isFactory) throws Exception {
		return getResource(knsServerList, 
				resourceName, 
				locateMethod,
				format,
				version,
				resolver, 
				installMethod, 
				isFactory, 
				true);
   }
	
   public static File getResource(RNServerList knsServerList, 
			URL resourceName, 
			Locate locateMethod,
			String format,
			String version,
			Resolver resolver,
			Install installMethod,
			InputStreamFactory isFactory,
			boolean install)
			throws Exception {
		File localDataset = locate(resourceName, format, version, locateMethod);
		if(localDataset == null && install) {
			RN resolvedKN = resolve(knsServerList, resourceName, format, version, resolver);
			notNull(new ResourceNotResolvedException(resourceName.toString()), resolvedKN);
			notNull(new ResourceNotResolvedException(resourceName.toString()), resolvedKN.getTargetURL());
			try {
				install(resolvedKN.getTargetURL(), resourceName, format, version, installMethod, isFactory);
			} catch (Exception e) {
				throw new ResourceDereferencingException(resourceName.toString(), e);
			}
			localDataset = locate(resourceName, format, version, locateMethod);
		} else {
			notNull(new ResourceNotResolvedException("Resource " + resourceName.toString() + " is not installed."
					+ " You can install it using the command install."), localDataset);
		}
		return localDataset;
   }
   
   public static File getResource(RNServerList knsServerList, 
			URL resourceName, 
			Locate locateMethod,
			String format,
			String version,
			Resolver resolver,
			InstallFactory installFactory,
			InputStreamFactory isFactory,
			boolean install)
			throws Exception {
		File localDataset = locate(resourceName, format, version, locateMethod);
		if(localDataset == null && install) {
			RN resolvedKN = resolve(knsServerList, resourceName, format, version, resolver);
			notNull(new ResourceNotResolvedException(resourceName.toString()), resolvedKN);
			notNull(new ResourceNotResolvedException(resourceName.toString()), resolvedKN.getTargetURL());
			try {
				Install installMethod = installFactory.get(resolvedKN);
				install(resolvedKN.getTargetURL(), resourceName, format, version, installMethod, isFactory);
			} catch (Exception e) {
				throw new ResourceDereferencingException(resourceName.toString(), e);
			}
			localDataset = locate(resourceName, format, version, locateMethod);	
		} 
		if(localDataset == null) {
			throw new ResourceNotResolvedException("Resource " + resourceName.toString() + " is not installed."
					+ " You can install it using the command install.");
		}
		return localDataset;
	}
   
   /**
	 * Resolve the given {@link URL} in the available RNS.
	 * The first RNS to be checked is the default RNS,
	 * thereafter the user's RNS.
	 * 
	 * @param rnsServerURL the {@link URL} of the RNS Server.
	 * @param resourceName the {@link URL} to be resolved by the RNS.
	 * 
	 * @return the resolved {@link RN} or {@link null} if the given resource's {@link URL} can not be resolved.
	 * 
	 * @throws Exception if any error occurs during the operation.
	 */
	public static RN resolve(URL rnsServerURL, URL resourceName) throws Exception {
		ResourceResolver resolver = new ResourceResolver();
		return resolve(rnsServerURL, resourceName, resolver);
	}
	
	/**
	 * Resolve the given {@link URL} in the available RNS.
	 * The first RNS to be checked is the default RNS,
	 * thereafter the user's RNS.
	 * 
	 * @param rnsServerURL the {@link URL} of the RNS Server.
	 * @param resourceName the {@link URL} to be resolved by the RNS.
	 * @param format the resource format.
	 * 
	 * @return the resolved {@link RN} or {@link null} if the given resource's {@link URL} can not be resolved.
	 * 
	 * @throws Exception if any error occurs during the operation.
	 */
	public static RN resolve(URL rnsServerURL, URL resourceName, String format) throws Exception {
		ResourceResolver resolver = new ResourceResolver();
		return resolve(rnsServerURL, resourceName, format, resolver);
	}
	
	/**
	 * Resolve the given {@link URL} in the available RNS.
	 * The first RNS to be checked is the default RNS,
	 * thereafter the user's RNS.
	 * 
	 * @param rnsServerURL the {@link URL} of the RNS Server.
	 * @param resourceName the {@link URL} to be resolved by the RNS.
	 * @param format the resource format.
	 * @param version the resource version.
	 * 
	 * @return the resolved {@link RN} or {@link null} if the given resource's {@link URL} can not be resolved.
	 * 
	 * @throws Exception if any error occurs during the operation.
	 */
	public static RN resolve(URL rnsServerURL, URL resourceName, String format, String version) throws Exception {
		ResourceResolver resolver = new ResourceResolver();
		return resolve(rnsServerURL, resourceName, format, version, resolver);
	}
	
}
