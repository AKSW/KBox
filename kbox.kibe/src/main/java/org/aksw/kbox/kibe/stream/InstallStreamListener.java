package org.aksw.kbox.kibe.stream;

/**
 * Stream Listener implementation for handle ZipStreams.
 * 
 * @author http://emarx.org
 *
 */
public interface InstallStreamListener extends StreamListener {
	
	/**
	 * Invoked when the stream read/write process starts.
	 */
	public void start();
	
	/**
	 * Invoked when the stream read/write process stops.
	 */
	public void stop();	


	/**
	 * Set the stream length.
	 * 
	 * @param content Length.
	 */
	public void setLength(long length);
}
