package org.aksw.kbox.kibe;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.aksw.kbox.kibe.KBox;
import org.aksw.kbox.kibe.tdb.TDBTest;
import org.junit.Test;

import com.hp.hpl.jena.query.ResultSet;

public class KBoxTest {
	
	@Test
	public void testPrintKBs() throws Exception {
		URL serverURL = KBoxTest.class.getResource("/org/aksw/kbox/kibe/");
		KBox.printKB(serverURL);
	}
	
	@Test
	public void testResolveURLWithKBoxKNSService() throws Exception {
		URL db = KBox.resolveURL(new URL("http://dbpedia.org/en/full"));
		assertEquals(db.toString(), "http://vmdbpedia.informatik.uni-leipzig.de:3030/kbox.kb");
	}
	
	@Test
	public void testInstallProcess() throws Exception {
		File indexFile = new File("knowledgebase.idx");
		URL[] filesToIndex = new URL[1];
		URL url = TDBTest.class.getResource("/org/aksw/kbox/kibe/dbpedia_3.9.xml");
		filesToIndex[0] = url;
		KBox.createIndex(indexFile, filesToIndex);
		KBox.installKB(new URL("http://my.knowledgebox"), 
									indexFile.toURI().toURL());
		ResultSet rs = KBox.query(new URL("http://my.knowledgebox"), 
									"Select ?p where {<http://dbpedia.org/ontology/Place> ?p ?o}");
		int i = 0;
		while (rs != null && rs.hasNext()) {
			rs.next();
			i++;
		}
		assertEquals(19, i);
	}
	
	@Test
	public void testQueryInstalledKB() throws Exception {
		ResultSet rs = KBox.query(new URL("http://my.knowledgebox"), 
									"Select ?p where {<http://dbpedia.org/ontology/Place> ?p ?o}");
		int i = 0;
		while (rs != null && rs.hasNext()) {
			rs.next();
			i++;
		}
		assertEquals(19, i);
		
		rs = KBox.query(new URL("http://my.knowledgebox"), 
				"Select ?p where {<http://dbpedia.org/ontology/Place> ?p ?o}");
		i = 0;
		while (rs != null && rs.hasNext()) {
		rs.next();
		i++;
		}
		assertEquals(19, i);
	}
	
	@Test
	public void testQueryNotInstalledKB() throws Exception {
		try {
			@SuppressWarnings("unused")
			ResultSet rs = KBox.query(new URL("http://my.knowledgebox.com"), 
					"Select ?p where {<http://dbpedia.org/ontology/Place> ?p ?o}");
			throw new Exception("The query should have returned an Exception.");
		} catch (Exception e) {			
		}	
	}
	
	@Test
	public void testResolveKNS() throws MalformedURLException, Exception {
		URL serverURL = TDBTest.class.getResource("/org/aksw/kbox/kibe/");
		URL fileURL = KBox.resolveURL(new URL("http://test.org"), serverURL);
		assertEquals(fileURL.toString(), "http://target.org");
	}
	
	@Test
	public void listKNSServers() throws MalformedURLException, Exception {
		Iterable<String> knsList = KBox.listAvailableKNS();
		int i = 0;
		for(@SuppressWarnings("unused") String knsServer : knsList) {
			i++;
		}
		assertEquals(0, i);
	}
	
	@Test
	public void listKNSServers2() throws MalformedURLException, Exception {
		URL serverURL = TDBTest.class.getResource("/org/aksw/kbox/kibe/");
		KBox.installKNS(serverURL);
		Iterable<String> knsList = KBox.listAvailableKNS();
		int i = 0;
		for(String knsServer : knsList) {
			assertEquals(knsServer, serverURL.toString());
			i++;
		}
		assertEquals(1, i);
		KBox.removeKNS(serverURL);
		knsList = KBox.listAvailableKNS();
		i=0;
		for(String knsServer : knsList) {
			assertEquals(knsServer, serverURL.toString());
			i++;
		}
		assertEquals(0, i);
	}
	
	@Test
	public void installKNSServers() throws MalformedURLException, Exception {
		Iterable<String> knsList = KBox.listAvailableKNS();
		int i = 0;
		for(@SuppressWarnings("unused") String knsServer : knsList) {
			i++;
		}
		assertEquals(0, i);
	}
	
}
