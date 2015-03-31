package com.ims.InventorySystem.representations;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class TransactionCompositeId implements Serializable{
	
	@Column(name = "trans_id")
	protected String transId;
	
	@Column(name = "product_id")
    protected long productId;

    public TransactionCompositeId() {}

    public TransactionCompositeId(String transId, long productId) {
        this.transId = transId;
        this.productId = productId;
    }
    
    public String getTransId() {
    	return transId;
    }

	public void setTransId(String transId) {
		this.transId = transId;
	}
	
	public long getProductId() {
		return productId;
	}
	
    public void setProductId(long productId) {
    	this.productId = productId;
    }
}
