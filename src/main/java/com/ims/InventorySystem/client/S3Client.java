package com.ims.InventorySystem.client;

import java.io.File;
import java.io.InputStream;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class S3Client {
	
	private AmazonS3 s3;
	
	public S3Client(final AWSCredentials awsCredentials) {
		s3 = new AmazonS3Client(awsCredentials);
	}
	
	public InputStream getObject(final String bucket, final String key) {
		GetObjectRequest request = new GetObjectRequest(bucket, key);
		return s3.getObject(request).getObjectContent();
	}
	
	public void uploadObject(final String bucket, final String key,
			final File file) {
		PutObjectRequest request = new PutObjectRequest(bucket, key, file);
		s3.putObject(request);
	}
}
