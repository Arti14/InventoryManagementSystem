package com.ims.InventorySystem.representations;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "inventory_transaction")
public class SalesHistory implements Serializable{
	
	@EmbeddedId
	TransactionCompositeId id;
	
	@Column(name = "quantity")
	private int quantity;
	
	@Column(name = "created_at")
	private String createdAt;
	
	@Column(name = "trans_type")
	private String transType;
	
	public SalesHistory() {}
	
	public SalesHistory(TransactionCompositeId id, int quantity, String createdAt, String transType) {
		this.id = id;
		this.quantity = quantity;
		this.createdAt = createdAt;
		this.transType = transType;
	}
	
	@JsonProperty
	public TransactionCompositeId getId() {
		return id;
	}
	
	@JsonProperty
	public void setId(TransactionCompositeId id) {
		this.id = id;
	}
	
	@JsonProperty
	public int getQuantity() {
		return quantity;
	}
	
	@JsonProperty
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	@JsonProperty 
	public String getCreatedAt() {
		return createdAt;
	}
	
	@JsonProperty 
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	
	@JsonProperty 
	public String getTransType() {
		return transType;
	}
	
	@JsonProperty 
	public void setTransType(String transType) {
		this.transType = transType;
	}
}
