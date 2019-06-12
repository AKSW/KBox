package org.aksw.kbox.kibe.utils;

import java.io.File;

public class FileUtils {
	public static String[] files2AbsolutePath(File... files) {
		String[] paths = new String[files.length];
		for(int i = 0; i < files.length; i++) {
			paths[i]  = files[i].getAbsolutePath();
		}
		return paths;
	}
}
