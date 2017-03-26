package org.aksw.kbox.kibe.tdb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.naming.OperationNotSupportedException;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.engine.binding.Binding;

public abstract class IteratorResultSet<T> implements com.hp.hpl.jena.query.ResultSet {
	
	private static final String SUBJECT = "subject";
	private static final String PREDICATE = "predicate";
	private static final String OBJECT = "object";
	
	private List<String> vars = new ArrayList<String>();	
	private Iterator<T> triples = null;
	
	IteratorResultSet(Iterator<T> triples) {
		this.triples = triples;
		this.vars.add(SUBJECT);
		this.vars.add(PREDICATE);
		this.vars.add(OBJECT);
	}

	@Override
	public void remove() {
		triples.remove();
	}

	@Override
	public boolean hasNext() {
		return triples.hasNext();
	}

	@Override
	public QuerySolution next() {
		T t = triples.next();
		QuerySolutionMap map = getQuerySolutionMap(t);
		return map;
	}
	
	protected abstract QuerySolutionMap getQuerySolutionMap(T t);

	@Override
	public QuerySolution nextSolution() {
		return next();
	}

	@Override
	public Binding nextBinding() {
		throw new RuntimeException(new OperationNotSupportedException());
	}

	@Override
	public int getRowNumber() {
		throw new RuntimeException(new OperationNotSupportedException());
	}

	@Override
	public List<String> getResultVars() {
		return vars;
	}
	
	public void setResultVars(List<String> vars) {
		this.vars = vars;
	}

	@Override
	public Model getResourceModel() {
		throw new RuntimeException(new OperationNotSupportedException());
	}
}
