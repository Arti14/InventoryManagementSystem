package com.ims.InventorySystem;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ims.InventorySystem.client.AwsCredentials;

import io.dropwizard.Configuration;
//import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.db.DataSourceFactory;

public class AppConfiguration extends Configuration {
	@JsonProperty
	private DataSourceFactory database = new DataSourceFactory();
	
	@JsonProperty
	private AwsCredentials awsCredentials;
	
	public DataSourceFactory getDataSourceFactory() {
		return database;
	}
	
	public AwsCredentials getAwsCredentials() {
		return awsCredentials;
	}
}
