package org.aksw.kbox.utils;

public class ObjectUtils extends AssertionUtils {
	
	public static <T> T getValue(T value, T defaultValue) {
		if(value == null) {
			return defaultValue;
		}
		return value;
	}

}
