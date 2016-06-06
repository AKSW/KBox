package org.aksw.kbox;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.aksw.kbox.KBox;
import org.junit.Test;

public class RBoxTest {

	@Test
	public void testResourceFolderMethods() throws MalformedURLException, Exception {
		
//		Files.deleteIfExists(new File(KBox.RBOX_DIR).toPath());
		assertEquals(KBox.KBOX_DIR, KBox.getResourceFolder());

		KBox.setResourceFolder("c:/");
		assertEquals("c:/", KBox.getResourceFolder());

		KBox.setResourceFolder(KBox.KBOX_DIR);
	}
	
	@Test
	public void testFileMethods() throws MalformedURLException, Exception {		
		File rboxedFile = KBox.getResource(new URL("http://downloads.dbpedia.org/3.8/en/contents-nt.txt"));		
		String path = KBox.URLToPath(new URL("http://downloads.dbpedia.org/3.8/en/contents-nt.txt"));

		File inDisk = new File(KBox.getResourceFolder() + File.separator + path);
		assertEquals(true, inDisk.exists());
		assertEquals(rboxedFile.getPath(), inDisk.getPath());
	}
	
	
	@Test
	public void testPublishMethods() throws MalformedURLException, Exception {
		File test = KBox.getResource(new URL("http://tttt"));
		assertEquals(null, test);
		URL fileToInstall = new URL("http://downloads.dbpedia.org/3.8/en/contents-nt.txt");
		File rboxedFile = KBox.getResource(fileToInstall);
		KBox.install(new URL("http://test.org/context.txt"), fileToInstall);
		String path = KBox.URLToPath(new URL("http://test.org/context.txt"));
		File inDisk = new File(KBox.getResourceFolder() + File.separator + path);
		assertEquals(true, inDisk.exists());
		rboxedFile =  KBox.getResource(new URL("http://test.org/context.txt"));
		assertEquals(rboxedFile.getPath(), inDisk.getPath());
	}

}
