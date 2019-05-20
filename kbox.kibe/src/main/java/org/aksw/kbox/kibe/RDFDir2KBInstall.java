package org.aksw.kbox.kibe;

import java.io.File;
import java.io.InputStream;

import org.aksw.kbox.apple.AbstractCompressedDataInstall;
import org.aksw.kbox.kibe.tdb.TDB;

/**
 * 
 * @author {@linkplain http://emarx.org}
 *
 */
public class RDFDir2KBInstall extends AbstractCompressedDataInstall {	
	@Override
	public void install(InputStream[] sources, File target) throws Exception {
		TDB.bulkload(target.getAbsolutePath(), sources);
	}
	
	@Override
	public void install(InputStream source, File target) throws Exception {
		TDB.bulkload(target.getAbsolutePath(), source);
	}
}