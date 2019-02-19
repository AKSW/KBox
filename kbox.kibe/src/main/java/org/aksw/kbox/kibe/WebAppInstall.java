package org.aksw.kbox.kibe;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.aksw.kbox.ZipInstall;

public class WebAppInstall extends ZipInstall {
	
	private String serverAddress = null;
	
	public WebAppInstall(String serverAddress) {
		this.serverAddress = serverAddress;
	}
	
	@Override
	public void install(InputStream inputStream, URL target) throws Exception {
		super.install(inputStream, target);
		String pathDir = urlToAbsolutePath(target);
		setServerAddress(pathDir);
		super.validate(target);
	}
	
	public void setServerAddress(String pathDir) throws IOException {
		String file = pathDir + File.separator + "index.html";
		Path filePath = Paths.get(file);
		Charset charset = StandardCharsets.UTF_8;
		String content = new String(Files.readAllBytes(filePath), charset);
		content = content.replaceAll("kbox_server_address", serverAddress);
		Files.write(filePath, content.getBytes(charset));
	}
	
	@Override
	public void validate(URL url) throws Exception {
		// remove validation method from super class
	}
}
