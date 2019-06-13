package org.aksw.kbox.apple.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.utils.IOUtils;

public class UncompressUtil {
	public static void unpack(InputStream is, File output) throws IOException, CompressorException {
	    try(BufferedInputStream bis = new BufferedInputStream(is)) {
		    try(CompressorInputStream input = new CompressorStreamFactory().createCompressorInputStream(bis)) {
			    try(OutputStream out = new FileOutputStream(output)) {
			    	IOUtils.copy(input, out);
			    }
		    }
	    }
	}
}
