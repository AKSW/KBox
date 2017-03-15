package org.aksw.kbox.kibe;

import kbox.apple.AppZipInstall;

import org.aksw.kbox.InputStreamFactory;

public class KBInstall extends AppZipInstall {

	public KBInstall() {
		super(KBFile.FORMAT, KBFile.VERSION);
	}

	public KBInstall(InputStreamFactory isFactory) {
		super(KBFile.FORMAT, KBFile.VERSION, isFactory);
	}
}
