package org.aksw.kbox.kibe.console;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.aksw.kbox.kibe.stream.InstallStreamListener;
import org.apache.commons.lang3.StringUtils;

/**
 * This class implements a StreamListener for command line
 * stream process feedback.
 * 
 * @author http://emarx.org
 */
public class ConsoleStreamListener implements InstallStreamListener {
	private String message = "loading";
	private long length = 0;
	private int read = 0;
	private long startTime = 0;
	private int size = 20;
	
	/**
	 * Constructor of CommandLineStreamListener
	 * 
	 * @param message the message that will appear ahead of the progress bar in the console.
	 * @param length stream length.
	 */
	public ConsoleStreamListener(String message, int length) {
		this(message);
		this.length = length;		
	}
	
	
	/**
	 * Constructor of CommandLineStreamListener
	 * 
	 * @param message the message that will appear ahead of the progress bar in the console.
	 */
	public ConsoleStreamListener(String message) {
		this.message = message;
	}
	
	@Override
	public void update(long bytes) {
		if(length > 0) {
			read += bytes;
			int percentage = (int)(((double)read / (double)length) * 100);
			if(percentage > 100) {
				percentage = 100;
			}
			printProgress(startTime, 100, percentage, size);
		} else {			
			printUndefined(read, size);
			read++;
			if(read == size) {
				read = 0;
			}
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
		printProgress(startTime, 100, 100, size);
		System.out.println();
	}
	
	/**
	 * Print the progress in console.
	 * 
	 * @param startTime time that the stream started in milliseconds.
	 * @param total stream length in percentage.
	 * @param current streamed length in percentage.
	 */
	private void printProgress(long startTime, int total, int current, int size) {
	    long eta = current == 0 ? 0 : 
	        (total - current) * (System.currentTimeMillis() - startTime) / current;

	    String etaHms = current == 0 ? "N/A" : 
	            String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(eta),
	                    TimeUnit.MILLISECONDS.toMinutes(eta) % TimeUnit.HOURS.toMinutes(1),
	                    TimeUnit.MILLISECONDS.toSeconds(eta) % TimeUnit.MINUTES.toSeconds(1));

	    StringBuilder string = new StringBuilder(140);
	    int logCurrent = 0;
	    if(current != 0) {
	    	logCurrent = (int) (Math.log10(current));
	    }
	    int percent = (int) (current * 100 / total);
	    string
	        .append('\r')
	        .append(message)
	        .append(StringUtils.join(Collections.nCopies(percent == 0 ? 2 : 2 - (int) (Math.log10(percent)), " "), ""))
	        .append(String.format(" %d%% [", percent))
	        .append(StringUtils.join(Collections.nCopies((percent/(percent/size)), "="), ""))
	        .append('>')
	        .append(StringUtils.join(Collections.nCopies(size-(percent/(percent/size)), " "), ""))
	        .append(']')
	        .append(StringUtils.join(Collections.nCopies((int) (Math.log10(total)) - logCurrent, " "), ""))
	        .append(String.format(" %d/%d, ETA: %s", current, total, etaHms));
	    System.out.print(string);
	}
	
	/**
	 * Print a undefined progress in the console.
	 * 
	 * @param read
	 */
	private void printUndefined(int read, int size) {		
	    StringBuilder string = new StringBuilder(140);
	    string
	        .append('\r')
	        .append(message)
	        .append("  ??% [");
        for(int i=0; i < size+1; i++) {
        	if(i==read) {
        		string.append(".");
        	} else {
        		string.append(" ");
        	}
        }
        string
        .append("] ??/??, ETA: ??:??:?? ");
	    System.out.print(string);
	}
}
