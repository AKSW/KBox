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
		AssertionUtils.notNull(new IllegalArgumentException("context"), context);
		AssertionUtils.notNull(new IllegalArgumentException("path"), path);
		this.context = context;
		this.path = path;
		File dbFile = new File(path);
		if(!dbFile.exists()) {
			createDB();
		}
	}
	
	private void createDB() {		
		DB db = getDB(false);
		db.commit();
		db.close();
	}
	
	public synchronized String getProperty(String property, String defaultValue) {
		AssertionUtils.notNull(new IllegalArgumentException("property"), property);
		AssertionUtils.notNull(new IllegalArgumentException("defaultValue"), defaultValue);
		String value;
		try (DB db = getReadOnlyDB()){
			value = getMap(db).get(property);
			if(value == null) {
				return defaultValue;
			}
			return value;
		}
	}
	
	public synchronized void setProperty(String property, String value) {
		AssertionUtils.notNull(new IllegalArgumentException("property"), property);
		AssertionUtils.notNull(new IllegalArgumentException("value"), value);
		try (DB db = getDB(false)) {
			getMap(db).put(property, value);
			db.commit();
		}		
	}
	
	public synchronized void add(String value) {
		AssertionUtils.notNull(new IllegalArgumentException("value"), value);
		try (DB db = getDB(false)) {
			getSet(db).add(value);
			db.commit();
		}
	}
		
	public synchronized boolean visit(CustomParamVisitor visitor) throws Exception {
		AssertionUtils.notNull(new IllegalArgumentException("visitor"), visitor);
		try(DB db = getReadOnlyDB()) {
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
			return next;
		}
	}
	
	private HTreeMap<String, String> getMap(DB db) {
		HTreeMap<String, String> treeMap = db.hashMap(context);
		return treeMap;
	}
	
	private Set<String> getSet(DB db) {
		Set<String> treeMap = db.hashSet(context);
		return treeMap;
	}
	
	private DB getDB(boolean readOnly) {
		File file = new File(path);
		DB db = null;
		if(readOnly) {
			db = DBMaker.fileDB(file).readOnly().make();			
		} else {
			db = DBMaker.fileDB(file).make();
		}
		return db;
	}
	
	private DB getReadOnlyDB() {
		return getDB(true);
	}

	public void remove(String value) {
		try (DB db = getDB(false)) {
			getSet(db).remove(value);
			db.commit();
		}	
	}
}
