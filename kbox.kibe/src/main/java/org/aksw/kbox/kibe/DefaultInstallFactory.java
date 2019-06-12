package org.aksw.kbox.kibe;

import org.apache.jena.riot.Lang;

public class DefaultInstallFactory extends org.aksw.kbox.kns.DefaultInstallFactory {

	public DefaultInstallFactory() {
		put("rdf2kb", new RDF2KBInstall());
		put("bz2xmlrdf2kb", new BZ2RDF2KBInstall(Lang.RDFXML));
		put("zipxmlrdf2kb", new CompressedRDF2KBInstall(Lang.RDFXML));
		put("bz2jsonld2kb", new BZ2RDF2KBInstall(Lang.JSONLD));
		put("zipjsonld2kb", new CompressedRDF2KBInstall(Lang.JSONLD));
		put("bz2nq2kb", new BZ2RDF2KBInstall(Lang.NQ));
		put("zipnqf2kb", new CompressedRDF2KBInstall(Lang.NQ));
		put("bz2ttl2kb", new BZ2RDF2KBInstall(Lang.TTL));
		put("zipttl2kb", new CompressedRDF2KBInstall(Lang.TTL));
		put("bz2nt2kb", new BZ2RDF2KBInstall(Lang.NT));
		put("zipnt2kb", new CompressedRDF2KBInstall(Lang.NT));
	}

}