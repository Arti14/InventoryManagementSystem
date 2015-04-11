package com.ims.InventorySystem.resources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.apache.log4j.Logger;

import com.amazonaws.auth.AWSCredentials;
import com.ims.InventorySystem.dao.BatchAddInventoryDAO;
import com.ims.InventorySystem.dao.BatchCheckoutInventoryDAO;
import com.ims.InventorySystem.dao.InventoryDAO;
import com.ims.InventorySystem.representations.BatchAddInventoryApiRequest;
import com.ims.InventorySystem.representations.BatchAddInventoryRequest;
import com.ims.InventorySystem.representations.Product;
import com.ims.InventorySystem.representations.ProductQuantityMap;
import com.ims.InventorySystem.representations.SalesHistory;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Path("/inventory")
@Produces(MediaType.APPLICATION_JSON)
public class InventoryDetailsResource {
	
	private static final String INVENTORY_BATCH_ADD_BUCKET_NAME = 
			"inventory-batch-add";
	
	private static final String INVENTORY_BATCH_CHECKOUT_BUCKET_NAME = 
			"inventory-batch-checkout";
	private final InventoryDAO dao;
	private final BatchAddInventoryDAO batchAddInventoryDAO;
	private final BatchCheckoutInventoryDAO batchCheckoutInventoryDAO;
	private static final Logger LOG = Logger.getLogger(InventoryDetailsResource.class);
	
	public InventoryDetailsResource(InventoryDAO dao, 
			BatchAddInventoryDAO batchAddInventoryDAO, BatchCheckoutInventoryDAO 
			batchCheckoutInventoryDAO) {
		this.dao = dao;
		this.batchAddInventoryDAO = batchAddInventoryDAO;
		this.batchCheckoutInventoryDAO = batchCheckoutInventoryDAO;
	}
	
	@GET
	@Path("/{id}")
	@UnitOfWork
	public Response getInventoryByProductId(@PathParam("id") Long id) {
		Product product = dao.findById(id);
		return Response
				.ok(product)
				.build();
	}
	
	@POST
	@Path("/add")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response addProduct(Product product) throws URISyntaxException{
		long productId = dao.create(product);
		return Response
				.ok(product)
				.build();
	}
	
	/*@POST
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path("/checkout")
	public Response checkOutInventory (ProductQuantityMap productMap) {
		for(Long key: productMap.getMap().keySet()) {
			LOG.info(key + " : " + productMap.getMap().get(key));
		}
		dao.checkOutInventoryUpdate(productMap);
		return Response
				.ok("{\"success\":\"true\"}")
				.build();
	}*/
	
	@GET
	@Path("/sales_history/{hrs}")
	@UnitOfWork
	public Response getInventorySalesHistory(@PathParam("hrs") int lastNhours) {
		LOG.info("Show sales history of :" + lastNhours +" hours!!!!");
		List<SalesHistory> sh = dao.getSalesHistory(lastNhours);
		return Response
				.ok(sh)
				.build();
	}
	
	@GET
	@Path("trans_id/{transId}")
	@Produces(MediaType.APPLICATION_JSON)
	@UnitOfWork
	public Response getInboundTransactionStatus(@PathParam("transId") String transId){
		LOG.info("Transaction Id is :" + transId);
		String status = dao.getTransactionStatus(transId);
		return Response
				.ok(status)
				.build();
				
	}
	
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Path("/batch")
	public Response addInventoryInBatch(
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail
			) 
			throws IOException {
		LOG.info(String.format("Uploading file: [%s]", fileDetail.getFileName()));
		BatchAddInventoryRequest queueMessage = new BatchAddInventoryRequest();
		String transactionId = UUID.randomUUID().toString();
		queueMessage.setTransactionId(transactionId);
		queueMessage.setBucket(INVENTORY_BATCH_ADD_BUCKET_NAME);
		queueMessage.setKey(transactionId);
		batchAddInventoryDAO.uploadToS3(queueMessage.getBucket(), 
				queueMessage.getKey(), uploadedInputStream);
		batchAddInventoryDAO.queueBatchAddInventoryRequest(queueMessage);
		dao.updateQueueStatus(queueMessage.getTransactionId(), "QUEUED");
		return Response
				.ok(null)
				.build();
	}
	
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Path("/checkout")
	public Response checkOutInventory(
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail
			) 
			throws IOException {
		LOG.info(String.format("Uploading file: [%s]", fileDetail.getFileName()));
		BatchAddInventoryRequest queueMessage = new BatchAddInventoryRequest();
		String transactionId = UUID.randomUUID().toString();
		queueMessage.setTransactionId(transactionId);
		queueMessage.setBucket(INVENTORY_BATCH_CHECKOUT_BUCKET_NAME);
		queueMessage.setKey(transactionId);
		batchCheckoutInventoryDAO.uploadToS3(queueMessage.getBucket(), 
				queueMessage.getKey(), uploadedInputStream);
		batchCheckoutInventoryDAO.queueBatchCheckoutInventoryRequest(queueMessage);
		dao.updateQueueStatus(queueMessage.getTransactionId(), "QUEUED");
		return Response
				.ok("{\"Transaction Id\" :" + transactionId + "}")
				.build();
	}
}
