package org.aksw.kbox.kibe;

import org.aksw.kbox.apple.GZipAppInstall;
import org.aksw.kbox.apple.ResourceAppInstall;
import org.aksw.kbox.apple.ZipAppInstall;
import org.aksw.kbox.kns.InstallFactory;

public class DefaultInstallFactory extends InstallFactory {

	public DefaultInstallFactory() {
		put("kb", new ZipAppInstall());
		put("gzip", new GZipAppInstall());
		put("zip", new ZipAppInstall());
		put("plain", new ResourceAppInstall());
		put("rdf2kb", new RDF2KBInstall());
		put("bz2rdf2kb", new RDF2KBInstall());
	}

}