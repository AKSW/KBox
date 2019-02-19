package org.aksw.kbox.kibe.tdb;

import java.util.Iterator;

import org.apache.jena.graph.Triple;
import org.apache.jena.query.QuerySolutionMap;
import org.apache.jena.rdf.model.Model;

public class TripleResultSet extends IteratorResultSet<Triple> {
	
	private static final String SUBJECT = "subject";
	private static final String PREDICATE = "predicate";
	private static final String OBJECT = "object";
	private Model model = null;
	
	public TripleResultSet(Iterator<Triple> triples, Model model) {
		super(triples);
		this.model = model;
	}

	@Override
	protected QuerySolutionMap getQuerySolutionMap(Triple t) {
		QuerySolutionMap map = new QuerySolutionMap();
		map.add(SUBJECT, model.asRDFNode(t.getSubject()));
		map.add(PREDICATE, model.asRDFNode(t.getPredicate()));
		map.add(OBJECT, model.asRDFNode(t.getObject()));
		return map;
	}

}
