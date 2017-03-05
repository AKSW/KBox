package org.aksw.kbox.kibe;

import java.io.InputStream;
import java.net.URL;

import org.aksw.kbox.kibe.stream.InputStreamFactory;

import kbox.apple.AppZipInstall;

public class KBInstall extends AppZipInstall {
	
	private InputStreamFactory isFactory = null;

	public KBInstall() {
		super(KBFile.FORMAT, KBFile.VERSION);
	}

	public KBInstall(InputStreamFactory isFactory) {
		this();
		this.isFactory = isFactory;
	}
	
	@Override
	public void install(URL source, URL dest) throws Exception {
		try(InputStream is = isFactory.get(source)) {
			install(is, dest);
		}
	}
}
