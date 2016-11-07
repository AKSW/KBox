package org.aksw.kbox;

import java.io.File;
import java.net.URL;

import org.aksw.kbox.utils.StreamListener;
import org.aksw.kbox.utils.ZIPUtil;

public class ZipInstall implements Install {

	@Override
	public void install(URL source, URL dest) throws Exception {
		File urlDir = KBox.newDir(dest);
		ZIPUtil.unzip(source.openStream(), urlDir.getAbsolutePath(), new StreamListener() {
			
			@Override
			public void update(int bytes) {
			}
			
			@Override
			public void stop() {
			}
			
			@Override
			public void start() {
			}
			
			@Override
			public void setLength(int length) {
			}
		});
	}

}
