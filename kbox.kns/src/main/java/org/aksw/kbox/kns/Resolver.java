package org.aksw.kbox.kns;

import java.io.IOException;
import java.net.URL;

public interface Resolver {	
	/**
	 * Resolve the given {@link URL} in the available KNS.
	 * The first KNS to be checked is the default KNS, 
	 * thereafter the user's KNS.
	 * 
	 * @param knsServerURL the {@link URL} of the KNS Server.
	 * @param resourceURL the {@link URL} to be resolved by the KNS.
	 * 
	 * @return the resolved {@link RN}.
	 * 
	 * @throws IOException if any error occurs during the operation.
	 */
	RN resolve(URL knsServerURL, URL resourceURL) throws Exception;	
	
	/**
	 * Resolve the given {@link URL} in the available KNS.
	 * The first KNS to be checked is the default KNS, 
	 * thereafter the user's KNS.
	 * 
	 * @param knsServerURL the {@link URL} of the KNS Server.
	 * @param resourceURL the {@link URL} to be resolved by the KNS.	 
	 * @param format the KB format.
	 * 
	 * @return the resolved {@link RN}.
	 * 
	 * @throws IOException if any error occurs during the operation.
	 */
	RN resolve(URL knsServerURL, URL resourceURL, String format) throws Exception;
	
	/**
	 * Resolve the given {@link URL} in the available KNS.
	 * The first KNS to be checked is the default KNS, 
	 * thereafter the user's KNS.
	 * 
	 * @param knsServerURL the {@link URL} of the KNS Server.
	 * @param resourceURL the U{@link URL} to be resolved by the KNS.	 
	 * @param format the KB format. 
	 * @param version the KB version.
	 * 
	 * @return the resolved {@link RN}.
	 * 
	 * @throws OException if any error occurs during the operation.
	 */
	RN resolve(URL knsServerURL, URL resourceURL, String format, String version) throws Exception;	
	
}
