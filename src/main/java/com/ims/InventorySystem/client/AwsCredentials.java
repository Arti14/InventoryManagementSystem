package com.ims.InventorySystem.client;

import com.amazonaws.auth.AWSCredentials;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AwsCredentials implements AWSCredentials {

	@JsonProperty
	private String accessKeyId;
	
	@JsonProperty
	private String secretAccessKey;

	@JsonIgnore
	public String getAWSAccessKeyId() {
		return accessKeyId;
	}

	@JsonIgnore
	public String getAWSSecretKey() {
		return secretAccessKey;
	}
}
