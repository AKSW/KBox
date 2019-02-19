package org.aksw.kbox.kibe.console;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.aksw.kbox.kibe.stream.InstallStreamListener;
import org.apache.commons.lang3.StringUtils;

/**
 * This class implements a StreamListener for command line
 * streaming feedback.
 * 
 * @author http://emarx.org
 */
public class ConsoleStreamListener implements InstallStreamListener {
	private String message = "loading";
	private long length = 0;
	private long read = 0;
	private long startTime = 0;
	private int blockSize = 20;
	private boolean stoped = false;
	
	/**
	 * CommandLineStreamListener's constructor
	 * 
	 * @param message the message that will appear ahead of the progress bar in the console.
	 * @param length stream length.
	 */
	public ConsoleStreamListener(String message, long length) {
		this(message);
		this.length = length;		
	}
	
	
	/**
	 * CommandLineStreamListener's constructor 
	 * 
	 * @param message the message that will appear ahead of the progress bar in the console.
	 */
	public ConsoleStreamListener(String message) {
		this.message = message;
	}
	
	@Override
	public void update(long bytes) {
		read += bytes;
		if(length > 0) {
			printProgress(startTime, read, length, blockSize);
		} else {			
			printUndefined(read, blockSize);
		}
	}
	
	@Override
	public void setLength(long length) {
		this.length = length;
	}

	@Override
	public void start() {
		startTime = System.currentTimeMillis();
		read = 0;
	}

	@Override
	public void stop() {
		if(!stoped) {
			System.out.println();
			stoped = true;
		}
	}
	
	/**
	 * Print the progress in the console.
	 * 
	 * @param startTime time that the stream started in milliseconds.
	 * @param blockSize stream size.
	 * @param read current streamed length.
	 */
	private void printProgress(long startTime, long read, long length, int blockSize) {
	
		int percentage = (int)(((double)read / (double)length) * 100);
		if(percentage > 100) {
			percentage = 100;
		}
	
		long elapsedTime = (System.currentTimeMillis() - startTime);
		
	    long eta = read == 0 ? 0 : 
	        (((length) * elapsedTime) / read);
    
	    String etaHms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(eta),
	                    TimeUnit.MILLISECONDS.toMinutes(eta) % TimeUnit.HOURS.toMinutes(1),
	                    TimeUnit.MILLISECONDS.toSeconds(eta) % TimeUnit.MINUTES.toSeconds(1));

	    StringBuilder string = new StringBuilder(140);
	    int logCurrent = 0;
	    if(percentage != 0) {
	    	logCurrent = (int) (Math.log10(percentage));
	    }
	    int blocks = (percentage * blockSize) / 100;
	    String bar = "=";
	    String cursor = read != length ? ">" : bar;

	    string
	        .append('\r')
	        .append(message)
	        .append(StringUtils.join(Collections.nCopies(percentage == 0 ? 2 : 2 - (int) (Math.log10(percentage)), " "), ""))
	        .append(String.format(" %d%% [", percentage))
	        .append(StringUtils.join(Collections.nCopies(blocks, bar), ""))
	        .append(cursor)
	        .append(StringUtils.join(Collections.nCopies(blockSize-(blocks), " "), ""))
	        .append(']')
	        .append(StringUtils.join(Collections.nCopies((int) (Math.log10(100)) - logCurrent, " "), ""))
	        .append(String.format(" [%d/%d] ETA: %s", read, length, etaHms));
	
	    System.out.print(string);
	}
	
	/**
	 * Print a undefined progress in the console.
	 * 
	 * @param read the number of read bytes.
	 * @param size the size of the console bar.
	 */
	private void printUndefined(long read, int size) {
	    StringBuilder string = new StringBuilder(140);
	    int pos = (int) ((read)/(size*50000)) % size;
	    string
	        .append('\r')
	        .append(message)
	        .append("  ??% [");
        for(int i=0; i < size+1; i++) {
        	if(i==pos) {
        		string.append("?");
        	} else if(i < pos) {
        		string.append("?");
        	} else {
        		string.append(" ");
        	}
        }
        string
        .append("] [??/??], ETA: ??:??:?? ");
	    System.out.print(string);
	}
}
