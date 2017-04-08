package org.aksw.kbox.kns;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.aksw.kbox.utils.URLUtils;
import org.apache.log4j.Logger;

public class KNSTable {
	
	public final static String FILE_SERVER_TABLE_FILE_NAME = "table.kns";
	
	private final static Logger logger = Logger.getLogger(KNSTable.class);	
	
	private URL tableURL = null;
	private URL knsServerURL = null;
	
	public KNSTable(URL url) throws MalformedURLException {		
		this(url.toString());
	}
	
	public KNSTable(String url) throws MalformedURLException {
		this.knsServerURL = new URL(url);
		this.tableURL = new URL(url + "/" + FILE_SERVER_TABLE_FILE_NAME);		
	}
	
	/**
	 * Resolve a given resource with by the given KNS service.
	 * 
	 * @param resourceURL the URL of the resource that will be resolved by the given KNS service.
	 * 
	 * @return the resolved URL.
	 * 
	 * @throws IOException if any error occurs during the operation.
	 */
	public URL resolveURL(URL resourceURL) throws IOException {
		return resolve(knsServerURL, resourceURL);
	}
	
	/**
	 * Resolve a given resource with by the given KNS service.
	 * 
	 * @param knsServerURL the URL of KNS server that will resolve the given URL.
	 * @param resourceURL the URL of the resource that will be resolved by the given KNS service.
	 * 
	 * @return the resolved URL.
	 * 
	 * @throws IOException if any error occurs during the operation.
	 */
	public static URL resolve(URL knsServerURL, URL resourceURL) throws IOException {
		return resolve(knsServerURL, resourceURL, null, null);
	}
	
	/**
	 * Resolve a given resource with by the given KNS service.
	 * 
	 * @param knsServerURL the URL of KNS server that will resolve the given URL.
	 * @param resourceURL the URL of the resource that will be resolved by the given KNS service.
	 * @param format the KB format. 
	 * @param version the KB version.
	 * 
	 * @return the resolved URL.
	 * 
	 * @throws IOException if any error occurs during the operation.
	 */
	public static URL resolve(URL knsServerURL, URL resourceURL, String format, String version) throws IOException {
		URL tableURL = new URL(knsServerURL.toString() + "/" + FILE_SERVER_TABLE_FILE_NAME);
		try(InputStream is = tableURL.openStream()) {
			try(BufferedReader in = new BufferedReader(new InputStreamReader(is))) {
				String line = null;
				while((line = in.readLine()) != null) {
				    KN kns = null;
					try {
						if(!line.isEmpty()) {
							kns = KN.parse(line);
							if(kns.equals(resourceURL.toString(), format, version)) {
							   URL target = new URL(kns.getTarget());
							   if(!URLUtils.checkStatus(target, 404)) {
								   return target;
							   } else {
								   logger.warn("Invalid KNS target: " + target);
							   }
							}
						}
					} catch (Exception e) {
						logger.error("KNS Table entry could not be parsed: " + line, e);
					}
				}
			}
		}
		return null;
	}
	
	public void visit(KNSVisitor visitor) throws IOException {
		InputStream is = tableURL.openStream();
		try(BufferedReader in = new BufferedReader(new InputStreamReader(is))) {
			String line = null;
			while((line = in.readLine()) != null) {
			    KN kn = null;
				try {
					String url = tableURL.toString();
					url = url.replace("/" + FILE_SERVER_TABLE_FILE_NAME, "");
					kn = KN.parse(line);
					kn.setKNS(url);
					boolean next = visitor.visit(kn);
					if(!next) {
						break;
					}
				} catch (Exception e) {
					logger.error("KNS Table entry could not be parsed: " + line, e);
				}
			}
		}
	}
}
