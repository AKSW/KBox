package kbox.apple;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.aksw.kbox.InputStreamFactory;
import org.aksw.kbox.KBox;
import org.aksw.kbox.ZipInstall;
import org.aksw.kbox.kns.AppInstall;
import org.aksw.kbox.utils.ZIPUtil;

public class ZipAppInstall extends ZipInstall implements AppInstall {
	
	public String URLToAbsolutePath(URL url, String format, String version) throws Exception {
		return KBox.getResourceFolder()
				+ File.separator
				+ format
				+ File.separator
				+ version
				+ File.separator
				+ KBox.URLToPath(url);
	}
	
	@Override
	public void install(URL source, URL target, String format, String version)
			throws Exception {
		try(InputStream is = source.openStream()) {
			install(is, target, format, version);
		}
	}
	
	@Override
	public void install(URL resource, URL dest, String format, String version, InputStreamFactory factory) throws Exception {
		try(InputStream is = isFactory.get(resource)) {
			install(is, dest, format, version);
		}
	}

	@Override
	public void install(InputStream resource, URL dest, String format,
			String version) throws Exception {
		File destPath = new File(URLToAbsolutePath(dest, format, version));
		destPath.mkdirs();
		ZIPUtil.unzip(resource, destPath.getAbsolutePath());
		validate(destPath);
	}
}