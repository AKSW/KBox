package org.aksw.kbox.kibe;

import java.net.URL;

import kbox.apple.AppZipInstall;

import org.aksw.kbox.InputStreamFactory;
import org.aksw.kbox.kns.Resolver;

public class KBInstall extends AppZipInstall {
	
	public KBInstall() {
		super(KBFile.FORMAT, KBFile.VERSION);
	}

	public KBInstall(InputStreamFactory isFactory) {
		super(KBFile.FORMAT, KBFile.VERSION, isFactory);
	}
	
	public String URLToAbsolutePath(URL url, Resolver resolver) throws Exception {
		return super.URLToAbsolutePath(url);
	}
}
