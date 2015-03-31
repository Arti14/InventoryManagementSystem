package com.ims.InventorySystem.dao;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List; 
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import com.ims.InventorySystem.representations.InboundTransactionStatus;
import com.ims.InventorySystem.representations.Product;
import com.ims.InventorySystem.representations.ProductQuantityMap;
import com.ims.InventorySystem.representations.SalesHistory;
import com.ims.InventorySystem.representations.TransactionCompositeId;

import io.dropwizard.hibernate.AbstractDAO;

public class InventoryDAO extends AbstractDAO<Product> implements Serializable{
	private SessionFactory factory;
	private static final Logger LOG = Logger.getLogger(InventoryDAO.class);

	public InventoryDAO(SessionFactory factory) {
		super(factory);
		this.factory = factory;
	}

	public Product findById(Long id) {
		//LOG.info("DAO class called findById()");
		Session session = factory.openSession();
		Transaction tx = null;
		Product p = null;
		try{
			tx = session.beginTransaction();
			String hql = "FROM Product E WHERE E.productId = :id";
			Query query = session.createQuery(hql);
			query.setParameter("id",id);
			p = (Product) query.list().get(0);
			tx.commit();
		} catch (HibernateException e) {
			if (tx!=null) tx.rollback();
			e.printStackTrace(); 
		}finally {
			session.close(); 
		}
		return p;
	}

	public long create(Product product) {
		Session session = factory.openSession();
		Transaction tx = null;
		long productId = 0;
		try {
			tx = session.beginTransaction();
			Serializable result = session.save(product);
			Long r = (Long)result;
			productId = r.longValue();
			tx.commit();
		} catch (HibernateException e) {
			if(tx != null) tx.rollback();
			LOG.error(e.getMessage(), e);
		} finally {
			session.close();
		}
		return productId;
	}

	public void checkOutInventoryUpdate(ProductQuantityMap productMap) {
		Session session = factory.openSession();
		Transaction tx = null;
		String uuid = UUID.randomUUID().toString();
		try {
			tx = session.beginTransaction();
			for(Long key: productMap.getMap().keySet()) {
				Long productId = key;
				int quantity = productMap.getMap().get(key);
				String hql = "UPDATE Product set quantity = quantity - :quantity "  + 
						"WHERE id = :id and (quantity - :quantity) >= 0";
				Query query = session.createQuery(hql);
				query.setParameter("id", productId);
				query.setParameter("quantity", quantity);
				int result = query.executeUpdate();
				LOG.info("Rows affected in inventory_detail : " + result);
				if(result > 0) {
					SalesHistory sh = new SalesHistory();
					TransactionCompositeId tid = new TransactionCompositeId();
					tid.setTransId(uuid);
					tid.setProductId(productId);
					sh.setId(tid);
					sh.setQuantity(quantity);
					sh.setTransType("outbound");
					TransactionCompositeId result1 = (TransactionCompositeId) 
							session.save(sh);
					LOG.info("Rows affected in inventory_transaction: " + result1.getTransId());
				}
			}
			tx.commit();
		} catch (Exception e) {
			if(tx != null) tx.rollback();
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			session.close();
		}
	}

	public List<SalesHistory> getSalesHistory(int lastNhours) {
		Session session = factory.openSession();
		Date dt = new Date();
		Date dateBefore = new Date(dt.getTime() - lastNhours * 3600 * 1000l );
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formatted = format1.format(dateBefore.getTime());
		LOG.info("dateBefore : " + formatted);
		Transaction tx = null;
		SalesHistory sh = null;
		List<SalesHistory> shList = new ArrayList<SalesHistory>();
		try {
			tx = session.beginTransaction();
			Criteria cr = session.createCriteria(SalesHistory.class);
			cr.add(Restrictions.gt("createdAt", formatted));
			cr.add(Restrictions.eq("transType", "outbound"));
			for (Object o : cr.list()) {
				sh = (SalesHistory) o;
				shList.add(sh);
			}
			tx.commit();
		}catch (HibernateException e) {
			if (tx!=null) tx.rollback();
			e.printStackTrace(); 
		}finally {
			session.close(); 
		}
		return shList;
	}
	
