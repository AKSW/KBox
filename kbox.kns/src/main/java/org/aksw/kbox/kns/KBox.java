package org.aksw.kbox.kns;

import java.io.File;
import java.net.URL;

import org.aksw.kbox.InputStreamFactory;
import org.aksw.kbox.apple.AppInstall;
import org.aksw.kbox.apple.AppLocate;
import org.aksw.kbox.apple.Locate;
import org.aksw.kbox.kns.exception.ResourceDereferencingException;
import org.aksw.kbox.kns.exception.ResourceNotResolvedException;

/**
 * 
 * @author http://emarx.org
 * 
 * @TODO all getResource methods should be mutual dependent 
 * @TODO the method install should also implement locate
 * 
 */
public class KBox extends org.aksw.kbox.apple.KBox {	

	/**
	 * Install the given {@link URL} in your personal Resource Name Service.
	 * This service will be used to Lookup resources.
	 * 
	 * @param url the {@link URL} of the RNS (Resource Name Service)
	 */
	public static void installKNS(URL url) {
		CustomKNSServerList rnsServerList = new CustomKNSServerList();
		rnsServerList.add(url.toString());
	}
	
	/**
	 * Remove the given {@link URL} from your personal Resource Name Service.
	 * 
	 * @param url the {@link URL} of the RNS (Resource Name Service)
	 */
	public static void removeKNS(URL url) {
		CustomKNSServerList knsServerList = new CustomKNSServerList();
		knsServerList.remove(url.toString());
	}
	
