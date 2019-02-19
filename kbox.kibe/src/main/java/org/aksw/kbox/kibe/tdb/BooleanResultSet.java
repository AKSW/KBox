package org.aksw.kbox.kibe.tdb;

import java.util.Iterator;

import org.apache.jena.query.QuerySolutionMap;
import org.apache.jena.rdf.model.Model;

public class BooleanResultSet extends IteratorResultSet<Boolean> {

	private static final String VALUE = "value";
	private Model model;

	BooleanResultSet(Iterator<Boolean> triples, Model model) {
		super(triples);
		this.model = model;
	}

	@Override
	protected QuerySolutionMap getQuerySolutionMap(Boolean t) {
		QuerySolutionMap map = new QuerySolutionMap();
		map.add(VALUE, model.createTypedLiteral(t));
		return map;
	}

}
