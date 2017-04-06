package org.aksw.kbox.kibe.stream;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamInstaller extends InputStream {
	
	private InputStream inputStream = null;
	private InstallStreamListener listener = null;
	private boolean started = false;
	
	public InputStreamInstaller(InputStream inputStream, InstallStreamListener listener) {
		this.inputStream = inputStream;
		this.listener = listener;
	}
	
	@Override
	public void close() throws IOException {
		inputStream.close();
		listener.stop();
	}
	
	@Override
	public boolean markSupported() {
		return inputStream.markSupported();
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		streaming();
		int read = inputStream.read(b);
		listener.update(read);
		return read;
	}
	
	@Override
	public synchronized void mark(int readlimit) {
		inputStream.mark(readlimit);
	}
	
	@Override
	public long skip(long n) throws IOException {
		return inputStream.skip(n);
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		streaming();
		int read = inputStream.read(b, off, len);
		listener.update(read);
		return read;
	}
	
	@Override
	public synchronized void reset() throws IOException {
		inputStream.reset();
	}
	
	@Override
	public int available() throws IOException {
		return inputStream.available();
	}

	@Override
	public int read() throws IOException {
		streaming();
		return inputStream.read();
	}
	
	private void streaming() {
		if(!started) {
			listener.start();
			started = true;
		}
	}

}
