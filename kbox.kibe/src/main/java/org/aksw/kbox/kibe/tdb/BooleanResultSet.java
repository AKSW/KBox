package org.aksw.kbox.kibe.tdb;

import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.engine.binding.Binding;

public class BooleanResultSet extends IteratorResultSet<Boolean>  {
	
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
