package org.aksw.kbox.kibe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.aksw.kbox.kibe.tdb.TDBTest;
import org.aksw.kbox.kns.KBResolver;
import org.aksw.kbox.kns.KN;
import org.aksw.kbox.kns.KNSSever;
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
		KBox.install(indexFile.toURI().toURL(), new URL("http://dbpedia39"));
		indexFile.deleteOnExit();
		
		KBox.install(indexFile.toURI().toURL(), new URL("http://dbpedia.org/3.9"));
		indexFile.deleteOnExit();
		
		indexFile = File.createTempFile("knowledgebase","idx");
		url = TDBTest.class.getResource("/org/aksw/kbox/kibe/foaf.rdf");
		filesToIndex[0] = url;		
		KBox.createIndex(indexFile, filesToIndex);
		KBox.install(indexFile.toURI().toURL(), new URL("http://foaf"));
		indexFile.deleteOnExit();
	}
	
	@Test
	public void testVisitKBs() throws Exception {
		URL serverURL = KBoxTest.class.getResource("/org/aksw/kbox/kibe/");
		MockKNSVisitor visitor = new MockKNSVisitor();
		KNSSever.visit(serverURL, visitor);
		assertEquals(1, visitor.getKNSVisitedList().size());
	}
	
	@Test
	public void testKNEquals() throws Exception {
		KN kn = new KN("teste", "a", "b", null, null, null, null);
		assertTrue("teste".equals(kn.getName()));
	}
	
	@Test
	public void testKNEqualsWithFormat() throws Exception {
		KN kn = new KN("teste", "a", "b", "c", null, null, null);
		assertTrue(kn.equals("teste","b", "c"));
	}
	
	@Test
	public void testKNEqualsWithFormatWithNull1() throws Exception {
		KN kn = new KN("teste", "a", "b", "d", null, null, null);
		Assert.assertFalse(kn.equals("teste","b", "e"));
	}
	
	@Test
	public void testKNEqualsWithFormatWithNull2() throws Exception {
		KN kn = new KN("teste", "a", "b", "c", null, null, null);
		assertTrue(kn.equals("teste","b", null));
	}
	
	@Test
	public void testKNEqualsWithFormatWithNull3() throws Exception {
		KN kn = new KN("teste", "a", "b", "c", null, null, null);
		Assert.assertFalse(kn.equals("teste", "b", "d"));
	}
	
	@Test
	public void testResolveURLWithKBoxKNSService() throws Exception {
		URL serverURL = KBoxTest.class.getResource("/org/aksw/kbox/kibe/");
		KN resolvedKN = KBox.resolve(serverURL, new URL("http://test.org"));
		assertEquals(resolvedKN.getTarget(), "http://target.org");
	}
	
	@Test
	public void testLocate() throws MalformedURLException, Exception {
		File resolved = KBox.locate(new URL("http://test.org"), "kibo");
		assertEquals(resolved, null);
	}
	
	@Test
	public void testResolveKNS() throws MalformedURLException, Exception {
		URL serverURL = TDBTest.class.getResource("/org/aksw/kbox/kibe/");
		KBResolver resolver = new KBResolver();
		KN resolvedKN = KBox.resolve(serverURL, new URL("http://test.org"), resolver);		
		assertEquals(resolvedKN.getTarget(), "http://target.org");
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
	public void testQueryInstalledKB2() throws Exception {
		ResultSet rs = KBox.query("Select ?p where {<http://dbpedia.org/ontology/Place> ?p ?o}",
				new URL("http://dbpedia.org/3.9"));
		int i = 0;
		while (rs != null && rs.hasNext()) {
			rs.next();
			i++;
		}
		assertEquals(19, i);
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
	public void listKNSServers() throws MalformedURLException, Exception {
		MockKNSServerListVisitor mockKNSVisitor = new MockKNSServerListVisitor();
		KBox.visit(mockKNSVisitor);
		assertEquals(1, mockKNSVisitor.getVisits());
	}
	
	@Test
	public void listKNSServers2() throws MalformedURLException, Exception {
		URL serverURL = TDBTest.class.getResource("/org/aksw/kbox/kibe/");
		KBox.installKNS(serverURL);
		MockKNSServerListVisitor mockKNSVisitor = new MockKNSServerListVisitor();
		KBox.visit(mockKNSVisitor);
		assertEquals(2, mockKNSVisitor.getVisits());
		KBox.removeKNS(serverURL);
		mockKNSVisitor = new MockKNSServerListVisitor();
		KBox.visit(mockKNSVisitor);
		assertEquals(1, mockKNSVisitor.getVisits());
	}
	
}
