package org.aksw.kbox.kibe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.aksw.kbox.apple.stream.DefaultInputStreamFactory;
import org.aksw.kbox.kibe.tdb.TDBTest;
import org.aksw.kbox.kns.KNSSever;
import org.aksw.kbox.kns.RN;
import org.aksw.kbox.kns.ResourceResolver;
import org.aksw.kbox.kns.exception.ResourceNotLacatedException;
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
		KNSSever.visit(serverURL, visitor);
		assertEquals(1, visitor.getKNSVisitedList().size());
	}
	
	@Test
	public void testKNEquals() throws Exception {
		RN rn = new RN("teste", "a", "b", null, null, null, null);
		assertTrue("teste".equals(rn.getName()));
	}
	
	@Test
	public void testGetResource() throws Exception {
		DefaultInputStreamFactory isFactory = new DefaultInputStreamFactory();
		URL resourceName = new URL("http://test.org");
		URL rnsServerURL = KBoxTest.class.getResource("/org/aksw/kbox/kibe/");
		File f = KBox.getResource(rnsServerURL, resourceName, RN.DEFAULT_FORMAT, RN.DEFAULT_VERSION, isFactory);
		Assert.assertTrue(f.delete());
		Assert.assertNotNull(f);
	}
	
	@Test(expected=ResourceNotResolvedException.class)
	public void testResourceNotResolvedException() throws Exception {	
		DefaultInputStreamFactory isFactory = new DefaultInputStreamFactory();
		URL resourceName = new URL("http://foaf");
		URL rnsServerURL = KBoxTest.class.getResource("/org/aksw/kbox/kibe/");
		KBox.getResource(rnsServerURL, resourceName, null, RN.DEFAULT_VERSION, isFactory);
		Assert.fail("The query should have returned a ResourceNotFoundException.");
	}
	
	@Test(expected=ResourceNotLacatedException.class)
	public void testResourceNotLocatedException() throws Exception {
		DefaultInputStreamFactory isFactory = new DefaultInputStreamFactory();
		URL resourceName = new URL("http://test.org");
		URL rnsServerURL = KBoxTest.class.getResource("/org/aksw/kbox/kibe/");
		File resource = KBox.getResource(rnsServerURL, resourceName, RN.DEFAULT_FORMAT, RN.DEFAULT_VERSION, isFactory, false);
		Assert.assertNull(resource);
		Assert.fail("The query should have returned a ResourceNotLocatedException.");
	}

	@Test
	public void testKNEqualsWithFormat() throws Exception {
		RN rn = new RN("teste", "a", "b", "c", null, null, null);
		assertTrue(rn.equals("teste","b", "c"));
	}

	@Test
	public void testKNEqualsWithFormatWithNull1() throws Exception {
		RN rn = new RN("teste", "a", "b", "d", null, null, null);
		Assert.assertFalse(rn.equals("teste","b", "e"));
	}

	@Test
	public void testKNEqualsWithFormatWithNull2() throws Exception {
		RN rn = new RN("teste", "a", "b", "c", null, null, null);
		assertTrue(rn.equals("teste","b", null));
	}

	@Test
	public void testKNEqualsWithFormatWithNull3() throws Exception {
		RN rn = new RN("teste", "a", "b", "c", null, null, null);
		Assert.assertFalse(rn.equals("teste", "b", "d"));
	}
	
	@Test
	public void testResolveURLWithKBoxKNSService() throws Exception {
		URL serverURL = KBoxTest.class.getResource("/org/aksw/kbox/kibe/");
		RN resolvedKN = KBox.resolve(serverURL, new URL("http://test.org"));
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
		ResourceResolver resolver = new ResourceResolver();
		RN resolvedKN = KBox.resolve(serverURL, new URL("http://test.org"), resolver);		
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
	public void listKNSServers2() throws MalformedURLException, Exception {
		URL serverURL = TDBTest.class.getResource("/org/aksw/kbox/kibe/");
		KBox.installRNS(serverURL);
		MockKNSServerListVisitor mockKNSVisitor = new MockKNSServerListVisitor();
		KBox.visit(mockKNSVisitor);
		assertEquals(2, mockKNSVisitor.getVisits());
		KBox.removeRNS(serverURL);
		mockKNSVisitor = new MockKNSServerListVisitor();
		KBox.visit(mockKNSVisitor);
		assertEquals(1, mockKNSVisitor.getVisits());
	}

}
