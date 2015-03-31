package com.ims.InventorySystem.daemon;

import java.io.IOException;
import java.io.InputStream;

import com.ims.InventorySystem.dao.BatchAddInventoryDAO;
import com.ims.InventorySystem.dao.InventoryDAO;
import com.ims.InventorySystem.representations.BatchAddInventoryRequest;

public class BatchAddInventoryDaemon implements Runnable {

	private static final long SLEEP_TIME_MS = 5 * 1000;

	private final BatchAddInventoryDAO batchAddInventoryDAO;
	private final InventoryDAO inventoryDAO;
	
	public BatchAddInventoryDaemon(InventoryDAO inventoryDAO,
			BatchAddInventoryDAO batchAddInventoryDAO) {
		this.batchAddInventoryDAO = batchAddInventoryDAO;
		this.inventoryDAO = inventoryDAO;
	}

	public void run() {
		while (true) {
			try {
				Thread.sleep(SLEEP_TIME_MS);
			} catch (InterruptedException e) {}

			try {
				BatchAddInventoryRequest request = batchAddInventoryDAO
						.fetchBatchAddInventoryRequest();
				if (request == null) {
					continue;
				}
				inventoryDAO.updateQueueStatus(request.getTransactionId(), "PROCESSING");
				InputStream in = batchAddInventoryDAO
						.getBatchAddInventoryFileStream(request);
				inventoryDAO.batchUpdate(request.getTransactionId(), in);
				batchAddInventoryDAO.deleteFromQueueBatchAddInventoryRequest(
						request);
			} catch (IOException e) {
				// TODO: Add logger
			}
		}
	}
}
