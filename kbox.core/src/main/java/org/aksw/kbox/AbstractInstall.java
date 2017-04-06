package org.aksw.kbox;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public abstract class AbstractInstall implements Install, Locator {
	
	private InputStreamFactory isFactory = null;
	
	public AbstractInstall() {		
	}
	
	public AbstractInstall(InputStreamFactory isFactory) {
		this.isFactory = isFactory;
	}
	
	@Override
	public void install(URL source, URL dest) throws Exception {
		if(isFactory == null) {
			try(InputStream is = source.openStream()) {
				install(is, dest);
			}
		} else {
			try(InputStream is = isFactory.get(source)) {
				install(is, dest);
			}
		}
	}

	@Override
	public void install(InputStream inputStream, URL dest) throws Exception {
		File destFile = new File(URLToAbsolutePath(dest));
		File resourceDir = destFile.getParentFile();
		resourceDir.mkdirs();
		destFile.createNewFile();
		ReadableByteChannel rbc = Channels.newChannel(inputStream);
		try (FileOutputStream fos = new FileOutputStream(destFile)) {
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		}
		validate(destFile);
	}
	
	public void validate(File destFile) throws IOException {
		KBox.validate(destFile);
	}
}
