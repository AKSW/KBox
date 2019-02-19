package org.aksw.kbox.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class StreamUtils {
	public static void stream(InputStream inputStream, File destFile) throws FileNotFoundException, IOException {
		ReadableByteChannel rbc = Channels.newChannel(inputStream);
		try (FileOutputStream fos = new FileOutputStream(destFile)) {
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		}
	}
}
