package org.aksw.kbox.kibe.stream;

/**
 * The listener for a stream processing.
 * 
 * @author http://emarx.org
 */
public interface StreamListener {
	
	/**
	 * This method is invoked each time that bytes are 
	 * successfully read/written in a stream.
	 * 
	 * @param bytes the number of bytes read/written.
	 */
	public void update(long bytes);
}