	/**
	 * Resolve the given {@link URL} in the available RNS.
	 * The first RNS to be checked is the default RNS, 
	 * thereafter the user's RNS.
	 * 
	 * @param knsServerList list of RNS servers
	 * @param resourceName the {@link URL} to be resolved by the RNS.
	 * 
	 * @return the resolved {@link KN} or {@link null} if it can not be resolved.
	 * 
	 * @throws {@link Exception} if any error occurs during the operation.
	 */
	public static KN resolve(KNSServerList knsServerList, 
			URL resourceName) throws Exception {
		return resolve(knsServerList, resourceName, null, null);
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
	 * @return the resolved {@link KN} or {@link null} if it can not be resolved.
	 * 
	 * @throws {@link Exception} if any error occurs during the operation.
	 */
	public static KN resolve(URL resourceName, 
			String format, 
			String version) throws Exception {
		return resolve(new CustomKNSServerList(), resourceName, format, version);
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
	 * 
	 * @return the resolved {@link KN} or {@link null} if it can not be resolved.
	 * 
	 * @throws {@link Exception} if any error occurs during the operation.
	 */
	public static KN resolve(KNSServerList knsServerList, 
			URL resourceName, 
			String format, 
			String version) throws Exception {
		KNSServerListResolverVisitor resolveVisitor = new KNSServerListResolverVisitor(resourceName, format, version);
		knsServerList.visit(resolveVisitor);
		return resolveVisitor.getResolvedKN();
	}
	
	/**
	 * Resolve the given {@link URL} in the available RNS.
	 * The first RNS to be checked is the default RNS, 
	 * thereafter the user's RNS.
	 * 
	 * @param rnsServerURL the {@link URL} of the RNS Server.
	 * @param resourceName the {@link URL} to be resolved by the RNS.
	 * 
	 * @return the resolved {@link URL} or {@link null} if it can not be resolved.
	 * 
	 * @throws {@link Exception} if any error occurs during the operation.
	 */
	public static KN resolve(URL knsServerURL, 
			URL resourceName) throws Exception {
		return resolve(knsServerURL, resourceName, null, null);
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
	 * @return the resolved {@link KN}  or {@link null} if it can not be resolved.
	 * 
	 * @throws {@link Exception} if any error occurs during the operation.
	 */
	public static KN resolve(URL rnsServerURL, 
			URL resourceName, 
			String format) throws Exception {
		return resolve(rnsServerURL, resourceName, format, null);
	}
	
	/**
	 * Resolve the given {@link URL} in the available RNS.
	 * The first RNS to be checked is the default RNS, 
	 * thereafter the user's RNS.
	 * 
	 * @param knsServerURL the {@link URL}  of the RNS Server.
	 * @param resourceURL the {@link URL}  to be resolved by the RNS.	 
	 * @param format the resource format. 
	 * @param version the resource version. 
	 * @param resolver the resolver that will resolve the given {@link URL}.
	 * 
	 * @return the resolved {@link KN}  or {@link null} if it can not be resolved.
	 * 
	 * @throws {@link Exception} if any error occurs during the operation.
	 */
	public static KN resolve(URL knsServerURL, 
			URL resourceURL, 
			String format, 
			String version) throws Exception {
		KNSServer knsServer = new KBoxKNSServer(knsServerURL);
		KNSResolverVisitor resolver = new KNSResolverVisitor(resourceURL, format, version);
		knsServer.visit(resolver);
		return resolver.getResolvedKN();
	}
	
	/**
	 * Get the resource from the given RNS Server {@link URL} with the given format and version.
	 * 
	 * @param knsServerList the {@link KNSServerList} to lookup for the give resource {@link URL}.
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
	public static File getResource(KNSServerList knsServerList, 
			URL resourceName,
			String format,
			String version,
			InputStreamFactory isFactory
			) throws Exception {
		return getResource(knsServerList, resourceName, format, version, isFactory, true);
	}
	
	/**
	 * Get the resource from the given KNS Server {@link URL} with the given format and version.
	 * 
	 * @param knsServerList the {@link KNSServerList} to lookup for the give resource {@link URL}.
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
	public static File getResource(KNSServerList knsServerList,
			URL resourceName,
			String format,
			String version,
			InputStreamFactory isFactory,
			boolean install
			) throws Exception {
		InstallFactory installFactory = new DefaultInstallFactory();
			File resource = getResource(knsServerList,
					resourceName,
					format,
					version,
					isFactory,
					installFactory,
					install);
			return resource;
	}
	
	/**
	 * Get the resource from the given RNS Server {@link URL} with the given format and version.
	 * 
	 * @param knsServerList the {@link KNSServerList} to lookup for the give resource {@link URL}.
	 * @param resourceName the {@link URL} of the resource. 
	 * @param format the resource format.
	 * @param version the resource version.
	 * @param isFactory the {@link InputStreamFactory} to be used to open the resource file stream.
	 * @param installFactory the {@link InstallFactory} to be used to lookup the install method.
	 * @param install true if the resource should be installed and false otherwise.
	 * 
	 * @return the resource {@link File} or {@link null} if the resource does not exist or could not be located. 
	 * 
	 * @throws Exception if some error occurs during while getting the resource. 
	 */
	public static File getResource(KNSServerList knsServerList,
			URL resourceName,
			String format,
			String version,
			InputStreamFactory isFactory,
			InstallFactory installFactory,
			boolean install
			) throws Exception {
		Locate resourceLocate = new AppLocate();
		File resource = getResource(knsServerList,
					resourceName,
					resourceLocate,
					format,
					version,
					installFactory, 
					isFactory,
					install);
		return resource;
	}
	
	/**
	 * Get the resource from the given KNS Server {@link URL} with the given format and version.
	 * 
	 * @param knsServerURL the RNS Server {@link URL}.
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
	public static File getResource(URL knsServerURL,
			URL resourceName,
			String format, 
			String version,
			InputStreamFactory isFactory
			) throws Exception {
		return getResource(knsServerURL, resourceName, format, version, isFactory, true);
	}
	
	/**
	 * Get the resource from the given RNS Server {@link URL} with the given format and version.
	 * 
	 * @param knsServerURL the KNS Server {@link URL}.
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
	public static File getResource(URL knsServerURL, 
			URL resourceName, 
			String format, 
			String version, 
			InputStreamFactory isFactory,
			boolean install
			) throws Exception {
		final KNSServer knsServer = new KBoxKNSServer(knsServerURL);
		KNSServerList knsServerList = new KNSServerList() {
			@Override
			public boolean visit(KNSServerListVisitor visitor) throws Exception {
				visitor.visit(knsServer);
				return false;
			}
		};
		return getResource(knsServerList, resourceName, format, version, isFactory, install);
	}
	
	/**
	 * Get the resource from the given RNS Server {@link URL} with the given format and version.
	 * 
	 * @param knsServerURL the KNS Server {@link URL}.
	 * @param resourceName the {@link URL} of the resource. 
	 * @param format the resource format.
	 * @param version the resource version.
	 * @param isFactory the {@link InputStreamFactory} to open the resource file stream.
	 * @param installFactory the {@link InstallFactory} to instantiate the install method.
	 * @param install true if the resource should be installed and false otherwise.
	 * 
	 * @return the resource {@link File} or null if the resource does not exist or could not be located. 
	 * 
	 * @throws Exception if some error occurs during while getting the resource. 
	 */
	public static File getResource(URL knsServerURL, 
			URL resourceName, 
			String format, 
			String version, 
			InputStreamFactory isFactory,
			InstallFactory installFactory,
			boolean install
			) throws Exception {
		final KNSServer knsServer = new KBoxKNSServer(knsServerURL);
		KNSServerList knsServerList = new KNSServerList() {
			@Override
			public boolean visit(KNSServerListVisitor visitor) throws Exception {
				visitor.visit(knsServer);
				return false;
			}
		};
		return getResource(knsServerList, resourceName, format, version, isFactory, installFactory, install);
	}
	
	public static File getResource(KNSServerList knsServerList, 
			URL resourceName, 
			Locate locateMethod,
			String format,
			String version,
			AppInstall installMethod,
			InputStreamFactory isFactory) throws Exception {
		return getResource(knsServerList, 
				resourceName, 
				locateMethod,
				format,
				version,
				installMethod, 
				isFactory, 
				true);
   }
	
   public static File getResource(KNSServerList knsServerList, 
			URL resourceName, 
			Locate locateMethod,
			String format,
			String version,
			AppInstall installMethod,
			InputStreamFactory isFactory,
			boolean install)
			throws Exception {
		File localDataset = locate(resourceName, format, version, locateMethod);
		if(localDataset == null && install) {
			KN resolvedKN = resolve(knsServerList, resourceName, format, version);
			notNull(new ResourceNotResolvedException(resourceName.toString()), resolvedKN);
			notNull(new ResourceNotResolvedException(resourceName.toString()), resolvedKN.getTargets().get(0).getURL());
			URL resourceNameURL = new URL(resolvedKN.getName());
			try {
				install(resolvedKN.getTargets().get(0).getURL(), resourceNameURL, format, version, installMethod, isFactory);
			} catch (Exception e) {
				throw new ResourceDereferencingException(resourceName.toString(), e);
			}
			localDataset = locate(resourceNameURL, format, version, locateMethod);
		}
		return localDataset;
   }
   
   public static File getResource(URL knsServerURL, 
			URL resourceName, 
			Locate locateMethod,
			String format,
			String version,
			AppInstall installMethod,
			InputStreamFactory isFactory,
			boolean install)
			throws Exception {
		File localDataset = locate(resourceName, format, version, locateMethod);
		if(localDataset == null && install) {
			KN resolvedKN = resolve(knsServerURL, resourceName, format, version);
			notNull(new ResourceNotResolvedException(resourceName.toString()), resolvedKN);
			notNull(new ResourceNotResolvedException(resourceName.toString()), resolvedKN.getTargets().get(0).getURL());
			URL resourceNameURL = new URL(resolvedKN.getName());
			try {
				install(resolvedKN.getTargets().get(0).getURL(), resourceNameURL, format, version, installMethod, isFactory);
			} catch (Exception e) {
				throw new ResourceDereferencingException(resourceName.toString(), e);
			}
			localDataset = locate(resourceNameURL, format, version, locateMethod);
		}
		return localDataset;
  }
   
   public static File getResource(KNSServerList knsServerList, 
			URL resourceName, 
			Locate locateMethod,
			String format,
			String version,
			InstallFactory installFactory,
			InputStreamFactory isFactory,
			boolean install)
			throws Exception {
	   KN resolvedKN = resolve(knsServerList, resourceName, format, version);
	   notNull(new ResourceNotResolvedException(resourceName.toString()), resolvedKN);
	   notNull(new ResourceNotResolvedException(resourceName.toString()), resolvedKN.getTargets().get(0).getURL());
	   URL resourceNameURL = new URL(resolvedKN.getName());
	   File localDataset = locate(resourceNameURL, format, version, locateMethod);
	   if(localDataset == null && install) {
			try {
				AppInstall installMethod = installFactory.get(resolvedKN);
				install(resolvedKN.getTargets().get(0).getURL(), resourceNameURL, format, version, installMethod, isFactory);
			} catch (Exception e) {
				throw new ResourceDereferencingException(resourceName.toString(), e);
			}
			localDataset = locate(resourceNameURL, format, version, locateMethod);
	   }
	   return localDataset;
   }
   
   public static File getResource(URL knsServerURL, 
			URL resourceName, 
			Locate locateMethod,
			String format,
			String version,
			InstallFactory installFactory,
			InputStreamFactory isFactory,
			boolean install)
			throws Exception {
		File localDataset = locate(resourceName, format, version, locateMethod);
		if(localDataset == null && install) {
			KN resolvedKN = resolve(knsServerURL, resourceName, format, version);
			notNull(new ResourceNotResolvedException(resourceName.toString()), resolvedKN);
			notNull(new ResourceNotResolvedException(resourceName.toString()), resolvedKN.getTargets().get(0).getURL());
			URL resourceNameURL = new URL(resolvedKN.getName());
			try {
				AppInstall installMethod = installFactory.get(resolvedKN);
				install(resolvedKN.getTargets().get(0).getURL(), resourceNameURL, format, version, installMethod, isFactory);
			} catch (Exception e) {
				throw new ResourceDereferencingException(resourceName.toString(), e);
			}
			localDataset = locate(resourceNameURL, format, version, locateMethod);	
		}
		return localDataset;
	}
	
   /**
	 * Creates a mirror for the given file in a given {@link URL}. This function allows
	 * KBox to serve files to applications, acting as proxy to the mirrored
	 * file. The file that is published in a give {@link URL} will be located when the
	 * client execute the function {@link KBox#getResource(URL)}.
	 * 
	 * @param rn the dynamic data name {@link URL} that will be resolved.
	 * @param target the target {@link URL} where the resource will be installed.
	 * @param installFactory an {@link InstallFactory} method to be used for installation.
	 * @param resolver the resolver of the given KNS and resource's {@link URL}.
	 * @param isFactory a {@link InputStreamFactory} to be used to create a stream connection with the resolved resource's {@link URL}.
	 * 
	 * @throws NullArgumentException if any of the arguments is {@link null}.
	 * @throws Exception if the resource does not exist or can not be copied or some
	 *             error occurs during the resource publication.
	 */
	public static void install(KN kn, 
			URL target,
			InstallFactory installFactory,
			InputStreamFactory isFactory)
			throws Exception {		
		notNull(new IllegalArgumentException("kn"), kn);
		notNull(new IllegalArgumentException("target"), target);
		notNull(new IllegalArgumentException("installFactory"), installFactory);
		notNull(new IllegalArgumentException("isFactory"), isFactory);
		AppInstall install = installFactory.get(kn);
		install(kn.getTargets().get(0).getURL(),
				target,
				kn.getFormat(),
				kn.getVersion(),
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
	 * @param resolver the resolver to resolve the given resource's {@link URL} in the {@link DefaultKNSServerList}.
	 * 
	 * @return the resolved {@link  KN} or {@link null} if it is not resolved.
	 * 
	 * @throws Exception if any error occurs during the operation.
	 */
	public static void install(KNSServerList knsServerList, 
			URL resourceURL, 
			String format, 
			String version,
			InstallFactory installFactory, 
			InputStreamFactory isFactory) throws ResourceNotResolvedException, Exception {
		KN resolvedKN = resolve(knsServerList, resourceURL, format, version);
		notNull(new ResourceNotResolvedException(resourceURL.toString()), resolvedKN);
		install(resolvedKN, 
				resourceURL,
				installFactory,
				isFactory);
	}
   
}
