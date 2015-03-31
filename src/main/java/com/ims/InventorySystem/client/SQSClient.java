package com.ims.InventorySystem.client;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class SQSClient {
	
	AmazonSQS sqs;
	
	public SQSClient(final AWSCredentials awsCredentials) {
		sqs = new AmazonSQSClient(awsCredentials);
	}
	
	public void sendMessage(final String queueUrl, 
			final String messageBody) {
		SendMessageRequest request = new SendMessageRequest(
				queueUrl, messageBody);
		sqs.sendMessage(request);
	}

	public List<Message> getMessage(final String queueUrl,
			final int maxNumberOfMessages) {
		ReceiveMessageRequest request = new ReceiveMessageRequest(queueUrl);
		request.setMaxNumberOfMessages(maxNumberOfMessages);
		ReceiveMessageResult result = sqs.receiveMessage(request);
		List<Message> messages = result.getMessages();
		return messages;
	}
	
	public void deleteMessage(final String queueUrl,
			final String receiptHandle) {
		DeleteMessageRequest request = new DeleteMessageRequest(queueUrl, 
				receiptHandle);
	}
}
