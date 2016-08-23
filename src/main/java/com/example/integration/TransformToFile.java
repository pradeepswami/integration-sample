package com.example.integration;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.transformer.Transformer;
import org.springframework.messaging.Message;

public class TransformToFile implements Transformer {

	@Override
	public Message<?> transform(Message<?> message) {
		String filePath = System.getProperty("java.io.tmpdir");
		System.err.println(filePath);
		File dir = new File(filePath);
		File file = new File(dir, (String) message.getPayload());
		try {
			FileUtils.write(file, (String) message.getPayload());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Message<File> fileMsg = MessageBuilder.withPayload(file).copyHeaders(message.getHeaders()).build();

		return fileMsg;
	}
}
