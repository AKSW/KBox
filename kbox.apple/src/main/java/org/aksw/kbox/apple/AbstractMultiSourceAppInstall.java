package org.aksw.kbox.apple;

import java.io.InputStream;
import java.net.URL;

import org.aksw.kbox.InputStreamFactory;
import org.aksw.kbox.utils.URLUtils;

/**
 * 
 * @author {@linkplain http://emarx.org}
 *
 */
public abstract class AbstractMultiSourceAppInstall extends AbstractAppInstall {
	public void install(URL[] resources, URL dest, String format, String version, InputStreamFactory isFactory) throws Exception {
		InputStream[] inputStreams = new InputStream[resources.length];
		int i = 0;
		for(URL resource : resources) {
			URL forwardURL = URLUtils.getURLForward(resource);
			InputStream is = isFactory.get(forwardURL);
			inputStreams[i] = is;
			i++;
		}
		install(inputStreams, dest, format, version);
		for(InputStream is : inputStreams) {
			is.close();
		}
	}
}
