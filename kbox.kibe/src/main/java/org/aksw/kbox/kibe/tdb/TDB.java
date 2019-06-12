package org.aksw.kbox.kibe.tdb;

import java.io.File;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.naming.OperationNotSupportedException;

import org.aksw.kbox.kibe.utils.FileUtils;
import org.aksw.kbox.kns.ServerAddress;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.tdb.TDBLoader;
import org.apache.jena.tdb.transaction.DatasetGraphTransaction;

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
	
	public static Model createModel(File... dirs) {
		return createModel(FileUtils.files2AbsolutePath(dirs));
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
	
	public static ResultSet query(String sparql, File... dbDirs) throws OperationNotSupportedException {
		return query(sparql, FileUtils.files2AbsolutePath(dbDirs));
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
	public static void bulkload(String dir, URL... urls) {
		DatasetGraphTransaction dataset = (DatasetGraphTransaction) TDBFactory.createDatasetGraph(dir);
		String[] stringURLs = new String[urls.length];
		int i=0;
		for(URL url : urls) {
			stringURLs[i] = url.toString();
			i++;
		}
		TDBLoader.load(dataset.getDatasetGraphToQuery(), Arrays.asList(stringURLs), true, false);
	}
	
	/**
	 * Load a list of files into a database located in a given directory.
	 * 
	 * @param dir directory containing the database file.
	 * @param inputStreams URLs of the files containing the RDF content to be loaded into the database file.
	 * @param lang the RDF streams syntax
	 */
	public static void bulkload(String dir, InputStream[] inputStreams, Lang lang) {
		List<InputStream> streams = Arrays.asList(inputStreams);
		SequenceInputStream sInputStream = new SequenceInputStream(Collections.enumeration(streams));
		bulkload(dir, sInputStream, lang);
	}
	
	/**
	 * Load a list of files into a database located in a given directory.
	 * 
	 * @param dir directory containing the database file.
	 * @param inputStreams URLs of the files containing the RDF content to be loaded into the database file.
	 * @param lang the RDF streams syntax
	 */
	public static void bulkload(String dir, InputStream inputStream, Lang lang) {
		DatasetGraph dataset = TDBFactory.createDatasetGraph(dir);
		RDFDataMgr.read(dataset, inputStream, lang);
		dataset.close();
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
