package org.aksw.kbox.utils;

/**
 * 
 * This class contains some useful method for Arrays.
 * 
 * @author {@linkplain http://emarx.org}
 * 
 */
public class ArrayUtils {
	
	/**
	 * This class receive an <T> array and invert it.
	 * 
	 * @param array an <T> array.
	 */
	public static <T> void reverse(T[] array) {
		for(int i = 0; i < array.length / 2; i++) {
		    T temp = array[i];
		    array[i] = array[array.length - i - 1];
		    array[array.length - i - 1] = temp;
		}
	}
}
