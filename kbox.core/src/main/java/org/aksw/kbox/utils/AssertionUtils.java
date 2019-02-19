package org.aksw.kbox.utils;

public class AssertionUtils {
	public static <T extends Exception> void notNull(T exception, Object... objects) throws T {
		for(Object object : objects) {
			if(object == null) {
				throw exception;
			}
		}
	}
}
