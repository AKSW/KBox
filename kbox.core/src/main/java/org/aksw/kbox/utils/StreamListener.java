package org.aksw.kbox.utils;

public interface StreamListener {
	
	/**
	 * Invoked when the stream read/write process starts.
	 */
	public void start();
	
	/**
	 * Invoked when the stream read/write process stops.
	 */
	public void stop();
	
	/**
	 * This method is invoked each time that bytes are 
	 * successfully read/written in a stream.
	 * 
	 * @param bytes the number of bytes read/written.
	 */
	public void update(int bytes);

	/**
	 * Set the stream length.
	 * 
	 * @param contentLength
	 */
	public void setLength(int length);
}
