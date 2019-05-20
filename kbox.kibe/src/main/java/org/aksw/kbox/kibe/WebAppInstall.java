package org.aksw.kbox.kibe;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.aksw.kbox.ZipInstall;

public class WebAppInstall extends ZipInstall {
	
	private static final String SERVER_ADDRESS_PAGE_PARAM = "kbox_server_address";
	private static final String ANCHOR_PAGE = "index.html";
	
	private String serverAddress = null;
	
	
	public WebAppInstall(String serverAddress) {
		this.serverAddress = serverAddress;
	}
	
	@Override
	public void stream(InputStream inputStream, File target) throws Exception {
		super.stream(inputStream, target);
		setServerAddress(target.getAbsolutePath());
	}
	
	public void setServerAddress(String pathDir) throws IOException {
		String file = pathDir + File.separator + ANCHOR_PAGE;
		Path filePath = Paths.get(file);
		Charset charset = StandardCharsets.UTF_8;
		String content = new String(Files.readAllBytes(filePath), charset);
		content = content.replaceAll(SERVER_ADDRESS_PAGE_PARAM, serverAddress);
		Files.write(filePath, content.getBytes(charset));
	}
}
