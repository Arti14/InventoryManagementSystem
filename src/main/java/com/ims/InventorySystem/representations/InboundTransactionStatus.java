package com.ims.InventorySystem.representations;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "inbound_transaction_status")
public class InboundTransactionStatus {
	
	@Id
	@Column(name = "trans_id")
	private String transId;
	
	@Column(name = "trans_status")
	private String status;
	
	public InboundTransactionStatus() { }
	
	public InboundTransactionStatus(String transId, String status) {
		this.transId = transId;
		this.status = status;
	}
	
	@JsonProperty
	public String getTransId() {
		return transId;
	}
	
	@JsonProperty
	public void setTransId(String transId) {
		this.transId = transId;
	}
	
	@JsonProperty
	public String getStatus() {
		return status;
	}
	
	@JsonProperty
	public void setStatus(String status) {
		this.status = status;
	}

}
