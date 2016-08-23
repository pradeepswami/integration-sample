package com.example.integration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.sshd.SshServer;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.Session;
import org.apache.sshd.common.file.FileSystemView;
import org.apache.sshd.common.file.nativefs.NativeFileSystemFactory;
import org.apache.sshd.common.file.nativefs.NativeFileSystemView;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.UserAuth;
import org.apache.sshd.server.auth.UserAuthNone;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.sftp.SftpSubsystem;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestServer {

	Logger logger = LoggerFactory.getLogger(TestServer.class);

	@Rule
	public TemporaryFolder folder = new TemporaryFolder(new File(System.getProperty("java.io.tmpdir")));

	@Test
	public void test() {
		start();
		System.err.println("tets");

	}

	public void start() {

		SshServer sshd = SshServer.setUpDefaultServer();
		sshd.setPort(Integer.valueOf(2222));
		sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("clientkeystore"));

		sshd.setFileSystemFactory(new NativeFileSystemFactory() {

			@Override
			public FileSystemView createFileSystemView(Session session) {
				return new NativeFileSystemView(session.getUsername(), false) {
					@Override
					public String getVirtualUserDir() {
						try {
							File newFolder = folder.newFolder("sftpFolder");
							System.err.println(newFolder.getAbsoluteFile().getPath());
							return newFolder.getAbsoluteFile().getPath();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return null;
					}
				};
			}

		});

		List<NamedFactory<UserAuth>> userAuthFactories = new ArrayList<NamedFactory<UserAuth>>();
		userAuthFactories.add(new UserAuthNone.Factory());
		sshd.setUserAuthFactories(userAuthFactories);
		sshd.setPublickeyAuthenticator(new DummyAuthenticator());

		sshd.setCommandFactory(new ScpCommandFactory());

		List<NamedFactory<Command>> namedFactoryList = new ArrayList();
		namedFactoryList.add(new SftpSubsystem.Factory());
		sshd.setSubsystemFactories(namedFactoryList);

		try {
			sshd.start();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}
