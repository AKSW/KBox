package org.aksw.kbox.kibe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.aksw.kbox.apple.stream.DefaultInputStreamFactory;
import org.aksw.kbox.kibe.tdb.TDBTest;
import org.aksw.kbox.kns.KBoxKNSServer;
import org.aksw.kbox.kns.KN;
import org.aksw.kbox.kns.KNSServer;
import org.aksw.kbox.kns.exception.ResourceNotResolvedException;
import org.apache.jena.query.ResultSet;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class KBoxTest {
	
	@BeforeClass
	public static void setUp() throws Exception {
		File indexFile = File.createTempFile("kbo","idx");
		indexFile.deleteOnExit();
		URL[] filesToIndex = new URL[1];
		URL url = TDBTest.class.getResource("/org/aksw/kbox/kibe/dbpedia_3.9.xml");
		filesToIndex[0] = url;
		KBox.RDFToKB(indexFile, filesToIndex);
		KBox.installKB(indexFile.toURI().toURL(), new URL("http://dbpedia39"));
		indexFile.deleteOnExit();

		KBox.installKB(indexFile.toURI().toURL(), new URL("http://dbpedia.org/3.9"));
		indexFile.deleteOnExit();

		indexFile = File.createTempFile("kbo","idx");
		indexFile.deleteOnExit();
		url = TDBTest.class.getResource("/org/aksw/kbox/kibe/foaf.rdf");
		filesToIndex[0] = url;		
		KBox.RDFToKB(indexFile, filesToIndex);
		KBox.installKB(indexFile.toURI().toURL(), new URL("http://foaf"));
		indexFile.deleteOnExit();
	}
	
	@Test
	public void testVisitKBs() throws Exception {
		URL serverURL = KBoxTest.class.getResource("/org/aksw/kbox/kibe/");
		MockKNSVisitor visitor = new MockKNSVisitor();
		KNSServer server = new KBoxKNSServer(serverURL);
		server.visit(visitor);
		assertEquals(7, visitor.getKNSVisitedList().size());
	}
	
	@Test
	public void testKNEquals() throws Exception {
		KN rn = new KN("teste", null, "b", null, null, null, null);
		assertTrue("teste".equals(rn.getName()));
	}
	
	@Test
	public void testResolveKN() throws Exception {
		URL serverURL = KBoxTest.class.getResource("/org/aksw/kbox/kibe/");
		KN resolvedKN = KBox.resolve(serverURL, new URL("http://test.org"));
		Assert.assertNull(resolvedKN.getTags());
		
		Assert.assertEquals("http://test.org", resolvedKN.getName());
		Assert.assertEquals("http://ipv4.download.thinkbroadband.com:81/5MB.zip", resolvedKN.getTargets().get(0).getURL().toString());
		Assert.assertEquals(null, resolvedKN.getVersion());
		Assert.assertEquals(null, resolvedKN.getFormat());
		
		resolvedKN = KBox.resolve(serverURL, new URL("http://test2.org"), "format", "version");
		Assert.assertTrue(resolvedKN.getTags().size() > 0);
		Assert.assertTrue(resolvedKN.getTags().contains("tag2.test.2"));
		
		Assert.assertEquals("label", resolvedKN.getLabel());
		Assert.assertEquals("version", resolvedKN.getVersion());
		Assert.assertEquals("format", resolvedKN.getFormat());
		Assert.assertEquals("owner", resolvedKN.getOwner());
		Assert.assertEquals("publisher", resolvedKN.getPublisher());
		Assert.assertEquals("license", resolvedKN.getLicense());
		Assert.assertTrue(resolvedKN.getTargets().get(0).getChecksum().containsKey("check1"));
		Assert.assertEquals("34233", resolvedKN.getTargets().get(0).getChecksum().get("check1"));
	}
	
	@Test
	public void testGetResource() throws Exception {
		DefaultInputStreamFactory isFactory = new DefaultInputStreamFactory();
		URL resourceName = new URL("http://test.org");
		URL knsServerURL = KBoxTest.class.getResource("/org/aksw/kbox/kibe/");
		File f = KBox.getResource(knsServerURL, resourceName, null, null, isFactory);
		Assert.assertTrue(f.delete());
		Assert.assertNotNull(f);
	}
	
	@Test(expected=ResourceNotResolvedException.class)
	public void testResourceNotResolvedException() throws Exception {	
		DefaultInputStreamFactory isFactory = new DefaultInputStreamFactory();
		URL resourceName = new URL("http://foaf");
		URL rnsServerURL = KBoxTest.class.getResource("/org/aksw/kbox/kibe/");
		KBox.getResource(rnsServerURL, resourceName, null, null, isFactory);
		Assert.fail("The query should have returned a ResourceNotFoundException.");
	}
	
	@Test(expected=ResourceNotResolvedException.class)
	public void testResourceNotResolvedException2() throws Exception {
		DefaultInputStreamFactory isFactory = new DefaultInputStreamFactory();
		URL resourceName = new URL("http://test.org");
		URL rnsServerURL = KBoxTest.class.getResource("/org/aksw/kbox/kibe/");
		File resource = KBox.getResource(rnsServerURL, resourceName, "teste", null, isFactory, true);
		Assert.assertNull(resource);
	}
	
	@Test
	public void testGetResourceNull() throws Exception {
		DefaultInputStreamFactory isFactory = new DefaultInputStreamFactory();
		URL resourceName = new URL("http://test.org");
		URL rnsServerURL = KBoxTest.class.getResource("/org/aksw/kbox/kibe/");
		File resource = KBox.getResource(rnsServerURL, resourceName, null, null, isFactory, false);
		Assert.assertNull(resource);
	}

	@Test
	public void testKNEqualsWithFormat() throws Exception {
		KN rn = new KN("teste", null, "b", "c", null, null, null);
		assertTrue(rn.equals("teste","b", "c"));
	}

	@Test
	public void testKNEqualsWithFormatWithNull1() throws Exception {
		KN rn = new KN("teste", null, "b", "d", null, null, null);
		Assert.assertFalse(rn.equals("teste","b", "e"));
	}

	@Test
	public void testKNEqualsWithFormatWithNull2() throws Exception {
		KN rn = new KN("teste", null, "b", "c", null, null, null);
		assertTrue(rn.equals("teste","b", null));
	}

	@Test
	public void testKNEqualsWithFormatWithNull3() throws Exception {
		KN rn = new KN("teste", null, "b", "c", null, null, null);
		Assert.assertFalse(rn.equals("teste", "b", "d"));
	}
	
	@Test
	public void testResolveURLWithKBoxKNSService() throws Exception {
		URL serverURL = KBoxTest.class.getResource("/org/aksw/kbox/kibe/");
		KN resolvedKN = KBox.resolve(serverURL, new URL("http://test.org"));
		assertEquals("http://ipv4.download.thinkbroadband.com:81/5MB.zip", 
				resolvedKN.getTargets().get(0).getURL().toString());
	}

	@Test
	public void testLocate() throws MalformedURLException, Exception {
		File resolved = KBox.locate(new URL("http://test.org"), "kibo");
		assertEquals(resolved, null);
	}

	@Test
	public void testResolveKNS() throws MalformedURLException, Exception {
		URL serverURL = TDBTest.class.getResource("/org/aksw/kbox/kibe/");
		KN resolvedKN = KBox.resolve(serverURL, new URL("http://test.org"));		
		assertEquals("http://ipv4.download.thinkbroadband.com:81/5MB.zip", 
				resolvedKN.getTargets().get(0).getURL().toString());
	}
	
	@Test
	public void testResolveKNS2() throws MalformedURLException, Exception {
		URL serverURL = TDBTest.class.getResource("/org/aksw/kbox/kibe/");
		KN resolvedKN = KBox.resolve(serverURL, new URL("http://test.org"));
		assertEquals("http://ipv4.download.thinkbroadband.com:81/5MB.zip", 
				resolvedKN.getTargets().get(0).getURL().toString());
	}

	@Test
	public void testNewDir() throws Exception {
		File f = KBox.newDir(new URL("http://dbpedia.org/en/full"));
		assertTrue(f.getAbsolutePath().endsWith("en" + File.separator + "full"));
	}
	
	@Test
	public void testInstallBZ2() throws Exception {
		URL knsServerURL = TDBTest.class.getResource("/org/aksw/kbox/kibe/");
		File fileDir = KBox.getResource(knsServerURL, new URL("http://dbpedia.org"), "xml", "0", true);
		Assert.assertNotNull(fileDir);
	}
	
	@Test
	public void testInstallZIP() throws Exception {
		URL knsServerURL = TDBTest.class.getResource("/org/aksw/kbox/kibe/");
		File fileDir = KBox.getResource(knsServerURL, new URL("http://dbpedia.org/zip"), "xml", "0", true);
		Assert.assertNotNull(fileDir);
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
	}
	
	@Test
	public void testQueryKBXML() throws Exception {
		URL knsServerURL = TDBTest.class.getResource("/org/aksw/kbox/kibe/");
		ResultSet rs = KBox.query(knsServerURL, new URL("http://dbpedia.org/3.9/xml"), 
				"Select ?p where {<http://dbpedia.org/ontology/Place> ?p ?o}", true);
		int i = 0;
		while (rs != null && rs.hasNext()) {
			rs.next();
			i++;
		}
		assertEquals(19, i);
	}
	
	@Test
	public void testQueryKBZip() throws Exception {
		URL knsServerURL = TDBTest.class.getResource("/org/aksw/kbox/kibe/");
		ResultSet rs = KBox.query(knsServerURL, new URL("http://dbpedia.org/3.9/zip"),
				"Select ?p where {<http://dbpedia.org/ontology/Place> ?p ?o}", true);
		int i = 0;
		while (rs != null && rs.hasNext()) {
			rs.next();
			i++;
		}
		assertEquals(19, i);
	}
	
	@Test
	public void testQueryKBXMLWithTag() throws Exception {
		URL knsServerURL = TDBTest.class.getResource("/org/aksw/kbox/kibe/");
		ResultSet rs = KBox.query(knsServerURL, new URL("http://dbpedia.org"),
				"Select ?p where {<http://dbpedia.org/ontology/Place> ?p ?o}", true);
		int i = 0;
		while (rs != null && rs.hasNext()) {
			rs.next();
			i++;
		}
		assertEquals(19, i);
	}
	
	@Test
	public void testQueryKBBZ2() throws Exception {
		URL knsServerURL = TDBTest.class.getResource("/org/aksw/kbox/kibe/");
		ResultSet rs = KBox.query(knsServerURL, new URL("http://dbpedia.org/3.9/bz2"),
				"Select ?p where {<http://dbpedia.org/ontology/Place> ?p ?o}", true);
		int i = 0;
		while (rs != null && rs.hasNext()) {
			rs.next();
			i++;
		}
		assertEquals(19, i);
	}
	
	@Test(expected=Exception.class)
	public void testQueryKB() throws Exception {
		KBox.query(
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
	public void listKNSServers4() throws MalformedURLException, Exception {
		URL serverURL = TDBTest.class.getResource("/org/aksw/kbox/kibe/");
		MockKNSServerListVisitor mockKNSVisitor = new MockKNSServerListVisitor();
		URLKNSServerList serverList = new URLKNSServerList(serverURL);
		serverList.visit(mockKNSVisitor);
		assertEquals(3, mockKNSVisitor.getVisits());
		MockKNSVisitor knsVisitor = new MockKNSVisitor();
		mockKNSVisitor.getServers().get(1).visit(knsVisitor);
		assertEquals(10, knsVisitor.getKNSVisitedList().size());
		knsVisitor = new MockKNSVisitor();
		mockKNSVisitor.getServers().get(2).visit(knsVisitor);
		assertEquals(10, knsVisitor.getKNSVisitedList().size());
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
	
	@Test
	public void listKNSServers3() throws MalformedURLException, Exception {
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
	
	@Test
	public void testSPARQLKNSServer() throws MalformedURLException, Exception {
		Map<String, String> mappings = new HashMap<String, String>();
		mappings.put(KN.NAME, "?p");
		SPARQLKNSServer server = new SPARQLKNSServer("http://dbpedia.org/sparql", 
				"Select ?p where {<http://dbpedia.org/ontology/Place> ?p ?o} limit 10",
				mappings
				);
		MockKNSVisitor knsVisitor = new MockKNSVisitor();
		server.visit(knsVisitor);
		assertEquals(10, knsVisitor.getKNSVisitedList().size());
	}

}
