package org.aksw.kbox;

public interface Visitor<T> {
	public boolean visit(T object) throws Exception;
}
