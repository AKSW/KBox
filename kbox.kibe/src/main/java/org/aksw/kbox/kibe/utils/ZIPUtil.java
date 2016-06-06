package org.aksw.kbox.kibe.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZIPUtil {

	public static void zip(String dir, String destFile) {
		try {
			BufferedInputStream origin = null;
			FileOutputStream dest = new FileOutputStream(destFile);
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
					dest));
			int BUFFER = 2048;
			// out.setMethod(ZipOutputStream.DEFLATED);
			byte data[] = new byte[BUFFER];
			// get a list of files from current directory
			File f = new File(dir);
			String files[] = f.list();

			for (int i = 0; i < files.length; i++) {
				FileInputStream fi = new FileInputStream(dir + "/" + files[i]);
				origin = new BufferedInputStream(fi, BUFFER);
				ZipEntry entry = new ZipEntry(files[i]);
				out.putNextEntry(entry);
				int count;
				while ((count = origin.read(data, 0, BUFFER)) != -1) {
					out.write(data, 0, count);
				}
				origin.close();
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void unzip(String zipFilePath, String destFilePath) {
		try {
			BufferedOutputStream dest = null;
			BufferedInputStream is = null;
			int BUFFER = 2048;
			ZipEntry entry = null;
			try (ZipFile zipfile = new ZipFile(zipFilePath)) {
				Enumeration<? extends ZipEntry> e = zipfile.entries();
				while (e.hasMoreElements()) {
					entry = (ZipEntry) e.nextElement();
					is = new BufferedInputStream(zipfile.getInputStream(entry));
					int count;
					byte data[] = new byte[BUFFER];
					FileOutputStream fos = new FileOutputStream(destFilePath + "/"
							+ entry.getName());
					dest = new BufferedOutputStream(fos, BUFFER);
					while ((count = is.read(data, 0, BUFFER)) != -1) {
						dest.write(data, 0, count);
					}
					dest.flush();
					dest.close();
					is.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void unzip(InputStream sourceStream, String destFilePath) {
		try {
			BufferedOutputStream dest = null;
			BufferedInputStream is = null;
			int BUFFER = 2048;
			try (ZipInputStream zipStream = new ZipInputStream(sourceStream)) {				
				ZipEntry entry = null;
				is = new BufferedInputStream(zipStream);
				while((entry=zipStream.getNextEntry()) != null) {					
					int count;
					byte data[] = new byte[BUFFER];
					File destFile = new File(destFilePath + "/"
					+ entry.getName());
					if(!destFile.exists()) {
						destFile.createNewFile();
					}
					FileOutputStream fos = new FileOutputStream(destFile);
					dest = new BufferedOutputStream(fos, BUFFER);
					while ((count = is.read(data, 0, BUFFER)) != -1) {
						dest.write(data, 0, count);
					}
					dest.flush();
					dest.close();					
				}
				is.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
