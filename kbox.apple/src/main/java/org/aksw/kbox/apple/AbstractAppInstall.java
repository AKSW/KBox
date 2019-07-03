package org.aksw.kbox.apple;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.InvalidParameterException;

import org.aksw.kbox.InputStreamFactory;
import org.aksw.kbox.apple.stream.DefaultInputStreamFactory;
import org.aksw.kbox.utils.URLUtils;
import org.apache.commons.compress.utils.IOUtils;

/**
 * 
 * @author {@linkplain http://emarx.org}
 *
 */
public abstract class AbstractAppInstall extends AppPathBinder implements AppInstall, PathBinder {
	
	public void install(URL[] resources, URL dest, String format, String version)
			throws Exception {		
		install(resources, dest, format, version, new DefaultInputStreamFactory());
	}

	@Override
	public void install(URL resource, URL dest, String format, String version)
			throws Exception {		
		install(resource, dest, format, version, new DefaultInputStreamFactory());
	}

	@Override
	public void install(URL resource, URL dest, String format, String version, InputStreamFactory isFactory) throws Exception {
		URL forwardURL = URLUtils.getURLForward(resource);
		try(InputStream is = isFactory.get(forwardURL)) {
			install(is, dest, format, version);
		}
	}
	
	public void install(URL[] resources, URL dest, String format, String version, InputStreamFactory isFactory) throws Exception {
		for(URL resource : resources) {
			install(resource, dest, format, version, isFactory);
		}
	}
	
	public void install(URL source, File dest, InputStreamFactory isFactory) throws Exception {
		try(FileOutputStream out = new FileOutputStream(dest)) {
			install(source, out, isFactory);
		}
	}
	
	public void install(URL source, OutputStream dest, InputStreamFactory isFactory) throws Exception {
		URL forwardURL = URLUtils.getURLForward(source);
		try(InputStream is = isFactory.get(forwardURL)) {
			install(is, dest);
		}
	}
	
	public void install(URL[] source, OutputStream dest, InputStreamFactory isFactory) throws Exception {
		for(URL resource : source) {
			install(resource, dest, isFactory);
		}
	}
	
	public void install(InputStream is, OutputStream dest) throws Exception {
		IOUtils.copy(is, dest);
	}

	public void install(URL[] sources, File dest, InputStreamFactory isFactory) throws Exception {
		for(URL resource : sources) {
			install(resource, dest, isFactory);
		}
	}
	
	@Override
	public void install(InputStream inputStream, URL dest, String format,
			String version) throws Exception {
		File destPath = new File(urlToAbsolutePath(dest, format, version));
		createPath(destPath);
		install(inputStream, destPath);
		register(dest, format, version);
		validate(dest, format, version);
	}
	
	public void install(InputStream[] inputStreams, URL dest, String format,
			String version) throws Exception {
		File destPath = new File(urlToAbsolutePath(dest, format, version));
		createPath(destPath);
		install(inputStreams, destPath);
		register(dest, format, version);
		validate(dest, format, version);
	}
	
	protected void createPath(File destPath) throws Exception {
		if(destPath.isDirectory()) {
			throw new InvalidParameterException("The parameter destPath should be a file.");
		}
		File parentDir = new File(destPath.getParent());
		if(parentDir.mkdirs() && (!destPath.exists() && !destPath.createNewFile())) {
			throw new Exception("File cannot be created. Provided path might be invalid.");
		}
	}

	public abstract void install(InputStream source, File target) throws Exception;
	
	public void install(InputStream[] sources, File target) throws Exception {
		for(InputStream is : sources) {
			install(is, target);
		}
	}
	
	public void register(URL path, String format, String version) throws Exception {
		File destFile = new File(urlToAbsolutePath(path, format, version));
		KBox.register(path, destFile, format, version);
	}

	@Override
	public void validate(URL path, String format, String version) throws Exception {
		File destFile = new File(urlToAbsolutePath(path, format, version));
		KBox.validate(destFile);
	}
}
