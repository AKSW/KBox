package kbox.apple;

import java.io.File;
import java.net.URL;

import org.aksw.kbox.KBox;
import org.aksw.kbox.ZipInstall;

public class AppZipInstall extends ZipInstall {
	private String format = null;
	private String version = null;
	
	public AppZipInstall(String format, String version) {
		this.format = format;
		this.version = version;
	}

	@Override
	public String URLToAbsolutePath(URL url) {
		return KBox.getResourceFolder()
				+ File.separator
				+ format
				+ File.separator
				+ version
				+ File.separator
				+ KBox.URLToPath(url);
	}
}
