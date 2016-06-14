package org.aksw.kbox.kibe.tdb;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;

import org.aksw.kbox.kibe.tdb.TDB;
import org.junit.Test;

import com.hp.hpl.jena.query.ResultSet;

public class TDBTest {
		
	@Test
	public void testBulkLoad() throws URISyntaxException, IOException {
		URL[] files = new URL[1];
		URL url = TDBTest.class.getResource("/org/aksw/kbox/kibe/dbpedia_3.9.xml");
		files[0] = url;
		java.nio.file.Path f = Files.createTempDirectory("kb");
		TDB.bulkload(f.toFile().getPath(), files);
		ResultSet rs = TDB.query("Select ?p where {<http://dbpedia.org/ontology/Place> ?p ?o}", "./testbulk");
		int i = 0;
		while (rs != null && rs.hasNext()) {
			rs.next();i++;
		}
		assertEquals(19, i);
	}
	
	@Test
	public void testQuery2() throws URISyntaxException {
		ResultSet rs = TDB.query("Select ?p where {<http://dbpedia.org/ontology/Place> ?p ?o}", "./testbulk");
		int i = 0;
		while (rs != null && rs.hasNext()) {
			rs.next();i++;
		}
		assertEquals(19, i);
	}
}
