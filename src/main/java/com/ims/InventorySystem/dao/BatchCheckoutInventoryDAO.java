package com.ims.InventorySystem.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.sqs.model.Message;
import com.ims.InventorySystem.client.S3Client;
import com.ims.InventorySystem.client.SQSClient;
import com.ims.InventorySystem.representations.BatchAddInventoryRequest;

/**
 * @author rt
 *
 */
public class BatchCheckoutInventoryDAO {

	public static final String SQS_QUEUE_URL = 
			"https://sqs.ap-southeast-1.amazonaws.com/074379253665/INVENTORY_BACTH_CHECKOUT";
	
	private S3Client s3;
	private SQSClient sqs;
	
	public BatchCheckoutInventoryDAO(final AWSCredentials credentials) {
		s3 = new S3Client(credentials);
		sqs = new SQSClient(credentials);
	}
	
	public void queueBatchCheckoutInventoryRequest(BatchAddInventoryRequest request) 
			throws IOException {
		sqs.sendMessage(SQS_QUEUE_URL, request.serialize());
	}
	
	public BatchAddInventoryRequest fetchBatchCheckoutInventoryRequest() 
			throws IOException {
		List<Message> messages = sqs.getMessage(SQS_QUEUE_URL, 1);
		if (messages.size() == 0) {
			return null;
		}
		BatchAddInventoryRequest request = BatchAddInventoryRequest
				.deserialize(messages.get(0).getBody());
		request.setReceiptHandle(messages.get(0).getReceiptHandle());
		return request;
	}
	
	public void deleteFromQueueBatchCheckoutInventoryRequest(
			BatchAddInventoryRequest request) {
		sqs.deleteMessage(SQS_QUEUE_URL, request.getReceiptHandle());
	}
	
	public InputStream getBatchCheckoutInventoryFileStream(
			BatchAddInventoryRequest request) {
		return s3.getObject(request.getBucket(), request.getKey());
	}
	
	public void uploadToS3(final String bucket, final String key,
			final InputStream in) throws IOException {
		File file = File.createTempFile("ADD-INVENTORY-REQUEST-", ".tmp");
		FileWriter writer = new FileWriter(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line = null;
		while ((line = br.readLine()) != null) {
			writer.write(line);
			writer.write("\n");
		}
		writer.close();
		br.close();
		s3.uploadObject(bucket, key, file);
	}
}

