package org.aksw.kbox;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.aksw.kbox.KBox;
import org.junit.Test;

public class KBoxTest {

	@Test
	public void testResourceFolderMethods() throws MalformedURLException, Exception {
		String currentResourceFolder = KBox.getResourceFolder();
		String newResourceFolder = currentResourceFolder + File.separator + "test";
		File newResourceFolderFile = new File(newResourceFolder);
		newResourceFolderFile.mkdir();
		newResourceFolderFile.deleteOnExit();		
		KBox.setResourceFolder(newResourceFolder);
		assertEquals(newResourceFolder, KBox.getResourceFolder());
		KBox.setResourceFolder(currentResourceFolder);
		
	}
	
	@Test
	public void testFileMethods() throws MalformedURLException, Exception {
		File kboxedFile = KBox.getResource(new URL("http://downloads.dbpedia.org/3.8/en/contents-nt.txt"));		
		String path = KBox.urlToPath(new URL("http://downloads.dbpedia.org/3.8/en/contents-nt.txt"));
		KBox.install(new URL("http://downloads.dbpedia.org/3.8/en/contents-nt.txt"));
		File inDisk = new File(KBox.getResourceFolder() + File.separator + path);
		assertEquals(true, inDisk.exists());
		kboxedFile = KBox.getResource(new URL("http://downloads.dbpedia.org/3.8/en/contents-nt.txt"));
		assertEquals(kboxedFile.getPath(), inDisk.getPath());
	}
	
	@Test
	public void testPublishMethods() throws MalformedURLException, Exception {
		File test = KBox.getResource(new URL("http://tttt"));
		assertEquals(null, test);
		URL fileToInstall = new URL("http://downloads.dbpedia.org/3.8/en/contents-nt.txt");
		File kboxedFile = KBox.getResource(fileToInstall);
		KBox.install(fileToInstall, new URL("http://test.org/context.txt"));
		String path = KBox.urlToPath(new URL("http://test.org/context.txt"));
		File inDisk = new File(KBox.getResourceFolder() + File.separator + path);
		assertEquals(true, inDisk.exists());
		kboxedFile =  KBox.getResource(new URL("http://test.org/context.txt"));
		assertEquals(kboxedFile.getPath(), inDisk.getPath());
	}

}
