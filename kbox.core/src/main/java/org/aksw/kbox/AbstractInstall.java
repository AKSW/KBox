package org.aksw.kbox;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public abstract class AbstractInstall implements Install, PathBinder {
	
	@Override
	public void install(URL source, URL dest) throws Exception {
		try(InputStream is = source.openStream()) {
			install(is, dest);
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
		validate(dest);
	}
	
	public void validate(URL url) throws Exception {
		File f = new File(URLToAbsolutePath(url));
		KBox.validate(f);
	}
}
