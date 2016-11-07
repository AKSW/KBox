package org.aksw.kbox.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Deque;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZIPUtil {

	public static void zip(String dir, String dest, StreamListener streamListener) throws IOException {
		File sourceFile = new File(dir);
		File destFile = new File(dest);
		zip(sourceFile, destFile, streamListener);
	}

	public static void zip(File sourceFile, File destFile, StreamListener streamListener) throws IOException {
		URI base = sourceFile.toURI();
		Deque<File> queue = new LinkedList<File>();
		queue.push(sourceFile);
		try (OutputStream out = new FileOutputStream(destFile);) {
			try (ZipOutputStream zout = new ZipOutputStream(out);) {
				while (!queue.isEmpty()) {
					File directory = queue.pop();
					for (File kid : directory.listFiles()) {
						String name = base.relativize(kid.toURI()).getPath();
						if (kid.isDirectory()) {
							queue.push(kid);
							name = name.endsWith("/") ? name : name + "/";
							zout.putNextEntry(new ZipEntry(name));
						} else {
							zout.putNextEntry(new ZipEntry(name));
							copy(kid, zout, streamListener);
							zout.closeEntry();
						}
					}
				}
			}
		}
	}

	public static void unzip(File sourceZipfile, File directory, StreamListener streamListener) throws IOException {
		try(ZipFile zfile = new ZipFile(sourceZipfile);) {
			Enumeration<? extends ZipEntry> entries = zfile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				File file = new File(directory, entry.getName());
				if (entry.isDirectory()) {
					file.mkdirs();
				} else {
					file.getParentFile().mkdirs();
					try (InputStream in = zfile.getInputStream(entry);) {
						copy(in, file, streamListener);
					}
				}
			}
		}
	}

	public static void unzip(String sourceZipFilePath, String destFilePath, StreamListener streamListener) throws IOException {
		unzip(new File(sourceZipFilePath), new File(destFilePath), streamListener);
	}

	private static void copy(InputStream in, OutputStream out, StreamListener streamListener)
			throws IOException {
		byte[] buffer = new byte[1024];
		int readCount = 0;
		streamListener.start();
		while ((readCount = in.read(buffer)) >= 0) {			
			out.write(buffer, 0, readCount);
			streamListener.update(readCount);
		}
		streamListener.stop();
	}

	private static void copy(File file, OutputStream out, StreamListener streamListener) throws IOException {
		try(InputStream in = new FileInputStream(file)) {
			copy(in, out, streamListener);
		}
	}

	private static void copy(InputStream in, File file, StreamListener streamListener) throws IOException {
		try (OutputStream out = new FileOutputStream(file);){
			copy(in, out, streamListener);
		}
	}

	public static void unzip(InputStream sourceStream, String destFilePath, StreamListener streamListener) throws IOException {
		BufferedInputStream is = null;
		File destFile = new File(destFilePath);
		try (ZipInputStream zipStream = new ZipInputStream(sourceStream)) {
			ZipEntry entry = null;
			is = new BufferedInputStream(zipStream);
			while ((entry = zipStream.getNextEntry()) != null) {
				File file = new File(destFile, entry.getName());
				if (entry.isDirectory()) {
					file.mkdirs();
				} else {
					file.getParentFile().mkdirs();
					copy(is, file, streamListener);						
				}
			}
			is.close();
		}		
	}
}
