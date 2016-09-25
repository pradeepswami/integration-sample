package com.example.integration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.transformer.Transformer;
import org.springframework.messaging.Message;

public class TransformToFile implements Transformer {

	@Override
	public Message<?> transform(Message<?> message) {
		File dir = new File(System.getProperty("java.io.tmpdir"));
		File file = new File(dir, UUID.randomUUID().toString() + ".txt");
		try {
			FileUtils.write(file, (String) message.getPayload());
		} catch (IOException e) {
			e.printStackTrace();
		}

		Message<File> fileMsg = MessageBuilder.withPayload(file).copyHeaders(message.getHeaders()).build();

		return fileMsg;
	}
}
