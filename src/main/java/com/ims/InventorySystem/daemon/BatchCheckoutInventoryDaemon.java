package com.ims.InventorySystem.daemon;

import java.io.IOException;
import java.io.InputStream;

import com.ims.InventorySystem.dao.BatchAddInventoryDAO;
import com.ims.InventorySystem.dao.BatchCheckoutInventoryDAO;
import com.ims.InventorySystem.dao.InventoryDAO;
import com.ims.InventorySystem.representations.BatchAddInventoryRequest;

public class BatchCheckoutInventoryDaemon implements Runnable{
	private static final long SLEEP_TIME_MS = 5 * 1000;

	private final BatchCheckoutInventoryDAO batchCheckoutInventoryDAO;
	private final InventoryDAO inventoryDAO;
	
	public BatchCheckoutInventoryDaemon(InventoryDAO inventoryDAO,
			BatchCheckoutInventoryDAO batchCheckoutInventoryDAO) {
		this.batchCheckoutInventoryDAO = batchCheckoutInventoryDAO;
		this.inventoryDAO = inventoryDAO;
	}

	public void run() {
		while (true) {
			try {
				Thread.sleep(SLEEP_TIME_MS);
			} catch (InterruptedException e) {}

			try {
				BatchAddInventoryRequest request = batchCheckoutInventoryDAO
						.fetchBatchCheckoutInventoryRequest();
				if (request == null) {
					continue;
				}
				inventoryDAO.updateQueueStatus(request.getTransactionId(), "PROCESSING");
				InputStream in = batchCheckoutInventoryDAO
						.getBatchCheckoutInventoryFileStream(request);
				inventoryDAO.batchCheckOutUpdate(request.getTransactionId(), in);
				batchCheckoutInventoryDAO.deleteFromQueueBatchCheckoutInventoryRequest(
						request);
			} catch (IOException e) {
				// TODO: Add logger
			}
		}
	}
}
