package org.aksw.kbox.kibe.tdb;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.naming.OperationNotSupportedException;

import org.aksw.kbox.appel.ServerAddress;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.tdb.TDBLoader;
import com.hp.hpl.jena.tdb.transaction.DatasetGraphTransaction;

public class TDB {
		
	public static ResultSet queryGraph(String sparql, String graph, String dbDir) throws Exception {
		Query query = QueryFactory.create(sparql, graph);
		return query(query, dbDir);
	}
	
	public static Model createModel(String... dbDirs) {
		Model mainModel = null; 
		Dataset dataset = null;
		for(String dbDir : dbDirs) {
			dataset = TDBFactory.createDataset(dbDir);
			if(mainModel == null) {
				mainModel = dataset.getDefaultModel();				
			} else {
				Model secondaryModel = dataset.getDefaultModel();
				mainModel = ModelFactory.createUnion(mainModel, secondaryModel);
			}
		}
		mainModel = ModelFactory.createRDFSModel(mainModel);
		return mainModel;
	}
	
	public static ResultSet query(Query query, String... dbDirs) throws OperationNotSupportedException {
		Model mainModel = createModel(dbDirs);		
		return query(query, mainModel);
	}
	
	public static ResultSet query(String sparql, String... dbDirs) throws OperationNotSupportedException {
		Model model = createModel(dbDirs);
		Query query = QueryFactory.create(sparql);
		return query(query, model);
	}
	
	public static ResultSet query(String sparql, Model model) throws OperationNotSupportedException {
		Query query = QueryFactory.create(sparql);
		return query(query, model);
	}
	
	public static ResultSet query(Query query, Model model) {
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		ResultSet results = null;
		if(query.isSelectType()) {
			results = qe.execSelect();
		} else if(query.isDescribeType()) {
			Iterator<Triple> triples = qe.execDescribeTriples();
			results = new TripleResultSet(triples, model);
		} else if(query.isConstructType()) {
			Iterator<Triple> triples = qe.execConstructTriples();
			results = new TripleResultSet(triples, model);
		} else if(query.isAskType()) {
			Boolean answer = qe.execAsk();
			List<Boolean> result = new ArrayList<Boolean>();
			result.add(answer);
			results = new BooleanResultSet(result.iterator(), model);
		}
		return results;
	}

	/**
	 * Load a list of files into a database located in a given directory.
	 * 
	 * @param dir directory containing the database file.
	 * @param urls URLs of the files containing the RDF content to be loaded into the database file. 
	 */
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

	/**
	 * Query a given KBox endpoint.
	 * 
	 * @param sparql a valid SPARQL query.
	 * @param address the ServerAddress of the service that will be queried.
	 * @return an ResultSet containing the elements retrieved by the given SPARQL query.
	 */
	public static ResultSet queryService(String sparql, ServerAddress address) {
		QueryExecution qe = QueryExecutionFactory.sparqlService(
				address.getQueryURL(), sparql);
        ResultSet results = qe.execSelect();
		return results;
	}
}
