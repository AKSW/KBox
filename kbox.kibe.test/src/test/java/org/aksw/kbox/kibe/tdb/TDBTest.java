package org.aksw.kbox.kibe.tdb;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;

import com.hp.hpl.jena.query.ResultSet;

public class TDBTest {
	
	private static String graphPath = null;
	
	@BeforeClass
	public static void init() throws IOException {
		URL[] files = new URL[1];
		URL url = TDBTest.class.getResource("/org/aksw/kbox/kibe/dbpedia_3.9.xml");
		files[0] = url;
		java.nio.file.Path f = Files.createTempDirectory("kb");
		graphPath = f.toFile().getPath();
		TDB.bulkload(f.toFile().getPath(), files);
	}
		
	@Test
	public void testBulkLoad() throws URISyntaxException, IOException {
		URL[] files = new URL[1];
		URL url = TDBTest.class.getResource("/org/aksw/kbox/kibe/dbpedia_3.9.xml");
		files[0] = url;
		java.nio.file.Path f = Files.createTempDirectory("kb");
		String path = f.toFile().getPath();
		TDB.bulkload(f.toFile().getPath(), files);
		Date start = new Date();
		ResultSet rs = TDB.query("Select ?p where {<http://dbpedia.org/ontology/Place> ?p ?o}", path);
		int i = 0;
		Date end = new Date();
		System.out.println(end.getTime() - start.getTime());
		while (rs != null && rs.hasNext()) {
			rs.next();
			i++;
		}
		assertEquals(19, i);
	}
	
	@Test
	public void testMultiGraphQuery() throws URISyntaxException, IOException {
		URL[] files = new URL[1];
		URL url = TDBTest.class.getResource("/org/aksw/kbox/kibe/foaf.rdf");
		files[0] = url;
		java.nio.file.Path f = Files.createTempDirectory("kb");
		String foafPath = f.toFile().getPath();
		TDB.bulkload(f.toFile().getPath(), files);
		Date start = new Date();
		ResultSet rs = TDB.query("Select ?p where {<http://dbpedia.org/ontology/Place> ?p ?o}", foafPath, graphPath);
		int i = 0;
		Date end = new Date();
		System.out.println(end.getTime() - start.getTime());
		while (rs != null && rs.hasNext()) {
			rs.next();
			i++;
		}
		assertEquals(19, i);
		
		start = new Date();
		rs = TDB.query("Select ?p where {<http://xmlns.com/foaf/0.1/Agent> ?p ?o}", foafPath, graphPath);
		i = 0;
		end = new Date();
		System.out.println(end.getTime() - start.getTime());
		while (rs != null && rs.hasNext()) {
			rs.next();
			i++;
		}
		assertEquals(9, i);
	}
	
	@Test
	public void testQuery2() throws URISyntaxException, IOException {
		ResultSet rs = TDB.query("Select ?p where {<http://dbpedia.org/ontology/Place> ?p ?o}", graphPath);
		int i = 0;
		while (rs != null && rs.hasNext()) {
			rs.next();i++;
		}
		assertEquals(19, i);
	}
}
