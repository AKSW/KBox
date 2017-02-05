package org.aksw.kbox.kibe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.aksw.kbox.kibe.tdb.TDBTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hp.hpl.jena.query.ResultSet;

public class KBoxTest {
	
	@BeforeClass
	public static void setUp() throws Exception {
		File indexFile = File.createTempFile("knowledgebase","idx");
		URL[] filesToIndex = new URL[1];
		URL url = TDBTest.class.getResource("/org/aksw/kbox/kibe/dbpedia_3.9.xml");
		filesToIndex[0] = url;
		KBox.createIndex(indexFile, filesToIndex);
		KBox.installKB(indexFile.toURI().toURL(), new URL("http://dbpedia39"));
		indexFile.deleteOnExit();
		
		indexFile = File.createTempFile("knowledgebase","idx");		
		url = TDBTest.class.getResource("/org/aksw/kbox/kibe/foaf.rdf");
		filesToIndex[0] = url;		
		KBox.createIndex(indexFile, filesToIndex);
		KBox.installKB(indexFile.toURI().toURL(), new URL("http://foaf"));
		indexFile.deleteOnExit();
	}
	
	@Test
	public void testVisitKBs() throws Exception {
		URL serverURL = KBoxTest.class.getResource("/org/aksw/kbox/kibe/");
		MockKNSVisitor visitor = new MockKNSVisitor();
		Main.visitKNS(serverURL, visitor);
		assertEquals(1, visitor.getKNSVisitedList().size());
	}
	
	@Test
	public void testResolveURLWithKBoxKNSService() throws Exception {
		URL db = KBox.resolveURL(new URL("http://dbpedia.org/3.9/en/full"));
		assertEquals(db.toString(), "http://vmdbpedia.informatik.uni-leipzig.de:3031/kbox.kb");
	}
	
	@Test
	public void testNewDir() throws Exception {
		File f = KBox.newDir(new URL("http://dbpedia.org/en/full"));
		assertTrue(f.getAbsolutePath().endsWith("en" + File.separator + "full"));
	}
	
	@Test
	public void testInstallProcess() throws Exception {		
		ResultSet rs = KBox.query("Select ?p where {<http://dbpedia.org/ontology/Place> ?p ?o}",
				new URL("http://dbpedia39"));
		int i = 0;
		while (rs != null && rs.hasNext()) {
			rs.next();
			i++;
		}
		assertEquals(19, i);
	}
	
	@Test
	public void testDescribeQuery() throws Exception {
		ResultSet rs = KBox.query("Describe <http://dbpedia.org/ontology/Place>",
				new URL("http://dbpedia39"));
		int i = 0;
		while (rs != null && rs.hasNext()) {
			rs.next();
			i++;
		}
		assertEquals(19, i);
	}
	
	@Test
	public void testAskQuery() throws Exception {
		ResultSet rs = KBox.query("ASK {  <http://dbpedia.org/ontology/Place> ?p ?o.  " +
			" FILTER(?o = 'test') . }",
			new URL("http://dbpedia39"));
		int i = 0;
		while (rs != null && rs.hasNext()) {
			rs.next();
			i++;
		}
		assertEquals(1, i);
	}
	
	@Test
	public void testQueryInstalledKB() throws Exception {
		ResultSet rs = KBox.query("Select ?p where {<http://dbpedia.org/ontology/Place> ?p ?o}",
				new URL("http://dbpedia39"));
		int i = 0;
		while (rs != null && rs.hasNext()) {
			rs.next();
			i++;
		}
		assertEquals(19, i);
		
		rs = KBox.query( 
				"Select ?p where {<http://dbpedia.org/ontology/Place> ?p ?o}",
				new URL("http://dbpedia39"));
		i = 0;
		while (rs != null && rs.hasNext()) {
		rs.next();
		i++;
		}
		assertEquals(19, i);
	}
	
	@Test(expected=Exception.class)
	public void testQueryNotInstalledKB() throws Exception {	
			@SuppressWarnings("unused")
			ResultSet rs = KBox.query(
					"Select ?p where {<http://dbpedia.org/ontology/Place> ?p ?o}",
					new URL("http://dbpedia39.o"));
		Assert.fail("The query should have returned an Exception.");
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
