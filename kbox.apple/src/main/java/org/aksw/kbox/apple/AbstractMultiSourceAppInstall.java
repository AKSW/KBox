package org.aksw.kbox.apple;

import java.io.File;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
	
	@Override
	public void install(InputStream[] inputStreams, URL dest, String format, String version) throws Exception {
		List<InputStream> streams = Arrays.asList(inputStreams);
		SequenceInputStream sInputStream = new SequenceInputStream(Collections.enumeration(streams));
		install(sInputStream, dest, format, version);
	}
	
	@Override
	protected void createPath(File destPath) throws Exception {
		destPath.createNewFile();
	}
}
