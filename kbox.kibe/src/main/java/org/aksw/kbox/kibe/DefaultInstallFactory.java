package org.aksw.kbox.kibe;

import org.apache.jena.riot.Lang;

public class DefaultInstallFactory extends org.aksw.kbox.kns.DefaultInstallFactory {
	
	public final static String RDF2KB = "rdf2kb";
	//BZ2
	public final static String BZ2XMLRDF2KB = "bz2xmlrdf2kb";
	public final static String BZ2JSONLDRDF2KB = "bz2jsonld2kb";
	public final static String BZ2NQRDF2KB = "bz2nq2kb";
	public final static String BZ2TTLRDF2KB = "bz2ttl2kb";
	public final static String BZ2NTRDF2KB = "bz2nt2kb";
	//ZIP
	public final static String ZIPXMLRDF2KB = "zipxmlrdf2kb";
	public final static String ZIPJSONLDRDF2KB = "zipjsonld2kb";
	public final static String ZIPNQRDF2KB = "zipnq2kb";
	public final static String ZIPTTLRDF2KB = "zipttl2kb";
	public final static String ZIPNTRDF2KB = "zipnt2kb";

	public DefaultInstallFactory() {
		put(RDF2KB, new RDF2KBInstall());
		put(BZ2XMLRDF2KB, new BZ2RDF2KBInstall(Lang.RDFXML));
		put("zipxmlrdf2kb", new CompressedRDF2KBInstall(Lang.RDFXML));
		put(BZ2JSONLDRDF2KB, new BZ2RDF2KBInstall(Lang.JSONLD));
		put("zipjsonld2kb", new CompressedRDF2KBInstall(Lang.JSONLD));
		put(BZ2NQRDF2KB, new BZ2RDF2KBInstall(Lang.NQ));
		put("zipnqf2kb", new CompressedRDF2KBInstall(Lang.NQ));
		put(BZ2TTLRDF2KB, new BZ2RDF2KBInstall(Lang.TTL));
		put("zipttl2kb", new CompressedRDF2KBInstall(Lang.TTL));
		put(BZ2NTRDF2KB, new BZ2RDF2KBInstall(Lang.NT));
		put("zipnt2kb", new CompressedRDF2KBInstall(Lang.NT));
	}

}