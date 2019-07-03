package org.aksw.kbox.kibe;

import java.io.File;
import java.io.InputStream;

import org.aksw.kbox.apple.AbstractDirectoryDataInstall;
import org.aksw.kbox.kibe.tdb.TDB;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.jena.riot.Lang;

/**
 * 
 * @author {@linkplain http://emarx.org}
 *
 */
public class CompressedRDF2KBInstall extends AbstractDirectoryDataInstall {

	Lang lang;

	public CompressedRDF2KBInstall(Lang lang) {
		this.lang = lang;
	}

	@Override
	public void install(InputStream source, File target) throws Exception {
		try (ArchiveInputStream i =  new ArchiveStreamFactory()
			    .createArchiveInputStream(source);) {
		    ArchiveEntry entry = null;
		    while ((entry = i.getNextEntry()) != null) {
		        if (!i.canReadEntryData(entry)) {
		            continue;
		        }
	            TDB.bulkload(target.getAbsolutePath(), lang, i);
		    }
		}
	}
}