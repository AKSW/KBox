package org.aksw.kbox.utils;

public class AssertionUtils {
	public static <T extends Exception> void assertNotNull(T exception, Object... object) throws T {
		if(object == null) {
			throw exception;
		}
	}
}
