package org.aksw.kbox;

import java.io.File;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;

/**
 * This class implements a persistent parameter map.
 * 
 * @author {@linkplain http://emarx.org}
 *
 */
public class CustomParams implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8598003281170345448L;
	private String context;
	private String path;
	
	public CustomParams(String path, String context) {
		this.context = context;
		this.path = path;
	}
	
	public synchronized String getProperty(String property, String defaultValue) {
		DB db = getDB();
		String value;
		try {
			value = getMap(db).get(property);
			if(value == null) {
				return defaultValue;
			}
			return value;
		} finally  {
			db.close();
		}
	}
	
	public synchronized void setProperty(String property, String value) {
		DB db = getDB();
		try {
			getMap(db).put(property, value);
			db.commit();
		} finally  {
			db.close();
		}		
	}
	
	public synchronized void add(String value) {
		DB db = getDB();
		try {
			getSet(db).add(value);
			db.commit();
		} finally  {
			db.close();
		}
	}
		
	public synchronized void visit(CustomParamVisitor visitor) {
		final DB db = getReadOnlyDB();
		final Iterator<String> internalIterator = getSet(db).iterator();
		Iterable<String> iterable = new Iterable<String>(
				) {
			
			@Override
			public Iterator<String> iterator() {
				return internalIterator;
			}
		};
		
		for(String param : iterable) {
			try {
				boolean next = visitor.visit(param);
				if(!next) {
					break;
				}
			} catch (Exception e) {
			}			
		}
		db.close();
	}
	
	private HTreeMap<String, String> getMap(DB db) {
		HTreeMap<String, String> treeMap = db.hashMap(context);
		return treeMap;
	}
	
	private Set<String> getSet(DB db) {
		Set<String> treeMap = db.hashSet(context);
		return treeMap;
	}
	
	private DB getDB() {
		File file = new File(path);
		DB db = DBMaker.fileDB(file).make();
		return db;
	}
	
	private DB getReadOnlyDB() {
		File file = new File(path);
		DB db = DBMaker.fileDB(file).readOnly().make();
		return db;
	}


	public void remove(String value) {
		DB db = getDB();
		try {
			getSet(db).remove(value);
			db.commit();
		} finally  {
			db.close();
		}	
	}
}
