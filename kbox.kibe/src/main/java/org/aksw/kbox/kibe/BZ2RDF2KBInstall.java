package org.aksw.kbox.kibe;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;

import org.aksw.kbox.apple.AbstractDirectoryDataInstall;
import org.aksw.kbox.kibe.tdb.TDB;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.jena.riot.Lang;

/**
 * 
 * @author {@linkplain http://emarx.org}
 *
 */
public class BZ2RDF2KBInstall extends AbstractDirectoryDataInstall {
	
	Lang lang;
	
	public BZ2RDF2KBInstall(Lang lang) {
		this.lang = lang;
	}
	
	@Override
	public void install(InputStream source, File target) throws Exception {
		try(BufferedInputStream in = new BufferedInputStream(source)) {
			try (BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream(in)) {		    
		        TDB.bulkload(target.getAbsolutePath(), bzIn, lang);
			}
		}
	}
}