package com.example.integration;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.integration.IntegrationSampleApplication.InboundGateway;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IntegrationSampleApplicationTests {

	private static File sftpfolder;

	@BeforeClass
	public static void beforeClass() {
		try {
			sftpfolder = new File(System.getProperty("java.io.tmpdir"), "sftpFolder");
			FileUtils.forceMkdir(sftpfolder);
			new SftpServer().start(sftpfolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void afterClass() throws IOException {
		FileUtils.forceDelete(sftpfolder);
	}

	@Autowired
	private InboundGateway inboundGateway;

	@Test
	public void contextLoads() {
		inboundGateway.processMessage(Arrays.asList("sample1", "sample2"));
		assertThat(2, equalTo(sftpfolder.list().length));
	}
}
