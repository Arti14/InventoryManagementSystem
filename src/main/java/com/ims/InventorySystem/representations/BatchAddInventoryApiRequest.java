package com.ims.InventorySystem.representations;

public class BatchAddInventoryApiRequest {

	private String bucket;
	
	private String key;

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
	
	
}
