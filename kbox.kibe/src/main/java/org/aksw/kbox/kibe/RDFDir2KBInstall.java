package org.aksw.kbox.kibe;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import javax.naming.OperationNotSupportedException;

import org.aksw.kbox.InputStreamFactory;

/**
 * 
 * @author {@linkplain http://emarx.org}
 *
 */
public class RDFDir2KBInstall extends RDF2KBInstall {	
	@Override
	public void install(URL[] resources, URL dest, String format, String version, InputStreamFactory isFactory) throws Exception {
		
		super.install(resources, dest, format, version, isFactory);
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