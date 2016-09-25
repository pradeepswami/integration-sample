package com.example.integration;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.sftp.Sftp;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.integration.sftp.session.SftpRemoteFileTemplate;

@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@IntegrationComponentScan
public class IntegrationSampleApplication {

	@Autowired
	private Sftpconfig sftpconfig;

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(IntegrationSampleApplication.class, args);
		// context.getBean(InboundGateway.class).processMessage(Arrays.asList("sample.txt",
		// "test.txt"));
	}

	@Bean
	public DefaultSftpSessionFactory sftpSessionFactory() {
		DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory(true);
		factory.setHost(sftpconfig.getHost());
		factory.setPort(sftpconfig.getPort());
		factory.setUser(sftpconfig.getUser());
		factory.setPassword(sftpconfig.getPassword());
		factory.setAllowUnknownKeys(true);
		return factory;
	}

	@MessagingGateway
	public interface InboundGateway {

		@Gateway(requestChannel = "messageInChannel")
		Collection<String> processMessage(Collection<String> strings);

	}

	public SftpRemoteFileTemplate getSftpRemoteFileTemplate() {
		SftpRemoteFileTemplate sftpRemoteFileTemplate = new SftpRemoteFileTemplate(sftpSessionFactory());
		sftpRemoteFileTemplate.setRemoteDirectoryExpression(new LiteralExpression("/"));
		return sftpRemoteFileTemplate;
	}

	@Bean
	public IntegrationFlow flow() {
		return IntegrationFlows.from("messageInChannel")
				// @formatter:off
				.split().transform(new TransformToFile())
				.handle(Sftp.outboundGateway(getSftpRemoteFileTemplate(), "put", "").remoteFileSeparator("/")).get();
		// @formatter:on
	}
}
