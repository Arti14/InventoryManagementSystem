package com.ims.InventorySystem.representations;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import com.fasterxml.jackson.databind.ObjectMapper;

public class BatchAddInventoryRequest {

	private String bucket;
	
	private String key;
	
	private String transactionId;
	
	private String receiptHandle;

	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String serialize() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		StringWriter writer = new StringWriter();
		mapper.writeValue(writer, this);
		return writer.toString();
	}
	
	public static BatchAddInventoryRequest deserialize(final String json) 
			throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		StringReader reader = new StringReader(json);
		return mapper.readValue(reader, BatchAddInventoryRequest.class);
	}

	public String getReceiptHandle() {
		return receiptHandle;
	}

	public void setReceiptHandle(String receiptHandle) {
		this.receiptHandle = receiptHandle;
	}
}
