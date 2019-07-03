package org.aksw.kbox.kibe;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.aksw.kbox.InputStreamFactory;
import org.aksw.kbox.apple.AbstractDirectoryDataInstall;
import org.aksw.kbox.kibe.tdb.TDB;
import org.aksw.kbox.utils.URLUtils;
import org.apache.jena.atlas.web.ContentType;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;

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
		install(resources, destPath, isFactory);
		register(dest, format, version);
		validate(dest, format, version);
	}

	@Override
	public void install(URL resource, URL dest, String format, String version, InputStreamFactory isFactory) throws Exception {
		File destPath = new File(urlToAbsolutePath(dest, format, version));
		createPath(destPath);
		install(resource, destPath, isFactory);
		register(dest, format, version);
		validate(dest, format, version);
	}

	@Override
	public void install(InputStream[] sources, File target) throws Exception {
		TDB.bulkload(target.getAbsolutePath(), sources);
	}

	@Override
	public void install(InputStream source, File target) throws Exception {
		TDB.bulkload(target.getAbsolutePath(), source);
	}

	@Override
	public void install(URL resource, File target, InputStreamFactory isFactory) throws Exception {
		Lang lang = RDFLanguages.filenameToLang(resource.toString());
		if(lang == null) {
			lang = RDFLanguages.contentTypeToLang(ContentType.create(URLUtils.getContentType(resource)));
		}
		try(InputStream is = isFactory.get(resource)) {
			TDB.bulkload(target.getAbsolutePath(), lang, is);
		}
	}
}