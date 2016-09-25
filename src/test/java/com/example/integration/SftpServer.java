package com.example.integration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.sshd.SshServer;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.sftp.SftpSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SftpServer {

	Logger logger = LoggerFactory.getLogger(SftpServer.class);

	public void start(File ftpFodler) {

		SshServer sshd = SshServer.setUpDefaultServer();
		sshd.setPort(Integer.valueOf(2222));
		sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("clientkeystore"));
		sshd.setPasswordAuthenticator((String, PrivateKey, ServerSession) -> true);

		sshd.setFileSystemFactory(new VirtualFileSystemFactory(ftpFodler.getAbsolutePath()));

		sshd.setCommandFactory(new ScpCommandFactory());

		List<NamedFactory<Command>> namedFactoryList = new ArrayList<>();
		namedFactoryList.add(new SftpSubsystem.Factory());
		sshd.setSubsystemFactories(namedFactoryList);

		try {
			sshd.start();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}
