package com.ims.InventorySystem.representations;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "inventory_detail")
@XmlRootElement
public class Product {
	@Id
	@Column(name = "product_id")
	private Long productId;
	
	@Column(name = "product_name")
	private String productName;
	
	@Column(name = "quantity")
	private int quantity;
		
	@Column(name = "category")
	private String category;
	
	@Column(name = "created_at")
	private String createdAt;
	
	@Column(name = "updated_at")
	private String updatedAt;
	
	public Product() {
		
	}
	
	public Product(Long id, String name, int quantity, String category, String createdAt, String updatedAt) {
		this.productId = id;
		this.productName = name;
		this.quantity = quantity;
		this.category = category;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
	
	@JsonProperty
	public Long getProductId() {
		return productId;
	}

	@JsonProperty
	public void setProductId(Long id) {
		this.productId = id;
	}
	
	@JsonProperty
	public String getProductName() {
		return productName;
	}

	@JsonProperty
	public void setProductName(String name) {
		this.productName = name;
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
	public String getCategory() {
		return category;
	}

	@JsonProperty 
	public void setCategory(String category) {
		this.category = category;
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
	public String getUpdatedAt() {
		return updatedAt;
	}
	
	@JsonProperty
	public void getUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}
	
}
