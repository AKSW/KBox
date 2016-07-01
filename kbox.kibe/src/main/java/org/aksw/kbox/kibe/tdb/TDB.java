package org.aksw.kbox.kibe.tdb;

import java.net.URL;
import java.util.Arrays;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.tdb.TDBLoader;
import com.hp.hpl.jena.tdb.transaction.DatasetGraphTransaction;

public class TDB {
	
	public static ResultSet query(String sparql, String dbDir) {
		Query query = QueryFactory.create(sparql);
		return query(query, dbDir);
	}

	public static ResultSet query(String sparql, String graph, String dbDir) {
		Query query = QueryFactory.create(sparql, graph);
		return query(query, dbDir);
	}
	
	public static ResultSet query(Query query, String dbDir) {
		Dataset dataset = TDBFactory.createDataset(dbDir);
		dataset.begin(ReadWrite.READ);
		Model model = dataset.getDefaultModel();
		model = ModelFactory.createRDFSModel(model);
		dataset.end();
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		ResultSet results = qe.execSelect();
		return results;
	}

	public static void bulkload(String dir, URL[] urls) {
		DatasetGraphTransaction dataset = (DatasetGraphTransaction) TDBFactory.createDatasetGraph(dir);
		String[] stringURLs = new String[urls.length];
		int i=0;
		for(URL url : urls) {
			stringURLs[i] = url.toString();
			i++;
		}
		TDBLoader.load(dataset.getDatasetGraphToQuery(), Arrays.asList(stringURLs));
	}
}
