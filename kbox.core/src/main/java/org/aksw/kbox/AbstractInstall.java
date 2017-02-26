package org.aksw.kbox;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public abstract class AbstractInstall implements Install, Locator {
	
	@Override
	public void install(URL source, URL dest) throws Exception {
		try(InputStream is = source.openStream()) {
			install(source.openStream(), dest);
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
	}
}
