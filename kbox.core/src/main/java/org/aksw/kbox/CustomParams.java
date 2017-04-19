package org.aksw.kbox;

import java.io.File;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;

import org.aksw.kbox.utils.AssertionUtils;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;

/**
 * This class implements a persistent parameter map.
 * 
 * @author {@linkplain http://emarx.org}
 *
 */
public class CustomParams implements Serializable, Visitor<CustomParamVisitor> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8598003281170345448L;
	private String context;
	private String path;
	
	public CustomParams(String path, String context) {
		AssertionUtils.assertNotNull(new IllegalArgumentException("context"), context);
		AssertionUtils.assertNotNull(new IllegalArgumentException("path"), path);
		this.context = context;
		this.path = path;
		File dbFile = new File(path);
		if(!dbFile.exists()) {
			createDB();
		}
	}
	
	private void createDB() {		
		DB db = getDB();
		db.close();
	}
	
	public synchronized String getProperty(String property, String defaultValue) {
		AssertionUtils.assertNotNull(new IllegalArgumentException("property"), property);
		AssertionUtils.assertNotNull(new IllegalArgumentException("defaultValue"), defaultValue);
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
		AssertionUtils.assertNotNull(new IllegalArgumentException("property"), property);
		AssertionUtils.assertNotNull(new IllegalArgumentException("value"), value);
		DB db = getDB();
		try {
			getMap(db).put(property, value);
			db.commit();
		} finally  {
			db.close();
		}		
	}
	
	public synchronized void add(String value) {
		AssertionUtils.assertNotNull(new IllegalArgumentException("value"), value);
		DB db = getDB();
		try {
			getSet(db).add(value);
			db.commit();
		} finally  {
			db.close();
		}
	}
		
	public synchronized boolean visit(CustomParamVisitor visitor) throws Exception {
		AssertionUtils.assertNotNull(new IllegalArgumentException("visitor"), visitor);
		final DB db = getReadOnlyDB();
		final Iterator<String> internalIterator = getSet(db).iterator();
		Iterable<String> iterable = new Iterable<String>(
				) {
			
			@Override
			public Iterator<String> iterator() {
				return internalIterator;
			}
		};
		boolean next = false;
		for(String param : iterable) {
			next = visitor.visit(param);
			if(!next) {
				break;
			}	
		}
		db.close();
		return next;
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
