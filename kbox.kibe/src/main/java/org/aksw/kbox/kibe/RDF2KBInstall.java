package org.aksw.kbox.kibe;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import javax.naming.OperationNotSupportedException;

import org.aksw.kbox.InputStreamFactory;
import org.aksw.kbox.apple.AbstractDirectoryDataInstall;
import org.aksw.kbox.kibe.tdb.TDB;

/**
 * 
 * @author {@linkplain http://emarx.org}
 *
 */
public class RDF2KBInstall extends AbstractDirectoryDataInstall {
	
	@Override
	public void install(URL[] resources, URL dest, String format, String version, InputStreamFactory isFactory) throws Exception {
		File destPath = new File(urlToAbsolutePath(dest, format, version));
		createPath(destPath);
		TDB.bulkload(destPath.getAbsolutePath(), resources);
		register(dest, format, version);
		validate(dest, format, version);
	}
	
	@Override
	public void install(URL resource, URL dest, String format, String version, InputStreamFactory isFactory) throws Exception {
		File destPath = new File(urlToAbsolutePath(dest, format, version));
		createPath(destPath);
		TDB.bulkload(destPath.getAbsolutePath(), resource);
		register(dest, format, version);
		validate(dest, format, version);
	}
	
	@Override
	public void install(InputStream[] sources, File target) throws Exception {
		throw new OperationNotSupportedException();
	}
	
	@Override
	public void install(InputStream source, File target) throws Exception {
		throw new OperationNotSupportedException();
	}
}