	public void updateQueueStatus(String transId, String status) {
		Session session = factory.openSession();
		Transaction tx = null;
		InboundTransactionStatus inTransStatus = new InboundTransactionStatus(transId, status);
		String transactionId = null;
		try {
			tx = session.beginTransaction();
			if(inTransStatus.getStatus() == "QUEUED") {
				//create new row with status as "queued"
				Serializable result = session.save(inTransStatus);
				transactionId = (String)result;
				LOG.info("New inbound transaction queued with Id: " + transactionId);
			} else {
				//update DB with new status
				String hql = "Update InboundTransactionStatus set status = :st "
						+ "WHERE transId = :tId";
				Query query = session.createQuery(hql);
				query.setParameter("st", status);
				query.setParameter("tId", transId);
				int result = query.executeUpdate();
				LOG.info("Rows affected in inbound_trasaction_status : " + result);
			}
			tx.commit();
		} catch (HibernateException e) {
			if(tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}
				
	}
	
	public String getTransactionStatus(String transId) {
		Session session = factory.openSession();
		Transaction tx = null;
		String status = null;
		try {
			tx = session.beginTransaction();
			String hql = "Select status FROM InboundTransactionStatus where transId = :tId";
			Query query = session.createQuery(hql);
			query.setParameter("tId", transId);
			status = (String)query.list().get(0);
			tx.commit();
		} catch (HibernateException e) {
			if(tx != null) {
				tx.rollback();
			}
			LOG.error(e); 
			throw e;
		} catch (Exception e) {
			LOG.error(e);
		} finally {
			session.close();
		}
		LOG.info("status ::::: " + status);
		return status;
	}
	
	public void batchUpdate(final String transactionId,
			final InputStream inputStream) {
		Session session = factory.openSession();
		Transaction tx = null;
		BufferedReader bReader = null;
		try {
			tx = session.beginTransaction();
			InputStreamReader isr = new InputStreamReader(inputStream);
			bReader = new BufferedReader(isr);
			String fileValues;
			int count = 0;
			int totalCount = 0;
			int updatedCount = 0;
			while ((fileValues = bReader.readLine()) != null)
			{
				++totalCount;
				
				fileValues = fileValues.trim();
				if (fileValues.length() == 0) {
					continue;
				}
				String[] values = fileValues.split(",");
				if (values.length != 2) {
					continue;
				}
				
				long productId = 0;
				int quantity = 0;
				try {
					productId = Long.parseLong(values[0].trim());
					quantity =  Integer.parseInt(values[1].trim());
				} catch (NumberFormatException nfe) {
					LOG.error(nfe.getMessage(), nfe);
					continue;
				}
				
				if(quantity > 0) {

					LOG.info(productId + " :::: " + quantity ); 
					TransactionCompositeId tCompId = new TransactionCompositeId(transactionId, productId);
					String hql = "UPDATE Product as p "
							+ "set p.quantity = p.quantity + :quantity " 
							+ "WHERE p.productId = :pId "
							+ "and p.productId NOT IN (SELECT s.id.productId from SalesHistory s WHERE "
							+ "s.id = :id)";
					Query query = session.createQuery(hql);
					query.setParameter("pId", productId);
					query.setParameter("quantity", quantity);
					query.setParameter("id", tCompId);
					int result = query.executeUpdate();
					LOG.info("Rows affected in inventory_detail : " + result);
					if ( ++count % 10 == 0 ) {  
						session.flush();  
						session.clear();  
						count = 0;
					}
					if(result > 0) {
						updatedCount++;
						SalesHistory sh = new SalesHistory();
						TransactionCompositeId tid = new TransactionCompositeId();
						tid.setTransId(transactionId);
						tid.setProductId(productId);
						sh.setId(tid);
						sh.setQuantity(quantity);
						sh.setTransType("inbound");
						TransactionCompositeId result1 = (TransactionCompositeId) 
								session.save(sh);
						LOG.info("Transaction Id in inventory_transaction: " + result1.getTransId());
					}
				} 
			}
			String hql = "UPDATE InboundTransactionStatus as i "
							+ "set i.status = :status " 
							+ "WHERE (i.status NOT LIKE '%SUCCESS%' OR "
							+ "i.status NOT LIKE '%PARTIAL_SUCCESS%' OR "
							+ "i.status NOT LIKE '%FAILED%') AND "
							+ "i.transId = :transId";
			Query query = session.createQuery(hql);
			query.setParameter("transId", transactionId);
			if (updatedCount == totalCount) {
				query.setParameter("status", "SUCCESS");
			} else if (updatedCount == 0) {
				query.setParameter("status", "FAILED");
			} else {
				query.setParameter("status", "PARTIAL_SUCCESS");
			}
			query.executeUpdate();
			tx.commit();
			bReader.close();
		}catch (IOException e) {
			LOG.error("File Read Error", e);
		} catch (HibernateException e) {
			if (tx!=null) tx.rollback();
			LOG.error(e); 
		} catch (Exception e) {
			LOG.error(e);
		} finally {
			session.close();
		}

	}
}
